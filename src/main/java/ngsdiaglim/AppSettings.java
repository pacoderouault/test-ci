package ngsdiaglim;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class AppSettings extends Properties {

    private static final Logger logger = LogManager.getLogger(AppSettings.class);
    private final String PROPS_PATH = "application.properties";

    public AppSettings() throws IOException {

        loadDefaultProperties();
        loadApplicationProperties();

    }


    private void loadDefaultProperties() {
        for (DefaultAppSettings setting : DefaultAppSettings.values()) {
            if (!containsKey(setting.name())) {
                put(setting.name(), setting.getValue());
            }
        }
    }


    private void loadApplicationProperties() throws IOException {
        File propertiesFile = new File(PROPS_PATH);
        if (propertiesFile.exists()) {
            load(new FileInputStream(propertiesFile));
        }
    }


    public void setValue(DefaultAppSettings appSettings, Object value) {
        put(appSettings.name(), value.toString());
        store();
    }


    /**
     * write the properties file
     */
    private void store() {
        FileOutputStream out;
        try {
            out = new FileOutputStream(PROPS_PATH);
            store(out, "");
        } catch (IOException e) {
            logger.error("Impossible to save application settings", e);
        }
    }



    public enum DefaultAppSettings {

        MAXIMIZED("True");

        private final String value;

        DefaultAppSettings(String value) {
            this.value = value;
        }

        public String getValue() {return value;}
    }
}
