package ngsdiaglim.controllers.cells;

import javafx.scene.control.ListCell;
import ngsdiaglim.modeles.variants.TranscriptConsequence;


public class TranscriptConsequenceListViewCell extends ListCell<TranscriptConsequence> {

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
                    getStyleClass().add("selected-transcript");
                }
            }
        }
    }
}
