package ngsdiaglim.modeles.igv;

import javafx.beans.property.SimpleObjectProperty;
import ngsdiaglim.App;
import ngsdiaglim.AppSettings;
import ngsdiaglim.modeles.analyse.Analysis;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.StringJoiner;

public class IGVLinks2 {

    private static final Logger logger = LogManager.getLogger(IGVLinks.class);
    private final SimpleObjectProperty<Analysis> openedAnalysis = new SimpleObjectProperty<>();
    private final String igvLink;

    private static HttpClient httpClient;

    public IGVLinks2() {
        String igv_port_str = App.get().getAppSettings().getProperty(AppSettings.DefaultAppSettings.IGV_PORT.name());
        igvLink = "http://localhost:"+igv_port_str+"/";

        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();


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

    public void goTo(Analysis analysis, String contig, int pos) throws Exception {
        if (analysis != null) {
            if (openedAnalysis.get() == null || !openedAnalysis.get().equals(analysis)) {
                String locus = "&locus=" + contig + ":" + pos;
                String link = getLink(analysis, locus);
                execute(link);
            } else {
                execute(igvLink + "goto?locus=" + contig+":"+pos);
            }
        }
    }

    public void goTo(Analysis analysis, String contig, int start, int end) throws Exception {
        if (analysis != null) {
            if (openedAnalysis.get() == null || !openedAnalysis.get().equals(analysis)) {
                String locus = "&locus=" + contig + ":" + start + "-" + end;
                String link = getLink(analysis, locus);
                execute(link);
            } else {
                execute(igvLink + "goto?locus=" + contig+":"+start+"-"+end);
            }
        }
    }

//    private void execute(String link) throws IOException {
//        HttpURLConnection con = null;
//        try {
//            URL url = new URL(link);
//            con = (HttpURLConnection) url.openConnection();
//            con.setReadTimeout(10000);
//            con.setConnectTimeout(10000);
//            con.setRequestMethod("GET");
////            System.out.println(con.getInputStream());
////
////            BufferedReader br = new BufferedReader(new InputStreamReader((con.getErrorStream())));
////            StringBuilder sb = new StringBuilder();
////            String output;
////            while ((output = br.readLine()) != null) {
////                System.out.println(output);
////                sb.append(output);
////            }
////            System.out.println(sb.toString());
//            BufferedReader br = null;
//            System.out.println(con.getResponseCode());
//            if (100 <= con.getResponseCode() && con.getResponseCode() <= 399) {
//                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            } else {
//                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
//            }
//            String output;
//            while ((output = br.readLine()) != null) {
//                System.out.println(output);
//            }
//            System.out.println(con.getResponseCode());
//        } catch (Exception e) {
//            logger.error(e);
//            throw e;
//        } finally {
//            if (con != null) {
//                con.disconnect();
//            }
//        }
//        con.disconnect();
//    }

    private void execute(String link) throws Exception {
//        try {
        System.out.println(link);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(link))
                .GET()
                .build();
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (SocketException se) {
            throw se;
        } catch (IOException ignored) { // IGV doesn't send a response
        }

        if (response != null ) {
//            // print response headers
//            HttpHeaders headers = response.headers();
//            headers.map().forEach((k, v) -> System.out.println(k + ":" + v));
//            // print status code
//            System.out.println(response.statusCode());
//            // print response body
//            System.out.println(response.body());
        }
    }

    private String getAnalysisFiles(Analysis analysis) {
        String link = igvLink + "load?file=";
        StringJoiner files = new StringJoiner(",");
        StringJoiner names = new StringJoiner(",");

        File bamFile = analysis.getBamFile();
        if (bamFile != null && bamFile.exists()) {
            files.add(encode(bamFile.getAbsolutePath()));
            names.add(encode(analysis.getSampleName()));
        }

        File covFile = analysis.getCoverageFile();
        if (covFile != null && covFile.exists()) {
            String path = covFile.getAbsolutePath();
//            if (!path.endsWith(".gz")) {
//                path += ".gz";
//            }
            files.add(encode(path));
            String trackname = analysis.getSampleName() + "_cov";
            names.add(encode(trackname));
        }

        if (analysis.getAnalysisParameters().getSpecificCoverageSet() != null) {
            File specCovFile = analysis.getSpecCoverageFile();
            if (specCovFile != null && specCovFile.exists()) {
                String path = specCovFile.getAbsolutePath();
//                if (!path.endsWith(".gz")) {
//                    path += ".gz";
//                }
                files.add(encode(path));
                String trackname = analysis.getSampleName() + "_spec_cov";
                names.add(encode(trackname));
            }
        }

        File vcfFile = analysis.getVcfFile();
        if (vcfFile != null && vcfFile.exists()) {
            files.add(encode(vcfFile.getAbsolutePath()));
            names.add(encode(analysis.getSampleName()));
        }

        File bedFile = analysis.getAnalysisParameters().getPanel().getBedFile();
        if (bedFile != null && bedFile.exists()) {
            files.add(encode(bedFile.getAbsolutePath()));
            names.add(encode(analysis.getAnalysisParameters().getPanel().getName()));
        }

        link += files.toString();
        link += "&name=";
        link += names.toString();

        return link;
    }


    private String encode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
}
