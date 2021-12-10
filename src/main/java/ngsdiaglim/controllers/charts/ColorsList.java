package ngsdiaglim.controllers.charts;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class ColorsList {

    public static List<Color> colors = generateColor();
    public static List<Color> geneColors = generateGenesColor();

//    private static List<Color> generateNormalColor() {
//        String[] colors = {"5c8b9bff", "77e8dcff", "264d54ff", "8ad0f9ff", "324380ff", "d27ae9ff", "867ac4ff",
//                "e8caeeff", "867ac4ff", "e8caeeff", "72329eff", "bee091ff", "48853aff", "8bec6eff", "8b9662ff",
//                "673b11ff", "f3a852ff", "e1502fff", "932846ff", "a17a65ff", "e77a9fff", "ec3686ff", "414b17ff",
//                "f0d447ff", "4141f5ff", "e743f4ff"};
//        List<Color> colorsList = new ArrayList<>();
//        for (String c : colors) {
//            colorsList.add(Color.web(c));
//        }
//        return colorsList;
//    }

    private static List<Color> generateColor() {
        String[] colors = {"d1bbd7", "ae76a3", "882e72", "1965B0", "5289C7", "7bafde", "4eb265",
                "90C987", "cae0ab", "f7f056", "f6c141", "f1932d", "e8601c", "dc050c"};
        List<Color> colorsList = new ArrayList<>();
        for (String c : colors) {
            colorsList.add(Color.web(c));
        }
        return colorsList;
    }

    private static List<Color> generateGenesColor() {
        String[] colors = {"0x2b3cf456", "0x70f1e060", "0x4fec3c43", "0xe9ea3b69", "0xfc64679c", "0xf4a1c240", "0x44cc7884",
                "0x2c26b565", "0xf012927c", "0x0a468141", "0x910e177c", "0x7508d8ac", "0x0d322739", "0x870ca86a", "0xaf8f7b48",
                "0x1cb919a6", "0x96abf578", "0x64343b38", "0x415c43a2", "0x58c4739b", "0xe03bd958", "0x7cc11240", "0xc500099a",
                "0x5c9eea4c", "0xe7483273", "0x7beb006e", "0xcc47b8ab", "0x5cb21663", "0x3736594c", "0x2bf3d250", "0x61307a9d",
                "0x15e9449d", "0x5fff8663", "0x0c128e7b", "0x175d1e49", "0xad40489c", "0x37a3fe9a", "0xb6198f82", "0x4fbe315d", "0xc782ff5c", "0x3009b66d"};
        List<Color> genesColor = new ArrayList<>();
        for (String c : colors) {
            genesColor.add(Color.web(c));
        }
        return genesColor;
    }

    public static Color getGeneColor(int i)  {
        return geneColors.get(i % colors.size());
    }

    public static Color getColor(int i) {
        return colors.get(i % colors.size());
    }

}
