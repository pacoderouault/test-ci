package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ngsdiaglim.App;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.utils.DateFormatterUtils;

public class AnalysisInfoDialog extends DialogPane.Dialog<Analysis> {

        private final GridPane gridPane = new GridPane();
        private final Label analysisNameLb = new Label(App.getBundle().getString("analysisinfodialog.lb.name"));
        private final TextField analysisNameTf = new TextField();
        private final Label analysisSampleNameLb = new Label(App.getBundle().getString("analysisinfodialog.lb.samplename"));
        private final TextField analysisSampleNameTf = new TextField();
        private final Label analysisCreationDateLb = new Label(App.getBundle().getString("analysisinfodialog.lb.creationDate"));
        private final TextField analysisCreationDateTf = new TextField();
        private final Label analysisUserCreationLb = new Label(App.getBundle().getString("analysisinfodialog.lb.userCreation"));
        private final TextField analysisUserCreationTf = new TextField();
        private final Label analysisPathLb = new Label(App.getBundle().getString("analysisinfodialog.lb.path"));
        private final TextField analysisPathTf = new TextField();
        private final Label analysisVCFLb = new Label(App.getBundle().getString("analysisinfodialog.lb.vcf"));
        private final TextField analysisVCFTf = new TextField();
        private final Label analysisBAMLb = new Label(App.getBundle().getString("analysisinfodialog.lb.bam"));
        private final TextField analysisBAMTf = new TextField();
        private final Label analysisDepthLb = new Label(App.getBundle().getString("analysisinfodialog.lb.depth"));
        private final TextField analysisDepthTf = new TextField();
        private final Label analysisParemetersNameLb = new Label(App.getBundle().getString("analysisinfodialog.lb.parameters"));
        private final TextField analysisParemetersNameTf = new TextField();
        private final Label analysisParemetersPanelLb = new Label(App.getBundle().getString("analysisinfodialog.lb.parameters.panel"));
        private final TextField analysisParemetersPanelTf = new TextField();
        private final Label analysisParemetersHotspotsLb = new Label(App.getBundle().getString("analysisinfodialog.lb.parameters.hotspots"));
        private final TextField analysisParemetersHotspotsTf = new TextField();
        private final Label analysisParemetersMinVAFLb = new Label(App.getBundle().getString("analysisinfodialog.lb.parameters.minVAF"));
        private final TextField analysisParemetersMinVAFTf = new TextField();
        private final Label analysisParemetersminDepthLb = new Label(App.getBundle().getString("analysisinfodialog.lb.parameters.minDepth"));
        private final TextField analysisParemetersminDepthTf = new TextField();
        private final Label analysisParemetersWarningDepthLb = new Label(App.getBundle().getString("analysisinfodialog.lb.parameters.genome"));
        private final TextField analysisParemetersWarningDepthTf = new TextField();
        private final Label analysisParemetersGenomeLb = new Label(App.getBundle().getString("analysisinfodialog.lb.parameters.genome"));
        private final TextField analysisParemetersGenomeTf = new TextField();
        private final Label analysisParemetersLibraryLb = new Label(App.getBundle().getString("analysisinfodialog.lb.parameters.library"));
        private final TextField analysisParemetersLibraryTf = new TextField();


        public AnalysisInfoDialog(DialogPane pane) {

            super(pane, DialogPane.Type.INFORMATION);

            setTitle(App.getBundle().getString("analysisinfodialog.title"));
            setContent(gridPane);
            initView();

            valueProperty().addListener((obs, oldV, newV) -> {
                fillFields();
            });
        }

        private void initView() {

            analysisNameTf.setEditable(false);
            analysisNameTf.setPrefWidth(600);
            analysisSampleNameTf.setEditable(false);
            analysisCreationDateTf.setEditable(false);
            analysisUserCreationTf.setEditable(false);
            analysisPathTf.setEditable(false);
            analysisVCFTf.setEditable(false);
            analysisBAMTf.setEditable(false);
            analysisDepthTf.setEditable(false);
            analysisParemetersNameTf.setEditable(false);
            analysisParemetersPanelTf.setEditable(false);
            analysisParemetersMinVAFTf.setEditable(false);
            analysisParemetersminDepthTf.setEditable(false);
            analysisParemetersWarningDepthTf.setEditable(false);
            analysisParemetersGenomeTf.setEditable(false);

            gridPane.setAlignment(Pos.CENTER);
            gridPane.setVgap(5);
            gridPane.setHgap(5);

            int rowIdx = 0;
            gridPane.add(analysisNameLb, 0, ++rowIdx);
            gridPane.add(analysisNameTf, 1, rowIdx);
            gridPane.add(analysisSampleNameLb, 0, ++rowIdx);
            gridPane.add(analysisSampleNameTf, 1, rowIdx);
            gridPane.add(analysisCreationDateLb, 0, ++rowIdx);
            gridPane.add(analysisCreationDateTf, 1, rowIdx);
            gridPane.add(analysisUserCreationLb, 0, ++rowIdx);
            gridPane.add(analysisUserCreationTf, 1, rowIdx);
            gridPane.add(analysisPathLb, 0, ++rowIdx);
            gridPane.add(analysisPathTf, 1, rowIdx);
            gridPane.add(analysisVCFLb, 0, ++rowIdx);
            gridPane.add(analysisVCFTf, 1, rowIdx);
            gridPane.add(analysisBAMLb, 0, ++rowIdx);
            gridPane.add(analysisBAMTf, 1, rowIdx);
            gridPane.add(analysisDepthLb, 0, ++rowIdx);
            gridPane.add(analysisDepthTf, 1, rowIdx);
            gridPane.add(analysisParemetersNameLb, 0, ++rowIdx);
            gridPane.add(analysisParemetersNameTf, 1, rowIdx);
            gridPane.add(analysisParemetersPanelLb, 0, ++rowIdx);
            gridPane.add(analysisParemetersPanelTf, 1, rowIdx);
            gridPane.add(analysisParemetersHotspotsLb, 0, ++rowIdx);
            gridPane.add(analysisParemetersHotspotsTf, 1, rowIdx);
            gridPane.add(analysisParemetersGenomeLb, 0, ++rowIdx);
            gridPane.add(analysisParemetersGenomeTf, 1, rowIdx);
            gridPane.add(analysisParemetersMinVAFLb, 0, ++rowIdx);
            gridPane.add(analysisParemetersMinVAFTf, 1, rowIdx);
            gridPane.add(analysisParemetersminDepthLb, 0, ++rowIdx);
            gridPane.add(analysisParemetersminDepthTf, 1, rowIdx);
            gridPane.add(analysisParemetersWarningDepthLb, 0, ++rowIdx);
            gridPane.add(analysisParemetersWarningDepthTf, 1, rowIdx);
            gridPane.add(analysisParemetersLibraryLb, 0, ++rowIdx);
            gridPane.add(analysisParemetersLibraryTf, 1, rowIdx);
        }

        private void fillFields() {
            if (getValue() != null) {
                analysisNameTf.setText(getValue().getName());
                analysisSampleNameTf.setText(getValue().getSampleName());
                analysisCreationDateTf.setText(DateFormatterUtils.formatLocalDateTime(getValue().getCreationDate(), "dd/MM/yyyy HH:ss"));
                analysisUserCreationTf.setText(getValue().getCreationUser());
                analysisPathTf.setText(getValue().getDirectoryPath());
                analysisVCFTf.setText(getValue().getVcfFile().getName());
                analysisBAMTf.setText(getValue().getBamFile() == null ? "" : getValue().getBamFile().getName());
                analysisDepthTf.setText(getValue().getDepthFile() == null ? "" : getValue().getDepthFile().getName());
                analysisParemetersNameTf.setText(getValue().getAnalysisParameters().getAnalysisName());
                analysisParemetersPanelTf.setText(getValue().getAnalysisParameters().getPanel().getName());
                analysisParemetersHotspotsTf.setText(getValue().getAnalysisParameters().getHotspotsSet() == null ? "" : getValue().getAnalysisParameters().getHotspotsSet().getName());
                analysisParemetersGenomeTf.setText(getValue().getAnalysisParameters().getGenome().getName());
                analysisParemetersMinVAFTf.setText(String.valueOf(getValue().getAnalysisParameters().getMinVAF()));
                analysisParemetersminDepthTf.setText(String.valueOf(getValue().getAnalysisParameters().getMinDepth()));
                analysisParemetersWarningDepthTf.setText(String.valueOf(getValue().getAnalysisParameters().getWarningDepth()));
                analysisParemetersLibraryTf.setText(String.valueOf(getValue().getAnalysisParameters().getTargetEnrichment()));
            }
            else {
                clearFiels();
            }
        }

        private void clearFiels() {
            analysisNameTf.setText(null);
            analysisSampleNameTf.setText(null);
            analysisCreationDateTf.setText(null);
            analysisUserCreationTf.setText(null);
            analysisPathTf.setText(null);
            analysisVCFTf.setText(null);
            analysisBAMTf.setText(null);
            analysisDepthTf.setText(null);
            analysisParemetersNameTf.setText(null);
            analysisParemetersPanelTf.setText(null);
            analysisParemetersPanelTf.setText(null);
            analysisParemetersGenomeTf.setText(null);
            analysisParemetersMinVAFTf.setText(null);
            analysisParemetersminDepthTf.setText(null);
            analysisParemetersWarningDepthTf.setText(null);
        }
}
