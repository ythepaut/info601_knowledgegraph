package exceptions;

/**
 * Thrown when using an uninitialized link
 */
public class UninitializedLinkException extends IllegalStateException {
    public UninitializedLinkException(String msg) {
        super(msg);
    }
}
