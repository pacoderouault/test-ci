package ngsdiaglim.modeles.parsers;

import htsjdk.samtools.util.CloserUtil;
import htsjdk.tribble.FeatureCodecHeader;
import htsjdk.tribble.gff.Gff3Codec;
import htsjdk.tribble.gff.Gff3Feature;
import htsjdk.tribble.readers.LineIterator;
import ngsdiaglim.utils.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class GFFParser {

//    private final GTFCodec gtfCodec = new GTFCodec();
    private Gff3Codec2 gffCodec = new Gff3Codec2();
//    private Gff3Codec gffCodec = new Gff3Codec(Gff3Codec.DecodeDepth.SHALLOW);

    public void parseGff(File file) throws IOException {

//        try (BufferedReader reader = IOUtils.getFileReader(file)){
            LineIterator lineIterator = gffCodec.makeSourceFromStream(IOUtils.getInputStream(file));
//            gffCodec.getCommentsWithLineNumbers().forEach((key, value) -> System.out.println(value));
//        System.out.println(gffCodec.getCommentsWithLineNumbers().size());
//            for (Integer i : gffCodec.getCommentsWithLineNumbers().keySet()) {
//                System.out.println(i);
//            }
//        System.out.println(lineIterator == null);
        gffCodec.readHeader(lineIterator);
        Gff3Feature t = gffCodec.decode(lineIterator);

        System.out.println(gffCodec.isDone(lineIterator));
        System.out.println(t == null);

//            String line;
//            while((line = reader.readLine()) != null) {
//                gffCodec.
//            }
//        }
//        System.out.println(gffCodec.canDecode(file.getPath()));
//
//        LineIterator lineIterator = null;
////        try {
//            lineIterator = IOUtils.openFileForLineIterator(file);
////            FeatureCodecHeader fch = gffCodec.readHeader(lineIterator);
////            FeatureCodecHeader t = gffCodec.readHeader(lineIterator);
////            System.out.println(t.getHeaderValue());
////            for (String s : gffCodec.getCommentTexts()) {
////                System.out.println(s);
////            }
//            Gff3Feature r = gffCodec.decode(lineIterator);
//            System.out.println(r == null);
//            System.out.println(r.getType());
////            Gff3Feature t = gffCodec.decode(lineIterator);
////            System.out.println(t.getName());
////            while (lineIterator.hasNext()) {
////                final String line = lineIterator.next();
////
////                if (line.isEmpty() || line.startsWith("#")) continue;
////                System.out.println(line);
////                gffCodec.
////            }
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        finally {
////            CloserUtil.close(lineIterator);
////        }



    }

}
