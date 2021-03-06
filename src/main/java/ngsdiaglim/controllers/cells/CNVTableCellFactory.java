package ngsdiaglim.controllers.cells;

import javafx.application.Platform;
import javafx.scene.control.TableCell;
import ngsdiaglim.App;
import ngsdiaglim.cnv.CovCopRegion;
import ngsdiaglim.controllers.analysisview.cnv.CNVNormalizedTableViewController;
import ngsdiaglim.enumerations.CNVTypes;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.stats.ZTest;

public class CNVTableCellFactory extends TableCell<CovCopRegion, Double> {

    private final String[] cssClasses = new String[]{"delCell", "delCellLight", "dupCell", "dupCellLight"};
    private final boolean autoModeEnabled = Boolean.parseBoolean(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_AUTO_DETECTION));
    private final double delThreshold = Double.parseDouble(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_DEL_THRESHOLD));
    private final double dupThreshold = Double.parseDouble(App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.CNV_DUP_THRESHOLD));

    @Override
    protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);

        // calling super here is very important - don't skip this!
        getStyleClass().removeAll(cssClasses);
        getStyleClass().add("allCells");
        setGraphic(null);
        if (item == null || empty) {
            setText("");
            getStyleClass().add("allCells");
        } else {
            setText(String.format(String.valueOf(item), "%.2f"));
            Platform.runLater(() -> {

                if (autoModeEnabled) {
                    if (getTableRow() != null) {
                        CovCopRegion amplicon = getTableRow().getItem();
                        if (amplicon != null) {
                            int sampleIndex = getTableView().getColumns().indexOf(getTableColumn()) - CNVNormalizedTableViewController.noSampleColumnsNumber;
                            if (sampleIndex >= 0) {
                                Double zScore = amplicon.getzScores().get(sampleIndex);
                                CNVTypes cnvType = ZTest.getCNVType(zScore, 0.01);
                                if (cnvType.equals(CNVTypes.DELETION)) {
                                    getStyleClass().add("delCell");
                                } else if (cnvType.equals(CNVTypes.DUPLICATION)) {
                                    getStyleClass().add("dupCell");
                                }
                            }
                        }
                    }
                } else {
                    if (item <= delThreshold) {
                        getStyleClass().add("delCell");
                    } else if (item <= (delThreshold + 0.1) &&
                            hasValidNeighbours(this, CNVTypes.DELETION, delThreshold)) {
                        getStyleClass().add("delCellLight");
                    } else if (item >= dupThreshold) {
                        getStyleClass().add("dupCell");
                    } else if (item >= dupThreshold - 0.1 &&
                            hasValidNeighbours(this, CNVTypes.DUPLICATION, dupThreshold)) {
                        getStyleClass().add("dupCellLight");
                    }
                }
            });
        }

    }


    private Boolean hasValidNeighbours(TableCell<CovCopRegion, Double> cell, CNVTypes t, double threshold) {
//        return false;
        double margin = 0.1;
        int validNeighboursMax = 2;
        int validNeighboursCount = 0;
        int rowId = cell.getIndex();
        int colID = cell.getTableView().getColumns().indexOf(cell.getTableColumn()) - CNVNormalizedTableViewController.noSampleColumnsNumber;
        if (colID >= 0) {
            if (t.equals(CNVTypes.DELETION)) {
                double delThreshold = threshold + margin;

                int index = rowId - 1;
                while (index >= 0 && validNeighboursCount < validNeighboursMax
                        && cell.getTableView().getItems().get(index).getNormalized_values().get(colID) <= delThreshold) {
                    validNeighboursCount++;
                    index--;
                }

                index = rowId + 1;
                while (index >= 0 && index < cell.getTableView().getItems().size() && validNeighboursCount < validNeighboursMax
                        && cell.getTableView().getItems().get(index).getNormalized_values().get(colID) <= delThreshold) {
                    validNeighboursCount++;
                    index++;
                }
            } else if (t.equals(CNVTypes.DUPLICATION)) {
                double dupThreshold = threshold - margin;

                int index = rowId - 1;
                while (index >= 0 && validNeighboursCount < validNeighboursMax
                        && cell.getTableView().getItems().get(index).getNormalized_values().get(colID) >= dupThreshold) {
                    validNeighboursCount++;
                    index--;
                }

                index = rowId + 1;
                while (index >= 0 && index < cell.getTableView().getItems().size() && validNeighboursCount < validNeighboursMax
                        && cell.getTableView().getItems().get(index).getNormalized_values().get(colID) >= dupThreshold) {
                    validNeighboursCount++;
                    index++;
                }
            }
        }
        return validNeighboursCount >= validNeighboursMax;
    }
}
