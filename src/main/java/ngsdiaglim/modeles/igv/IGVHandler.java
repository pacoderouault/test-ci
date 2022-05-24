package ngsdiaglim.modeles.igv;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import ngsdiaglim.App;
import ngsdiaglim.AppSettings;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.utils.NumberUtils;
import ngsdiaglim.utils.PlatformUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class IGVHandler {

    private final Logger logger = LogManager.getLogger(IGVHandler.class);

    private final IGVConfig igvConfig = new IGVConfig();
    private PrintWriter out = null;
    private BufferedReader in = null;

    private final SimpleObjectProperty<Analysis> openedAnalysis = new SimpleObjectProperty<>();

    public IGVHandler() {

//        if (!igvConfig.igvIsRunning()) {
//            igvConfig.launchIGV();
//        }

//        openedAnalysis.addListener((obs, oldV, newV) -> {
//            if (newV != null) {
//                try {
//                    loadPanel();
//                    loadVCF();
//                    loadBam();
//                    loadCoverage();
//                } catch (IOException e) {
//                    logger.error(e.getMessage(), e);
//                }
//            }
//        });

    }

    public Analysis getAnalysis() {
        return openedAnalysis.get();
    }

    public SimpleObjectProperty<Analysis> openedAnalysisproperty() {
        return openedAnalysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.openedAnalysis.set(analysis);
    }

    public void newSession() throws IOException {
        execute(null, "new");
    }

    public void loadBam() throws IOException {
        File bamFile = openedAnalysis.get().getBamFile();
        if (bamFile != null && bamFile.exists()) {
            execute(openedAnalysis.get(), "load " + bamFile.getAbsolutePath() + " name=" + openedAnalysis.get().getSampleName());
        }
    }


    public void loadCoverage() throws IOException {
        File covFile = openedAnalysis.get().getCoverageFile();
        if (covFile != null && covFile.exists()) {
            String trackname = openedAnalysis.get().getSampleName() + "_cov";
            execute(openedAnalysis.get(), "load " + covFile.getAbsolutePath() + " name=" + trackname);
            execute(openedAnalysis.get(), "expand " + trackname);
        }
    }


    public void loadVCF() throws IOException {
        File vcfFile = openedAnalysis.get().getVcfFile();
        if (vcfFile != null && vcfFile.exists()) {
            execute(openedAnalysis.get(), "load " + vcfFile.getAbsolutePath() + " name=" + openedAnalysis.get().getSampleName() + "_vcf");
        }
    }


    public void loadPanel() throws IOException {
        File bedFile = openedAnalysis.get().getAnalysisParameters().getPanel().getBedFile();
        if (bedFile != null && bedFile.exists()) {
            execute(openedAnalysis.get(), "load " + bedFile.getAbsolutePath() + " name=" + openedAnalysis.get().getAnalysisParameters().getPanel().getName());
        }
    }


    public void goTo(Analysis analysis, String contig, int pos) throws IOException {
        execute(analysis, "goto " + contig + ":" + pos);
    }

    public void goTo(Analysis analysis, String contig, int start, int end) throws IOException {
        execute(analysis, "goto " + contig + ":" + start + "-" + end);
    }

    private void checkAnalysisChange(Analysis analysis) throws IOException {
        if (analysis != null && (openedAnalysis.get() == null || !openedAnalysis.get().equals(analysis))) {
            openedAnalysis.set(analysis);
            newSession();
            loadBam();
            loadPanel();
            loadCoverage();
            loadVCF();
        }
    }

    public void execute(Analysis analysis, String cmd) throws IOException {
//        launchIgv();
//        initSocket();
        try {
            System.out.println("init socket");
            initSocket();
        } catch (IOException e) {
            Message.error(App.getBundle().getString("app.msg.err.igvnotreponding"));
        }

//        String rslt = checkConnection();
//        if (rslt != null) {
//            Platform.runLater(() -> {
//                Message.error(rslt);
//            });
//        } else {
            checkAnalysisChange(analysis);
            System.out.println(cmd);
            out.println(cmd);
//            try {
//        System.out.println("print in");
//                System.out.println(in.readLine());
//        System.out.println("end print in");

//            } catch (IOException e) {
//                e.printStackTrace();
//                Message
//            }
//        }
    }


    private String checkConnection() {
        if (!igvConfig.igvIsRunning()) {
            return App.getBundle().getString("app.msg.err.igvnotrunning");
        } else {
            try {
                initSocket();
                out.println("echo");
                System.out.println(in.readLine());
            } catch (IOException e) {
                return App.getBundle().getString("app.msg.err.igvnotreponding");
            }
        }
        return null;
    }


    private void launchIgv() throws IOException {
        if (!igvConfig.igvIsRunning()) {
            igvConfig.launchIGV();
        }
    }


    private void initSocket() throws IOException {
        if (out == null || in == null) {
            String igv_ip = App.get().getAppSettings().getProperty(AppSettings.DefaultAppSettings.IGV_IP.name());
            String igv_port_str = App.get().getAppSettings().getProperty(AppSettings.DefaultAppSettings.IGV_PORT.name());
            int igv_port;
            if (NumberUtils.isInt(igv_port_str)) {
                igv_port = Integer.parseInt(igv_port_str);
            } else {
                igv_port = Integer.parseInt(AppSettings.DefaultAppSettings.IGV_PORT.getValue());
            }

            Socket socket = new Socket(igv_ip, igv_port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
    }
}
