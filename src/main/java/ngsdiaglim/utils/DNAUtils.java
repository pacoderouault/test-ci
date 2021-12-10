package ngsdiaglim.utils;

public class DNAUtils {

    private DNAUtils() {}

    public static String reverseComplement(String seq) {
        seq = seq.toUpperCase();
        StringBuilder sb = new StringBuilder();
        for (int i = seq.length() - 1; i >= 0; i--) {
            sb.append(complement(seq.charAt(i)));
        }
        return sb.toString();
    }

    public static char complement(char seq) {
        if (seq == 'A' || seq == 'a') return 'T';
        if (seq == 'C' || seq == 'c') return 'G';
        if (seq == 'G' || seq == 'g') return 'C';
        if (seq == 'T' || seq == 't') return 'A';
        else return 'N';
    }

    public static String complement(String seq) {
        StringBuilder sb  = new StringBuilder();
        for (char c : seq.toCharArray()) {
            sb.append(complement(c));
        }
        return sb.toString();
    }

    public static double getGCContent(String seq) {
        if (seq.length() == 0) {
            return 0;
        }

        int gcNb = 0;
        for (char c: seq.toCharArray()) {
            if (c == 'C' || c == 'G' || c == 'c' || c == 'g') {
                gcNb++;
            }
        }
        return gcNb / (seq.length() * 1.0);
    }
}
