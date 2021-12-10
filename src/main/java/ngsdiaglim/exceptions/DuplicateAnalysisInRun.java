package ngsdiaglim.exceptions;

public class DuplicateAnalysisInRun extends Exception {

    public DuplicateAnalysisInRun(String mess) {
        super(mess);
    }

    public DuplicateAnalysisInRun(String mess, Throwable throwable) {
        super(mess, throwable);
    }

}
