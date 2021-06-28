package awm;

public class AwmException extends RuntimeException {
    public AwmException(String reason) {
        super(reason);
    }
    public AwmException(Exception ex) {
        super(ex);
    }
}
