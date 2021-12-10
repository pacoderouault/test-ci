package ngsdiaglim.exceptions;

public class MalformedSearchQuery extends Exception {

    public MalformedSearchQuery(String mess) {
        super(mess);
    }

    public MalformedSearchQuery(String mess, Throwable throwable) {
        super(mess, throwable);
    }
}


