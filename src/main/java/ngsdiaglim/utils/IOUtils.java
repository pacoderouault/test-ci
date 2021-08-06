package ngsdiaglim.utils;

import htsjdk.samtools.util.BlockCompressedInputStream;
import htsjdk.samtools.util.BlockCompressedStreamConstants;
import htsjdk.samtools.util.IOUtil;
import htsjdk.tribble.readers.LineIterator;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReader;
import htsjdk.tribble.readers.SynchronousLineReader;

import java.io.*;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;

public class IOUtils {

    public static boolean isGZipped(File f) throws IOException {
        InputStream is = new FileInputStream(f);
        byte[] b = new byte[2];
        int n = is.read(b);
        return n == 2 && (b[0] == (byte) 0x1f) && (b[1] == (byte)0x8b);
    }


    public static InputStream getInputStream(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        InputStreamReader isr;
        if (isGZipped(f)) {
            GZIPInputStream gis = new GZIPInputStream(fis);
            return gis;
//            isr = new InputStreamReader(gis);
        }
        else {
            return fis;
//            isr = new InputStreamReader(fis);
        }

    }


    public static BufferedReader getFileReader(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        InputStreamReader isr;
        if (isGZipped(f)) {
            GZIPInputStream gis = new GZIPInputStream(fis);
            isr = new InputStreamReader(gis);
        }
        else {
            isr = new InputStreamReader(fis);
        }
        return new BufferedReader(isr);
    }


    public static InputStream openFileForReading(final File file) throws IOException
    {
        IOUtil.assertFileIsReadable(file);
        InputStream in= Files.newInputStream(file.toPath());
        if(file.getName().endsWith(".gz") || file.getName().endsWith(".bgz")) // DB added .bgz
        {
            in = tryBGZIP(in);
        }
        return in;
    }

    private static InputStream tryBGZIP(final InputStream in) throws IOException
    {
        final byte buffer[]=new byte[
                BlockCompressedStreamConstants.GZIP_BLOCK_PREAMBLE.length  ];

        final PushbackInputStream push_back=new PushbackInputStream(in,buffer.length+10);
        int nReads=push_back.read(buffer);
        push_back.unread(buffer, 0, nReads);

        try
        {
            if( nReads>= buffer.length &&
                    buffer[0]==BlockCompressedStreamConstants.GZIP_ID1 &&
                    buffer[1]==(byte)BlockCompressedStreamConstants.GZIP_ID2 &&
                    buffer[2]==BlockCompressedStreamConstants.GZIP_CM_DEFLATE &&
                    buffer[3]==BlockCompressedStreamConstants.GZIP_FLG &&
                    buffer[8]==BlockCompressedStreamConstants.GZIP_XFL
            )
            {
                return new BlockCompressedInputStream(push_back);
            }
        }
        catch(final Exception err)
        {
            //not bzip
        }
        return new GZIPInputStream(push_back);
    }

    public static LineReader openFileForLineReader(File file) throws IOException
    {
        return new SynchronousLineReader(openFileForReading(file));
    }

    /** @return a LineIterator that should be closed with CloserUtils */
    public static LineIterator openFileForLineIterator(File file) throws IOException
    {
        return  new LineIteratorImpl(openFileForLineReader(file));
    }
}
