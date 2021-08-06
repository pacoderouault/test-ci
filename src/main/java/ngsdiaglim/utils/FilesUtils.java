package ngsdiaglim.utils;

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

}
