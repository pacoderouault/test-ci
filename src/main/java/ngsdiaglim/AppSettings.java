package ngsdiaglim;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class AppSettings extends Properties {

    private static final Logger logger = LogManager.getLogger(AppSettings.class);
    private static final String PROPS_PATH = "application.properties";
    public static final String TOOLS_DATA = ".data";
    public static final String RUNS_PATH = "runs_data";
    public static final String PANELS_PATH = "panels";
    public static final String CNV_DIRNAME = "cnv";
    public static final String CNV_CONTROLES_DIRNAME = "controls";
//    public static final String REFERENCE_HG19_PATH = "/mnt/Data/Biological_Data/References/UCSC_hg19/fasta/split/hg19.fa";

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

        MAXIMIZED("True"),
        REFERENCE_HG19("/mnt/Data/Biological_Data/References/hg19.fasta"),
        IGV_IP("127.0.0.1"),
        IGV_PORT("60151"),
        BGM_BLANK_REPORT("./.data/bgm_report_template.docx"),
        SERVICE("");

        private final String value;

        DefaultAppSettings(String value) {
            this.value = value;
        }

        public String getValue() {return value;}
    }
}
