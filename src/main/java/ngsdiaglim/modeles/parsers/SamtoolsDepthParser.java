package ngsdiaglim.modeles.parsers;

import ngsdiaglim.utils.IOUtils;
import ngsdiaglim.utils.NumberUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class SamtoolsDepthParser {
    private final File file;

    public SamtoolsDepthParser(File file) {
        this.file = file;
    }

    public File getFile() {return file;}

    public void parseFile() {

    };

    /**
     * Return true if the file corresponds to a standard samtools depth output (<String>contig</String>  <int>pos</int> <int>depth</int>)
     */
    public static boolean isDepthDile(File file) throws IOException {
        try (BufferedReader reader = IOUtils.getFileReader(file)) {
            String line = reader.readLine();
            String[] tks = line.trim().split("\t");
            return tks.length == 3 &&
                    NumberUtils.isInt(tks[1]) &&
                    NumberUtils.isInt(tks[2]);
        }
    }
}
