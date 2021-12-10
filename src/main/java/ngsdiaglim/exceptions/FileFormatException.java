package ngsdiaglim.exceptions;

public class FileFormatException extends Exception {
    public FileFormatException(String mess) {
        super(mess);
    }

    public FileFormatException(String mess, Throwable throwable) {
        super(mess, throwable);
    }
}
