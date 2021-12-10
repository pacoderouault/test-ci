package ngsdiaglim.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.comparators.NaturalSortComparator;
import ngsdiaglim.comparators.SiftPredictionComparator;
import ngsdiaglim.comparators.SpliceAIComparators;
import ngsdiaglim.comparators.VariantPredictionComparator;
import ngsdiaglim.controllers.cells.variantsTableCells.*;
import ngsdiaglim.controllers.ui.popupfilters.*;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.EnsemblConsequence;
import ngsdiaglim.enumerations.GnomadPopulation;
import ngsdiaglim.enumerations.VariantsTableColumns;
import ngsdiaglim.modeles.analyse.ExternalVariation;
import ngsdiaglim.modeles.biofeatures.Transcript;
import ngsdiaglim.enumerations.ACMG;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.users.UserVariantTableColumns;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.Hotspot;
import ngsdiaglim.modeles.variants.populations.GnomadPopulationFreq;
import ngsdiaglim.modeles.variants.predictions.SpliceAIPredictions;
import ngsdiaglim.modeles.variants.predictions.VariantPrediction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.TableColumn2;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.util.*;

public class VariantTableBuilderOrg {

    private final Logger logger = LogManager.getLogger(VariantTableBuilderOrg.class);

    private final FilteredTableView<Annotation> table;
    private final static NaturalSortComparator naturalSortComparator = new NaturalSortComparator();
    private final static VariantPredictionComparator variantPredictionComparator = new VariantPredictionComparator();
    private final static SpliceAIComparators spliceAIComparator = new SpliceAIComparators();

    private final HashMap<VariantsTableColumns, TableColumn2<Annotation, ?>> columnsMap = new HashMap<>();
    private final HashMap<VariantsTableColumns, Boolean> defaultVisibleColumns = new HashMap<>();
    private final HashMap<VariantsTableColumns, Double> defaultColumnsSize = new HashMap<>();
    private final List<VariantsTableColumns> defaultColumnsOrder = new ArrayList<>();
    private final List<TableColumn2<Annotation, ?>> columns = new ArrayList<>();
    private UserVariantTableColumns userVariantTableColumns;

    public VariantTableBuilderOrg(FilteredTableView<Annotation> table) {
        this.table = table;
    }

    public void buildTable() throws SQLException {

        User loggedUser = App.get().getLoggedUser();
        userVariantTableColumns = DAOController.get().getUserVariantTableColumnsDAO().getUsersVariantTableColumn(loggedUser.getId());
        initColumnsMap();
        setDefaultColumnsOrder();
        setDefaultVisibleColumns();
        setColmunsOrder();
        setDefaultColumnsSize();
        table.getColumns().setAll(columns);
        Platform.runLater(() -> {
            table.getColumns().addListener((ListChangeListener<TableColumn<?, ?>>) change -> {
                try {
                    saveColumnsOrder();
                } catch (SQLException e) {
                    logger.error(e);
                }
            });
            table.getVisibleLeafColumns().addListener((ListChangeListener<TableColumn<Annotation, ?>>) c -> {
                try {
                    saveColumnsVisible();
                } catch (SQLException e) {
                    logger.error(e);
                }
            });
            addColumnResizeListerer();
        });
    }

    public TableColumn2<Annotation, ?> getColumn(VariantsTableColumns variantsTableColumns) {
        return columnsMap.get(variantsTableColumns);
    }

    public List<VariantsTableColumns> getDefaultColumnsOrder() {return defaultColumnsOrder;}

    private void initColumnsMap() {

        columnsMap.clear();

//        TableColumn2<Annotation, Hotspot> hotspotColumn = new TableColumn2<>(VariantsTableColumns.HOTSPOT.getName());
//        hotspotColumn.setCellValueFactory(data -> data.getValue().getVariant().hotspotProperty());
//        hotspotColumn.setCellFactory(data -> new HotspotTableCell());
//        columnsMap.put(VariantsTableColumns.HOTSPOT, hotspotColumn);

        FilteredTableColumn<Annotation, String> contigColumn = createStringFilteredColumn(VariantsTableColumns.CONTIG);
        contigColumn.setCellValueFactory(data -> data.getValue().getVariant().contigProperty());
        columnsMap.put(VariantsTableColumns.CONTIG, contigColumn);

        FilteredTableColumn<Annotation, Number> posColumn = createNumberFilteredColumn(VariantsTableColumns.POSITION);
        posColumn.setCellValueFactory(data -> data.getValue().getVariant().startProperty());
        columnsMap.put(VariantsTableColumns.POSITION, posColumn);

        TableColumn2<Annotation, String> refColumn = createStringFilteredColumn(VariantsTableColumns.REF);
        refColumn.setCellValueFactory(data -> data.getValue().getVariant().refProperty());
        columnsMap.put(VariantsTableColumns.REF, refColumn);

        TableColumn2<Annotation, String> altColumn = createStringFilteredColumn(VariantsTableColumns.ALT);
        altColumn.setCellValueFactory(data -> data.getValue().getVariant().altProperty());
        columnsMap.put(VariantsTableColumns.ALT, altColumn);

        FilteredTableColumn<Annotation, ACMG> acmgColumn2 = new FilteredTableColumn<>();
        acmgColumn2.setCellValueFactory(data -> data.getValue().getVariant().acmgProperty());
        acmgColumn2.setCellFactory(data -> new ACMGTableCell());
//        ACMGPopupFilter acmgPopupfilter = new ACMGPopupFilter(acmgColumn2);
//        FontIcon acmgColumn2Graphic = new FontIcon("mdal-filter_alt");
//        Label acmgColumn2Lb = new Label(VariantsTableColumns.ACMG.getName());
//        acmgColumn2.predicateProperty().addListener((obs, oldV, newV) -> {
//            if (newV != null) {
//                acmgColumn2Lb.setGraphic(acmgColumn2Graphic);
//            }
//            else {
//                acmgColumn2Lb.setGraphic(null);
//            }
//        });
//        acmgColumn2Lb.setOnMouseClicked(e -> {
//            if (e.getButton().equals(MouseButton.SECONDARY)) {
//                acmgPopupfilter.showPopup();
//                e.consume();
//            }
//        });
//        acmgColumn2.setGraphic(acmgColumn2Lb);
        columnsMap.put(VariantsTableColumns.ACMG, acmgColumn2);

        TableColumn2<Annotation, Number> occurrenceColumn = createNumberFilteredColumn(VariantsTableColumns.OCCURENCE);
        occurrenceColumn.setCellValueFactory(data -> data.getValue().getVariant().occurrenceProperty());
        columnsMap.put(VariantsTableColumns.OCCURENCE, occurrenceColumn);

        TableColumn2<Annotation, Number> occurrenceRunColumn = createNumberFilteredColumn(VariantsTableColumns.OCCURENCE_IN_RUN);
        occurrenceRunColumn.setCellValueFactory(data -> data.getValue().getVariant().occurrenceInRunProperty());
        columnsMap.put(VariantsTableColumns.OCCURENCE_IN_RUN, occurrenceRunColumn);

        TableColumn2<Annotation, Number> depthColumn = createNumberFilteredColumn(VariantsTableColumns.DEPTH);
        depthColumn.setCellValueFactory(data -> data.getValue().depthProperty());
        columnsMap.put(VariantsTableColumns.DEPTH, depthColumn);

        TableColumn2<Annotation, Number> vafColumn = createNumberFilteredColumn(VariantsTableColumns.VAF);
        vafColumn.setCellValueFactory(data -> data.getValue().vafProperty());
        columnsMap.put(VariantsTableColumns.VAF, vafColumn);

        TableColumn2<Annotation, String> alleleDepthColumn = new TableColumn2<>(VariantsTableColumns.ALLELE_DEPTH.getName());
        alleleDepthColumn.setCellValueFactory(data -> data.getValue().allelesDepthProperty());
        columnsMap.put(VariantsTableColumns.ALLELE_DEPTH, alleleDepthColumn);

        TableColumn2<Annotation, String> alleleStrandDepthColumn = new TableColumn2<>(VariantsTableColumns.ALLELE_STRAND_DEPTH.getName());
        alleleStrandDepthColumn.setCellValueFactory(data -> data.getValue().allelesStrandDepthProperty());
        columnsMap.put(VariantsTableColumns.ALLELE_STRAND_DEPTH, alleleStrandDepthColumn);

        FilteredTableColumn<Annotation, String> genesColumn = createStringFilteredColumn(VariantsTableColumns.GENES);
        genesColumn.setCellValueFactory(data -> data.getValue().geneNamesProperty());
        columnsMap.put(VariantsTableColumns.GENES, genesColumn);

        TableColumn2<Annotation, String> geneColumn = createStringFilteredColumn(VariantsTableColumns.GENE);
        geneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTranscriptConsequence().getGeneName()));
        columnsMap.put(VariantsTableColumns.GENE, geneColumn);

        TableColumn2<Annotation, String> proteinColumn = createStringFilteredColumn(VariantsTableColumns.PROTEIN);
        proteinColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().proteinNameProperty());
        columnsMap.put(VariantsTableColumns.PROTEIN, proteinColumn);

        TableColumn2<Annotation, Transcript> transcriptColumn = new TableColumn2<>(VariantsTableColumns.TRANSCRIPT.getName());
        transcriptColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().transcriptProperty());
        transcriptColumn.setCellFactory(data -> new TranscriptTableCell());
        columnsMap.put(VariantsTableColumns.TRANSCRIPT, transcriptColumn);

        TableColumn2<Annotation, String> exonColumn = new TableColumn2<>(VariantsTableColumns.EXON.getName());
        exonColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().exonProperty());
        columnsMap.put(VariantsTableColumns.EXON, exonColumn);

        TableColumn2<Annotation, String> intronColumn = new TableColumn2<>(VariantsTableColumns.INTRON.getName());
        intronColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().intronProperty());
        columnsMap.put(VariantsTableColumns.INTRON, intronColumn);

        TableColumn2<Annotation, String> hgvscColumn = createStringFilteredColumn(VariantsTableColumns.HGVSC);
        hgvscColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().hgvscProperty());
        columnsMap.put(VariantsTableColumns.HGVSC, hgvscColumn);

        TableColumn2<Annotation, String> hgvspColumn = createStringFilteredColumn(VariantsTableColumns.HGVSP);
        hgvspColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().hgvspProperty());
        columnsMap.put(VariantsTableColumns.HGVSP, hgvspColumn);

        FilteredTableColumn<Annotation, EnsemblConsequence> consequenceColumn = new FilteredTableColumn<>();
        consequenceColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().consequenceProperty());
        consequenceColumn.setCellFactory(data -> new EnsemblConsequenceTableCell<>());
        ConsequencePopupFilter consPopupfilter = new ConsequencePopupFilter(consequenceColumn);
        FontIcon consGraphic = new FontIcon("mdal-filter_alt");
        Label consLabel = new Label(VariantsTableColumns.CONSEQUENCE.getName());
        consequenceColumn.predicateProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                consLabel.setGraphic(consGraphic);
            }
            else {
                consLabel.setGraphic(null);
            }
        });
        consLabel.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.SECONDARY)) {
                consPopupfilter.showPopup();
                e.consume();
            }
        });
        consequenceColumn.setGraphic(consLabel);
        columnsMap.put(VariantsTableColumns.CONSEQUENCE, consequenceColumn);

        TableColumn2<Annotation, String> clinvarSignColumn = createStringFilteredColumn(VariantsTableColumns.CLIVAR_SIGN);
        clinvarSignColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().clinvarSignProperty());
        columnsMap.put(VariantsTableColumns.CLIVAR_SIGN, clinvarSignColumn);

        TableColumn2<Annotation, VariantPrediction> polyphen2HvarColumn = createPredictionFilteredColumn(VariantsTableColumns.POLYPHEN2_HVAR);
        polyphen2HvarColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().polyphen2HvarPredProperty());
        polyphen2HvarColumn.setCellFactory(data -> new VariantPredictionTableCell());
        polyphen2HvarColumn.setComparator(variantPredictionComparator);
        columnsMap.put(VariantsTableColumns.POLYPHEN2_HVAR, polyphen2HvarColumn);

        TableColumn2<Annotation, VariantPrediction> polyphen2HdivColumn = createPredictionFilteredColumn(VariantsTableColumns.POLYPHEN2_HDIV);
        polyphen2HdivColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().polyphen2HdivPredProperty());
        polyphen2HdivColumn.setCellFactory(data -> new VariantPredictionTableCell());
        polyphen2HdivColumn.setComparator(variantPredictionComparator);
        columnsMap.put(VariantsTableColumns.POLYPHEN2_HDIV, polyphen2HdivColumn);

        TableColumn2<Annotation, VariantPrediction> siftColumn = createPredictionFilteredColumn(VariantsTableColumns.SIFT);
        siftColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().siftPredProperty());
        siftColumn.setCellFactory(data -> new VariantPredictionTableCell());
        siftColumn.setComparator(new SiftPredictionComparator());
        columnsMap.put(VariantsTableColumns.SIFT, siftColumn);

        TableColumn2<Annotation, VariantPrediction> caddColumn = createPredictionFilteredColumn(VariantsTableColumns.CADD);
        caddColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().caddPhredPredProperty());
        caddColumn.setCellFactory(data -> new VariantPredictionTableCell());
        caddColumn.setComparator(variantPredictionComparator);
        columnsMap.put(VariantsTableColumns.CADD, caddColumn);

        TableColumn2<Annotation, VariantPrediction> gerpColumn = createPredictionFilteredColumn(VariantsTableColumns.GERP);
        gerpColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().gerpPredProperty());
        gerpColumn.setCellFactory(data -> new VariantPredictionTableCell());
        gerpColumn.setComparator(variantPredictionComparator);
        columnsMap.put(VariantsTableColumns.GERP, gerpColumn);

        TableColumn2<Annotation, VariantPrediction> revelColumn = createPredictionFilteredColumn(VariantsTableColumns.REVEL);
        revelColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().revelPredProperty());
        revelColumn.setCellFactory(data -> new VariantPredictionTableCell());
        revelColumn.setComparator(variantPredictionComparator);
        columnsMap.put(VariantsTableColumns.REVEL, revelColumn);

        TableColumn2<Annotation, VariantPrediction> mvpColumn = createPredictionFilteredColumn(VariantsTableColumns.MVP);
        mvpColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().mvpPredProperty());
        mvpColumn.setCellFactory(data -> new VariantPredictionTableCell());
        mvpColumn.setComparator(variantPredictionComparator);
        columnsMap.put(VariantsTableColumns.MVP, mvpColumn);

        TableColumn2<Annotation, SpliceAIPredictions> spliceAIColumn = new TableColumn2<>(VariantsTableColumns.SPLICE_AI.getName());
        spliceAIColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().spliceAIPredsProperty());
        spliceAIColumn.setCellFactory(data -> new SpliceAIPredsTableCell());
        spliceAIColumn.setComparator(spliceAIComparator);
        columnsMap.put(VariantsTableColumns.SPLICE_AI, spliceAIColumn);

//        TableColumn2<Annotation, GnomadPopulationFreq> gnomadMaxColumn = createNumberFilteredColumn(VariantsTableColumns.GNOMAD_MAX);
//        gnomadMaxColumn.setCellValueFactory(data -> data.getValue().getTranscriptConsequence().getAnnotation().getGnomADFrequencies().maxProperty());
//        gnomadMaxColumn.setCellFactory(data -> new PopulationFrequencyTableCell());
//        columnsMap.put(VariantsTableColumns.GNOMAD_MAX, gnomadMaxColumn);

        TableColumn2<Annotation, List<ExternalVariation>> externalVariationsColumn = new TableColumn2<>(VariantsTableColumns.EXTERNAL_VARIATIONS.getName());
        externalVariationsColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTranscriptConsequence().getExternalVariations()));
        externalVariationsColumn.setCellFactory(data -> new ExternalVariationsTableCell<>());
        columnsMap.put(VariantsTableColumns.EXTERNAL_VARIATIONS, externalVariationsColumn);
    }


    private FilteredTableColumn<Annotation, Number> createNumberFilteredColumn(VariantsTableColumns variantsTableColumns) {
        FilteredTableColumn<Annotation, Number> col = new FilteredTableColumn<>();
        NumberPopupFilter popupfilter = new NumberPopupFilter(col);
        FontIcon graphic = new FontIcon("mdal-filter_alt");
        Label label = new Label(variantsTableColumns.getName());
        col.predicateProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                label.setGraphic(graphic);
            }
            else {
                label.setGraphic(null);
            }
        });
        label.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.SECONDARY)) {
                popupfilter.showPopup();
                e.consume();
            }
        });
        if (variantsTableColumns.getDesc() != null) {
            Tooltip tp = new Tooltip(variantsTableColumns.getDesc());
            tp.setShowDuration(Duration.ZERO);
            label.setTooltip(tp);
        }
        col.setGraphic(label);
        return col;
    }


    private FilteredTableColumn<Annotation, String> createStringFilteredColumn(VariantsTableColumns variantsTableColumns) {
        FilteredTableColumn<Annotation, String> col = new FilteredTableColumn<>();
        col.setComparator(naturalSortComparator);
        StringPopupFilter popupfilter = new StringPopupFilter(col);
        FontIcon graphic = new FontIcon("mdal-filter_alt");
        Label label = new Label(variantsTableColumns.getName());
        col.predicateProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                label.setGraphic(graphic);
            }
            else {
                label.setGraphic(null);
            }
        });
        label.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.SECONDARY)) {
                popupfilter.showPopup();
                e.consume();
            }
        });
        if (variantsTableColumns.getDesc() != null) {
            Tooltip tp = new Tooltip(variantsTableColumns.getDesc());
            tp.setShowDuration(Duration.ZERO);
            label.setTooltip(tp);
        }
        col.setGraphic(label);
        return col;
    }


    private FilteredTableColumn<Annotation, VariantPrediction> createPredictionFilteredColumn(VariantsTableColumns variantsTableColumns) {
        FilteredTableColumn<Annotation, VariantPrediction> col = new FilteredTableColumn<>();
        PredictionPopupFilter popupfilter = new PredictionPopupFilter(col);
        FontIcon graphic = new FontIcon("mdal-filter_alt");
        Label label = new Label(variantsTableColumns.getName());
        col.predicateProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                label.setGraphic(graphic);
            }
            else {
                label.setGraphic(null);
            }
        });
        label.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.SECONDARY)) {
                popupfilter.showPopup();
                e.consume();
            }
        });
        col.setGraphic(label);
        return col;
    }

    private void setDefaultColumnsOrder() {
        defaultColumnsOrder.add(VariantsTableColumns.HOTSPOT);
        defaultColumnsOrder.add(VariantsTableColumns.CONTIG);
        defaultColumnsOrder.add(VariantsTableColumns.POSITION);
        defaultColumnsOrder.add(VariantsTableColumns.REF);
        defaultColumnsOrder.add(VariantsTableColumns.ALT);
        defaultColumnsOrder.add(VariantsTableColumns.GENES);
        defaultColumnsOrder.add(VariantsTableColumns.TRANSCRIPT);
        defaultColumnsOrder.add(VariantsTableColumns.GENE);
        defaultColumnsOrder.add(VariantsTableColumns.CONSEQUENCE);
        defaultColumnsOrder.add(VariantsTableColumns.HGVSC);
        defaultColumnsOrder.add(VariantsTableColumns.HGVSP);
        defaultColumnsOrder.add(VariantsTableColumns.ACMG);
        defaultColumnsOrder.add(VariantsTableColumns.OCCURENCE);
        defaultColumnsOrder.add(VariantsTableColumns.OCCURENCE_IN_RUN);
        defaultColumnsOrder.add(VariantsTableColumns.EXTERNAL_VARIATIONS);
        defaultColumnsOrder.add(VariantsTableColumns.DEPTH);
        defaultColumnsOrder.add(VariantsTableColumns.VAF);
        defaultColumnsOrder.add(VariantsTableColumns.ALLELE_DEPTH);
        defaultColumnsOrder.add(VariantsTableColumns.ALLELE_STRAND_DEPTH);
        defaultColumnsOrder.add(VariantsTableColumns.EXON);
        defaultColumnsOrder.add(VariantsTableColumns.INTRON);
        defaultColumnsOrder.add(VariantsTableColumns.PROTEIN);
        defaultColumnsOrder.add(VariantsTableColumns.CLIVAR_SIGN);
        defaultColumnsOrder.add(VariantsTableColumns.GNOMAD_MAX);
        defaultColumnsOrder.add(VariantsTableColumns.POLYPHEN2_HVAR);
        defaultColumnsOrder.add(VariantsTableColumns.POLYPHEN2_HDIV);
        defaultColumnsOrder.add(VariantsTableColumns.SIFT);
        defaultColumnsOrder.add(VariantsTableColumns.CADD);
        defaultColumnsOrder.add(VariantsTableColumns.GERP);
        defaultColumnsOrder.add(VariantsTableColumns.REVEL);
        defaultColumnsOrder.add(VariantsTableColumns.MVP);
        defaultColumnsOrder.add(VariantsTableColumns.SPLICE_AI);
    }


    private void setColmunsOrder() {
        columns.clear();
        if (userVariantTableColumns == null) {
            defaultColumnsOrder.forEach(c -> {
                if (columnsMap.containsKey(c)) {
                    columns.add(columnsMap.get(c));
                }
            });
        } else {
            userVariantTableColumns.getColumnsOrder().forEach(c -> {
                if (columnsMap.containsKey(c)) {
                    columns.add(columnsMap.get(c));
                }
            });
            defaultColumnsOrder.forEach(c -> {
                if (columnsMap.containsKey(c) && !columns.contains(columnsMap.get(c))) {
                    columns.add(columnsMap.get(c));
                }
            });
        }
    }

    private void setDefaultVisibleColumns() throws SQLException {
        defaultVisibleColumns.put(VariantsTableColumns.HOTSPOT, true);
        defaultVisibleColumns.put(VariantsTableColumns.CONTIG, true);
        defaultVisibleColumns.put(VariantsTableColumns.POSITION, true);
        defaultVisibleColumns.put(VariantsTableColumns.REF, true);
        defaultVisibleColumns.put(VariantsTableColumns.ALT, true);
        defaultVisibleColumns.put(VariantsTableColumns.ACMG, true);
        defaultVisibleColumns.put(VariantsTableColumns.OCCURENCE, true);
        defaultVisibleColumns.put(VariantsTableColumns.OCCURENCE_IN_RUN, true);
        defaultVisibleColumns.put(VariantsTableColumns.DEPTH, true);
        defaultVisibleColumns.put(VariantsTableColumns.VAF, true);
        defaultVisibleColumns.put(VariantsTableColumns.ALLELE_DEPTH, true);
        defaultVisibleColumns.put(VariantsTableColumns.ALLELE_STRAND_DEPTH, true);
        defaultVisibleColumns.put(VariantsTableColumns.GENES, true);
        defaultVisibleColumns.put(VariantsTableColumns.GENE, true);
        defaultVisibleColumns.put(VariantsTableColumns.PROTEIN, false);
        defaultVisibleColumns.put(VariantsTableColumns.TRANSCRIPT, true);
        defaultVisibleColumns.put(VariantsTableColumns.EXON, false);
        defaultVisibleColumns.put(VariantsTableColumns.INTRON, false);
        defaultVisibleColumns.put(VariantsTableColumns.HGVSC, true);
        defaultVisibleColumns.put(VariantsTableColumns.HGVSP, true);
        defaultVisibleColumns.put(VariantsTableColumns.CONSEQUENCE, true);
        defaultVisibleColumns.put(VariantsTableColumns.CLIVAR_SIGN, true);
        defaultVisibleColumns.put(VariantsTableColumns.POLYPHEN2_HDIV, true);
        defaultVisibleColumns.put(VariantsTableColumns.POLYPHEN2_HVAR, true);
        defaultVisibleColumns.put(VariantsTableColumns.SIFT, true);
        defaultVisibleColumns.put(VariantsTableColumns.CADD, true);
        defaultVisibleColumns.put(VariantsTableColumns.GERP, true);
        defaultVisibleColumns.put(VariantsTableColumns.REVEL, true);
        defaultVisibleColumns.put(VariantsTableColumns.MVP, true);
        defaultVisibleColumns.put(VariantsTableColumns.SPLICE_AI, true);
        defaultVisibleColumns.put(VariantsTableColumns.GNOMAD_MAX, true);
        defaultVisibleColumns.put(VariantsTableColumns.EXTERNAL_VARIATIONS, true);

        User user = App.get().getLoggedUser();
        UserVariantTableColumns userVariantTableColumns = DAOController.get().getUserVariantTableColumnsDAO().getUsersVariantTableColumn(user.getId());
        if (userVariantTableColumns != null) {
            userVariantTableColumns.getColumnsVisibility().forEach((k, v) -> {
               if (defaultVisibleColumns.containsKey(k)) {
                   defaultVisibleColumns.put(k, v);
               }
            });
        }

        defaultVisibleColumns.forEach((k, v) -> {
            if (columnsMap.containsKey(k)) {
                columnsMap.get(k).setVisible(v);
            }
        });
    }

    private void setDefaultColumnsSize() throws SQLException {
        double v = 100;
        defaultColumnsSize.put(VariantsTableColumns.HOTSPOT, v);
        defaultColumnsSize.put(VariantsTableColumns.CONTIG, v);
        defaultColumnsSize.put(VariantsTableColumns.POSITION, v);
        defaultColumnsSize.put(VariantsTableColumns.REF, v);
        defaultColumnsSize.put(VariantsTableColumns.ALT, v);
        defaultColumnsSize.put(VariantsTableColumns.ACMG, v);
        defaultColumnsSize.put(VariantsTableColumns.OCCURENCE, v);
        defaultColumnsSize.put(VariantsTableColumns.OCCURENCE_IN_RUN, v);
        defaultColumnsSize.put(VariantsTableColumns.DEPTH, v);
        defaultColumnsSize.put(VariantsTableColumns.VAF, v);
        defaultColumnsSize.put(VariantsTableColumns.ALLELE_DEPTH, v);
        defaultColumnsSize.put(VariantsTableColumns.ALLELE_STRAND_DEPTH, v);
        defaultColumnsSize.put(VariantsTableColumns.GENES, v);
        defaultColumnsSize.put(VariantsTableColumns.GENE, v);
        defaultColumnsSize.put(VariantsTableColumns.PROTEIN, v);
        defaultColumnsSize.put(VariantsTableColumns.TRANSCRIPT, v);
        defaultColumnsSize.put(VariantsTableColumns.EXON, v);
        defaultColumnsSize.put(VariantsTableColumns.INTRON, v);
        defaultColumnsSize.put(VariantsTableColumns.HGVSC, v);
        defaultColumnsSize.put(VariantsTableColumns.HGVSP, v);
        defaultColumnsSize.put(VariantsTableColumns.CONSEQUENCE, v);
        defaultColumnsSize.put(VariantsTableColumns.CLIVAR_SIGN, v);
        defaultColumnsSize.put(VariantsTableColumns.POLYPHEN2_HDIV, v);
        defaultColumnsSize.put(VariantsTableColumns.POLYPHEN2_HVAR, v);
        defaultColumnsSize.put(VariantsTableColumns.SIFT, v);
        defaultColumnsSize.put(VariantsTableColumns.CADD, v);
        defaultColumnsSize.put(VariantsTableColumns.GERP, v);
        defaultColumnsSize.put(VariantsTableColumns.REVEL, v);
        defaultColumnsSize.put(VariantsTableColumns.MVP, v);
        defaultColumnsSize.put(VariantsTableColumns.SPLICE_AI, v);
        defaultColumnsSize.put(VariantsTableColumns.GNOMAD_MAX, v);
        defaultColumnsSize.put(VariantsTableColumns.EXTERNAL_VARIATIONS, v);

        User user = App.get().getLoggedUser();
        UserVariantTableColumns userVariantTableColumns = DAOController.get().getUserVariantTableColumnsDAO().getUsersVariantTableColumn(user.getId());
        if (userVariantTableColumns != null) {
            userVariantTableColumns.getColumnsSize().forEach((k, v2) -> {
               if (defaultColumnsSize.containsKey(k)) {
                   defaultColumnsSize.put(k, v2);
               }
            });
        }

        defaultColumnsSize.forEach((k, v2) -> {
            if (columnsMap.containsKey(k)) {
                columnsMap.get(k).setPrefWidth(v2);
            }
        });
    }

    private void addColumnResizeListerer() {

        final ChangeListener<Number> listener = new ChangeListener<>() {
            final Timer timer = new Timer(); // uses a timer to call your resize method
            TimerTask task = null; // task to execute after defined delay
            final long delayTime = 500; // delay that has to pass in order to consider an operation done

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
                System.out.println(observable);
                System.out.println(oldValue);
                System.out.println(newValue);
                if (oldValue != null && oldValue.doubleValue() > 0) {
                    if (task != null) { // there was already a task scheduled from the previous operation ...
                        task.cancel(); // cancel it, we have a new size to consider
                    }

                    task = new TimerTask() // create new task that calls your resize operation
                    {
                        @Override
                        public void run() {
                            // here you can place your resize code
                            try {
                                saveColumnsSize();
                            } catch (SQLException e) {
                                logger.error(e);
                            }
                        }
                    };
                    // schedule new task
                    timer.schedule(task, delayTime);
                }
            }
        };


        table.getColumns().forEach(c -> {
            c.widthProperty().addListener(listener);
        });
    }


    private void saveColumnsOrder() throws SQLException {
        User user = App.get().getLoggedUser();
        UserVariantTableColumns userVariantTableColumns = DAOController.get().getUserVariantTableColumnsDAO().getUsersVariantTableColumn(user.getId());
        if (userVariantTableColumns == null) {
            userVariantTableColumns = new UserVariantTableColumns();
            userVariantTableColumns.setColumnsOrderFromTable(table);
            DAOController.get().getUserVariantTableColumnsDAO().addUsersVariantTableColumn(user.getId(), userVariantTableColumns);
        } else {
            userVariantTableColumns.setColumnsOrderFromTable(table);
            DAOController.get().getUserVariantTableColumnsDAO().updateUsersVariantTableColumn(user.getId(), userVariantTableColumns);
        }
    }


    private void saveColumnsVisible() throws SQLException {
        User user = App.get().getLoggedUser();
        UserVariantTableColumns userVariantTableColumns = DAOController.get().getUserVariantTableColumnsDAO().getUsersVariantTableColumn(user.getId());
        if (userVariantTableColumns == null) {
            userVariantTableColumns = new UserVariantTableColumns();
            userVariantTableColumns.setVisibleColumnsFromTable(table);
            DAOController.get().getUserVariantTableColumnsDAO().addUsersVariantTableColumn(user.getId(), userVariantTableColumns);
        } else {
            userVariantTableColumns.setVisibleColumnsFromTable(table);
            DAOController.get().getUserVariantTableColumnsDAO().updateUsersVariantTableColumn(user.getId(), userVariantTableColumns);
        }
    }


    private void saveColumnsSize() throws SQLException {
        System.out.println("SAVE RESIZE");
        User user = App.get().getLoggedUser();
        UserVariantTableColumns userVariantTableColumns = DAOController.get().getUserVariantTableColumnsDAO().getUsersVariantTableColumn(user.getId());
        if (userVariantTableColumns == null) {
            userVariantTableColumns = new UserVariantTableColumns();
            userVariantTableColumns.setColumnsSizeFromTable(table);
            DAOController.get().getUserVariantTableColumnsDAO().addUsersVariantTableColumn(user.getId(), userVariantTableColumns);
        } else {
            userVariantTableColumns.setColumnsSizeFromTable(table);
            DAOController.get().getUserVariantTableColumnsDAO().updateUsersVariantTableColumn(user.getId(), userVariantTableColumns);
        }
    }


}
