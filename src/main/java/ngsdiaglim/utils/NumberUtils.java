package ngsdiaglim.utils;

public class NumberUtils {

    /**
     * Check if a string represents a valid int.
     * @param str the sting to check.
     * @return true if the line is valid.
     */
    public static boolean isInt(Object str) {
        try
        {
            Integer.parseInt(str.toString());
            return true;
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
    }

    /**
     * Check if a string represents a valid int.
     * @param str the sting to check.
     * @return true if the line is valid.
     */
    public static boolean isDouble(String str) {
        try
        {
            Double.parseDouble(str);
            return true;
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
    }

    /**
     * Check if a string represents a valid int.
     * @param str the sting to check.
     * @return true if the line is valid.
     */
    public static boolean isFloat(Object str) {
        try
        {
            Float.parseFloat(str.toString());
            return true;
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
    }

    /**
     * Check if a string represents a valid numeric value.
     * @param str the sting to check.
     * @return true if the line is valid.
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
