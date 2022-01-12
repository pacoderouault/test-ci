package ngsdiaglim.utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProteinUtils {

    private ProteinUtils(){}

    private static final HashMap<String, String> oneToThree = createOneToThreeMap();
    private static final HashMap<String, String> threeToOne = createThreeToOneMap();


    /**
     * init the 1-letter to 3-letter aminoacid code
     */
    private static HashMap<String, String> createOneToThreeMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("A", "Ala");
        map.put("B", "Asx");
        map.put("C", "Cys");
        map.put("D", "Asp");
        map.put("E", "Glu");
        map.put("F", "Phe");
        map.put("G", "Gly");
        map.put("H", "His");
        map.put("I", "Ile");
        map.put("K", "Lys");
        map.put("L", "Leu");
        map.put("M", "Met");
        map.put("N", "Asn");
        map.put("P", "Pro");
        map.put("Q", "Gln");
        map.put("R", "Arg");
        map.put("S", "Ser");
        map.put("T", "Thr");
        map.put("U", "Sec");
        map.put("V", "Val");
        map.put("W", "Trp");
        map.put("Y", "Tyr");
        map.put("Z", "Glx");
        map.put("*", "Ter");
        map.put("X", "Ter");
        map.put("=", "=");
        map.put("fs", "fs");
        return map;
    }


    /**
     * init the 3-letter to 1-letter aminoacid code
     */
    private static HashMap<String, String> createThreeToOneMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Ala", "A");
        map.put("Asx", "B");
        map.put("Cys", "C");
        map.put("Asp", "D");
        map.put("Glu", "E");
        map.put("Phe", "F");
        map.put("Gly", "G");
        map.put("His", "H");
        map.put("Ile", "I");
        map.put("Lys", "K");
        map.put("Leu", "L");
        map.put("Met", "M");
        map.put("Asn", "N");
        map.put("Pro", "P");
        map.put("Gln", "Q");
        map.put("Arg", "R");
        map.put("Ser", "S");
        map.put("Thr", "T");
        map.put("Sec", "U");
        map.put("Val", "V");
        map.put("Trp", "W");
        map.put("Xaa", "X");
        map.put("Tyr", "Y");
        map.put("Glx", "Z");
        map.put("Ter", "*");
        map.put("=", "=");
        return map;
    }


    /**
     * Convert an 1 letter amuinoacid name to this 3 letter code
     * @return The corresponding 3 letter code for the 1 letter aminoacid
     */
    public static String OneToThree(String a) {
        return oneToThree.getOrDefault(a, null);
    }


    /**
     * Convert an 3 letter amuinoacid name to this 1 letter code
     * @return The corresponding 1 letter code for the 3 letter aminoacid
     */
    public static String ThreeToOne(String a) {
        return threeToOne.get(a);
    }

    /**
     * Convert a 3 aminoacid mutation to 1 letter code
     * example : Ser332Pro to S332P
     */
    public static String mutationOneToThree(String mut) {
        if (mut == null) return null;
        // Change amino acide notation from 3 letters to 1 letters
        Pattern p = Pattern.compile("\\p{Lu}|\\*");
        Matcher m = p.matcher(mut);
        while (m.find()) {
            String aminoAcid3L = m.group(0);
            String aminoAcid1L = ProteinUtils.OneToThree(aminoAcid3L);
            if (aminoAcid1L != null) {
                mut = mut.replace(aminoAcid3L, aminoAcid1L);
            }
        }
        return mut;
    }

    /**
     * Convert a 1 aminoacid mutation to 3 letter code
     * example : S332P to Ser332Pro
     */
    public static String mutationThreeToOne(String mut) {
        if (mut == null) return null;
        // Change amino acide notation from 3 letters to 1 letters
        Pattern p = Pattern.compile("\\p{Lu}\\w{2}");
        Matcher m = p.matcher(mut);
        while (m.find()) {
            String aminoAcid3L = m.group(0);
            String aminoAcid1L = ProteinUtils.ThreeToOne(aminoAcid3L);
            if (aminoAcid1L != null) {
                mut = mut.replace(aminoAcid3L, aminoAcid1L);
            }
        }
        return mut;
    }

    /**
     * Format hgvp mutation with capitalization of the first letter of each aminoacid
     * p.pro352arg => p.Pro352Arg
     * p.PRO352ARG => p.Pro352Arg
     */
    public static String formatAAChange(String s) {
        if (s == null) return null;
        StringBuilder sb = new StringBuilder();

        Pattern p = Pattern.compile("^p\\.([a-zA-Z]+)([0-9]+)([a-zA-Z=]+)$");
        Matcher m = p.matcher(s);
        if (m.find()) {
            sb.append("p.")
                    .append(StringUtils.capitalizeFirstLetter(m.group(1).toLowerCase()))
                    .append(m.group(2))
                    .append(StringUtils.capitalizeFirstLetter(m.group(3).toLowerCase()));
        }
        else {
            sb.append(s);
        }
        return sb.toString();
    }
}
