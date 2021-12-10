package ngsdiaglim.controllers.cells;

import javafx.scene.control.ListCell;
import ngsdiaglim.modeles.variants.TranscriptConsequence;
import org.kordamp.ikonli.javafx.FontIcon;


public class TranscriptConsequenceListViewCell extends ListCell<TranscriptConsequence> {

//    public TranscriptConsequenceListViewCell() {
//        setContentDisplay(ContentDisplay.RIGHT);

//    }

    @Override
    protected void updateItem(TranscriptConsequence item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(null);
        getStyleClass().remove("selected-transcript");
        if (empty || item == null) {
            setText(null);
        } else {

            if (item.getTranscript() != null) {
                StringBuilder sb = new StringBuilder();
                if (item.getTranscript().getGene() != null) {
                    sb.append(item.getTranscript().getGene().getGeneName());
                } else if(item.getGeneName() != null) {
                    sb.append(item.getGeneName());
                } else {
                    sb.append("Unknown Gene");
                }
                sb.append(":");
                sb.append(item.getTranscript().getName());
                if (item.getTranscript().isPreferred()) {
                    sb.append(" (pref.)");
                }
                setText(sb.toString());
                if (item.getAnnotation().getTranscriptConsequence().getTranscript().equals(item.getTranscript())) {
                    setGraphic(new FontIcon("mdomz-star"));
                    getStyleClass().add("selected-transcript");
                }
            }
//            Analysis analysis = ModuleManager.getAnalysisViewController().getAnalysis();
//            String geneName = item.getGeneName();
//            if (geneName != null) {
//                Gene gene = analysis.getAnalysisParameters().getGeneSet().getGene(item.getGeneName());
//                if (gene != null) {
//                    if (gene.getTranscriptPreferred().equals(item.getTranscript())) {
//                        setGraphic(new FontIcon("mdomz-star_rate"));
//                    }
//                }
//            }
        }
    }
}
