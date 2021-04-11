package exceptions;

/**
 * Thrown when we try to attach incompatible nodes through a link type
 */
public class IllegalLinkAssociationException extends Exception {
    public IllegalLinkAssociationException(String msg) {
        super(msg);
    }
}
