package mempress;

public class MempressException extends RuntimeException {

    private static final long serialVersionUID = 6560510941064291204L;

    public MempressException() {
    }

    //Constructor that accepts a message
    public MempressException(String message) {
        super(message);
    }

}
