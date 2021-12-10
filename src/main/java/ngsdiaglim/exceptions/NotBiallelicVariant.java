package ngsdiaglim.exceptions;

public class NotBiallelicVariant extends Exception {

    public NotBiallelicVariant(String mess) {
        super(mess);
    }

    public NotBiallelicVariant(String mess, Throwable throwable) {
        super(mess, throwable);
    }
}
