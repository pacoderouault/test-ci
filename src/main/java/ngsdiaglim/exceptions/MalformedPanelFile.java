package ngsdiaglim.exceptions;

public class MalformedPanelFile extends Exception {

    public MalformedPanelFile(String mess) {
        super(mess);
    }

    public MalformedPanelFile(String mess, Throwable throwable) {
        super(mess, throwable);
    }
}
