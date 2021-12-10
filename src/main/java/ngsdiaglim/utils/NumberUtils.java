package ngsdiaglim.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberUtils {

    /**
     * Check if a string represents a valid int.
     * @param str the sting to check.
     * @return true if the line is valid.
     */
    public static boolean isInt(Object str) {
        if (str == null) return false;
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
        if (str == null) return false;
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
        if (str == null) return false;
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


    public static double round(Number n, int decimalPlace) throws NullPointerException {
        if (n == null) throw new NullPointerException("Number is null");

        BigDecimal bd = BigDecimal.valueOf(n.doubleValue());
        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
