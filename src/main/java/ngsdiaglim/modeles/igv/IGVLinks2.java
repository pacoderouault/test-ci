package ngsdiaglim.modeles.igv;

import javafx.beans.property.SimpleObjectProperty;
import ngsdiaglim.App;
import ngsdiaglim.AppSettings;
import ngsdiaglim.modeles.analyse.Analysis;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringJoiner;

public class IGVLinks2 {

    private final SimpleObjectProperty<Analysis> openedAnalysis = new SimpleObjectProperty<>();

    private final String igvLink;

    public IGVLinks2() {
        String igv_port_str = App.get().getAppSettings().getProperty(AppSettings.DefaultAppSettings.IGV_PORT.name());
        igvLink = "http://localhost:"+igv_port_str+"/";
    }

    private String getLink(Analysis analysis, String locus) {
        String link = getAnalysisFiles(analysis);
        if (openedAnalysis.get() == null || !openedAnalysis.get().equals(analysis)) {
            openedAnalysis.set(analysis);
            link += "&merge=false";
        }
        link += locus;
        return link;
    }

    public void goTo(Analysis analysis, String contig, int pos) throws IOException {
        if (analysis != null) {
            String locus = "&locus=" + contig + ":" + pos;
            String link = getLink(analysis, locus);
            execute(link);
        }
    }

    public void goTo(Analysis analysis, String contig, int start, int end) throws IOException {
        if (analysis != null) {
            String locus = "&locus=" + contig + ":" + start + "-" + end;
            String link = getLink(analysis, locus);
            execute(link);
        }
    }

    private void execute(String link) throws IOException {
        System.out.println(link);
        URL url = new URL(link);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.getInputStream();
    }

    private String getAnalysisFiles(Analysis analysis) {
        String link = igvLink + "load?file=";
        StringJoiner files = new StringJoiner(",");
        StringJoiner names = new StringJoiner(",");

        File bamFile = analysis.getBamFile();
        if (bamFile != null && bamFile.exists()) {
            files.add(bamFile.getAbsolutePath());
            names.add(analysis.getSampleName());
        }

        File covFile = analysis.getCoverageFile();
        if (covFile != null && covFile.exists()) {
            String path = covFile.getAbsolutePath();
            if (!path.endsWith(".gz")) {
                path += ".gz";
            }
            files.add(path);
            String trackname = analysis.getSampleName() + "_cov";
            names.add(trackname);
        }

        if (analysis.getAnalysisParameters().getSpecificCoverageSet() != null) {
            File specCovFile = analysis.getSpecCoverageFile();
            if (specCovFile != null && specCovFile.exists()) {
                String path = specCovFile.getAbsolutePath();
                if (!path.endsWith(".gz")) {
                    path += ".gz";
                }
                files.add(path);
                String trackname = analysis.getSampleName() + "_spec_cov";
                names.add(trackname);
            }
        }

        File vcfFile = analysis.getVcfFile();
        if (vcfFile != null && vcfFile.exists()) {
            files.add(vcfFile.getAbsolutePath());
            names.add(analysis.getSampleName());
        }

        File bedFile = analysis.getAnalysisParameters().getPanel().getBedFile();
        if (bedFile != null && bedFile.exists()) {
            files.add(bedFile.getAbsolutePath());
            names.add(analysis.getAnalysisParameters().getPanel().getName());
        }

        link += files.toString();
        link += "&name=";
        link += names.toString();

        return link;
    }
}
