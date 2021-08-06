package ngsdiaglim.utils;

import ngsdiaglim.App;

import java.text.MessageFormat;

public class BundleFormatter {
    public static String format(String bundleString, Object[] messageArguments) {
        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(App.locale);
        formatter.applyPattern(App.getBundle().getString(bundleString));
        return formatter.format(messageArguments);
    }
}
