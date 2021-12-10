package ngsdiaglim.exceptions;

public class DuplicateSampleInRun extends Exception {

    public DuplicateSampleInRun(String mess) {
        super(mess);
    }

    public DuplicateSampleInRun(String mess, Throwable throwable) {
        super(mess, throwable);
    }

}
