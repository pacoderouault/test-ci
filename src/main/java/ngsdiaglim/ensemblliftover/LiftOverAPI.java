package ngsdiaglim.ensemblliftover;

import ngsdiaglim.App;
import ngsdiaglim.AppSettings;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class LiftOverAPI {

    private final String server;

    public LiftOverAPI() {
        server = App.get().getAppSettings().getProperty(AppSettings.DefaultAppSettings.ENSEMBL_API.name());
    }

    public void grch38ToGrch37(String contig, int start, int end) throws IOException {
        String ext = "/map/human/GRCh38/%s:%d-%d:1/GRCh37?";
        query(ext);
    }

    public void grch37ToGrch38(String contig, int start, int end) throws IOException {
        String ext = "/map/human/GRCh37/%s:%d-%d:1/GRCh38?";
        query(ext);
    }

    public void query(String ext) throws IOException {
        URL url = new URL(server + ext);

        URLConnection connection = url.openConnection();
        HttpURLConnection httpConnection = (HttpURLConnection)connection;

        httpConnection.setRequestProperty("Content-Type", "application/json");


        InputStream response = connection.getInputStream();
        int responseCode = httpConnection.getResponseCode();

        if(responseCode != 200) {
            throw new RuntimeException("Response code was not 200. Detected response was "+responseCode);
        }

        String output;
        Reader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(response, "UTF-8"));
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }
            output = builder.toString();
        }
        finally {
            if (reader != null) try {
                reader.close();
            } catch (IOException logOrIgnore) {
                logOrIgnore.printStackTrace();
            }
        }

    }


}
