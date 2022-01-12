package ngsdiaglim.utils;

import org.controlsfx.control.ListSelectionView;

public class ListSelectionViewUtils {


    public static void rewriteButtons(ListSelectionView<?> listSelectionView) {
        listSelectionView.getActions().get(0).graphicProperty().unbind();
        listSelectionView.getActions().get(0).setText(">");
        listSelectionView.getActions().get(0).setGraphic(null);
        listSelectionView.getActions().get(1).graphicProperty().unbind();
        listSelectionView.getActions().get(1).setText(">>");
        listSelectionView.getActions().get(1).setGraphic(null);
        listSelectionView.getActions().get(2).graphicProperty().unbind();
        listSelectionView.getActions().get(2).setText("<");
        listSelectionView.getActions().get(2).setGraphic(null);
        listSelectionView.getActions().get(3).graphicProperty().unbind();
        listSelectionView.getActions().get(3).setText("<<");
        listSelectionView.getActions().get(3).setGraphic(null);
    }

}
