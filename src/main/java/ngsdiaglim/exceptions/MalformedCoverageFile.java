package ngsdiaglim.exceptions;

public class MalformedCoverageFile  extends Exception {

    public MalformedCoverageFile(String mess) {
        super(mess);
    }

    public MalformedCoverageFile(String mess, Throwable throwable) {
        super(mess, throwable);
    }
}
