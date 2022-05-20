package ngsdiaglim.modeles.igv;

import javafx.beans.property.SimpleObjectProperty;
import ngsdiaglim.App;
import ngsdiaglim.AppSettings;
import ngsdiaglim.modeles.analyse.Analysis;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class IGVLinks {

    private final SimpleObjectProperty<Analysis> openedAnalysis = new SimpleObjectProperty<>();

    private final String igvLink;
    private final String gotTo = "goto?";
    private final String load = "load?";

    public IGVLinks() {
        String igv_port_str = App.get().getAppSettings().getProperty(AppSettings.DefaultAppSettings.IGV_PORT.name());
        igvLink = "http://localhost:"+igv_port_str+"/";
    }

    public void goTo(Analysis analysis, String contig, int pos) throws IOException {
        String link = igvLink + gotTo + "&locus=" + contig + ":" + pos;
        execute(analysis, link);
    }

    public void goTo(Analysis analysis, String contig, int start, int end) throws IOException {
        String link = igvLink + gotTo + "&locus=" + contig + ":" + start + "-" + end;
        execute(analysis, link);
    }

    private void execute(Analysis analysis, String link) throws IOException {
        checkAnalysisChange(analysis);
        execute(link);
    }

    private void execute(String link) throws IOException {
        try {
            System.out.println(link);
            link = "http://localhost:60151/goto?locus=egfr";
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.getInputStream();
        } catch (Exception e) {

        }
    }

    private String loadFiles() {
        String names = "&name=";
        String link = igvLink + load + "&file=";

        File bamFile = openedAnalysis.get().getBamFile();
        if (bamFile != null && bamFile.exists()) {
            link += bamFile.getAbsolutePath() + ",";
            names += openedAnalysis.get().getSampleName() + ",";
        }

        File covFile = openedAnalysis.get().getCoverageFile();
        if (covFile != null && covFile.exists()) {
            String path = covFile.getAbsolutePath();
            if (!path.endsWith(".gz")) {
                path += ".gz";
            }
            link += path + ",";
            String trackname = openedAnalysis.get().getSampleName() + "_cov";
            names += trackname + ",";
        }

        File vcfFile = openedAnalysis.get().getVcfFile();
        if (vcfFile != null && vcfFile.exists()) {
            link += vcfFile.getAbsolutePath() + ",";
            names += openedAnalysis.get().getSampleName() + "_vcf" + ",";
        }

        File bedFile = openedAnalysis.get().getAnalysisParameters().getPanel().getBedFile();
        if (bedFile != null && bedFile.exists()) {
            link += bedFile.getAbsolutePath();
            names += openedAnalysis.get().getAnalysisParameters().getPanel().getName();
        }

        link += names;
        link += "&merge=false";

        return link;
    }

    private void checkAnalysisChange(Analysis analysis) throws IOException {
        if (analysis != null && (openedAnalysis.get() == null || !openedAnalysis.get().equals(analysis))) {
            openedAnalysis.set(analysis);
            String link = loadFiles();
            execute(link );
        }
    }
}
