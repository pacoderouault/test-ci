package ngsdiaglim.utils;

import java.util.Arrays;

public class StringUtils {

    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
        if (inputStr == null || items == null) return false;
        return Arrays.stream(items).anyMatch(inputStr::contains);
    }

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    /**
     *
     * @return True is the param src contains the param what, without case sensitive
     */
    public static boolean containsIgnoreCase(String src, String what) {
        if (src == null || what == null) return false;
        final int length = what.length();
        if (length == 0)
            return true; // Empty string is contained

        final char firstLo = Character.toLowerCase(what.charAt(0));
        final char firstUp = Character.toUpperCase(what.charAt(0));

        for (int i = src.length() - length; i >= 0; i--) {
            // Quick check before calling the more expensive regionMatches() method:
            final char ch = src.charAt(i);
            if (ch != firstLo && ch != firstUp)
                continue;

            if (src.regionMatches(true, i, what, 0, length))
                return true;
        }

        return false;
    }
}
