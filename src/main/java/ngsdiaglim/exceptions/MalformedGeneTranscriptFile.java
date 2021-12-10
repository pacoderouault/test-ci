package ngsdiaglim.exceptions;

public class MalformedGeneTranscriptFile  extends Exception {

    public MalformedGeneTranscriptFile(String mess) {
        super(mess);
    }

    public MalformedGeneTranscriptFile(String mess, Throwable throwable) {
        super(mess, throwable);
    }
}
