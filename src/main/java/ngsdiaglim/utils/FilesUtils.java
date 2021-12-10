package ngsdiaglim.utils;

import ngsdiaglim.App;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesUtils {

    public static String removeFileExtension(String filename) {
        return removeFileExtension(filename, false);
    }

    public static String removeFileExtension(String filename, boolean removeAllExtensions) {
        if (filename == null || filename.isEmpty()) {
            return filename;
        }

        String extPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");
        return filename.replaceAll(extPattern, "");
    }

    public static Path convertAbsolutePathToRelative(Path p) throws IOException {
        Path path = Paths.get(URLDecoder.decode(p.toString(), StandardCharsets.UTF_8));
        File jarDir = new File(App.getJarPath());

        Path pathBase = Paths.get(URLDecoder.decode(jarDir.getPath(), StandardCharsets.UTF_8));
        try {
            return pathBase.relativize(path);
        }
        catch (Exception e) {
            throw new IOException("Impossible to relativize path", e);
        }
    }


    public static String computesSHA256(File file) throws IOException {
        if (file.exists() && file.isFile()) {
            return DigestUtils.sha256Hex(new FileInputStream(file));
        }
        return null;
    }


    public static boolean compareFiles(File file1, File file2) throws IOException {
        String hash1 = computesSHA256(file1);
        String hash2 = computesSHA256(file2);
        return hash1 != null && hash1.equals(hash2);
    }
}
