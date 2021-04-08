package exceptions;

/**
 * Thrown when searching through a link with a node that isn't one of its extremity
 */
public class NoLinkedNodeException extends NoSuchFieldException {
    public NoLinkedNodeException(String msg) {
        super(msg);
    }
}
