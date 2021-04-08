package model.link;

import model.node.Node;
import exceptions.NoLinkedNodeException;
import exceptions.UninitializedLinkException;

/**
 * Link
 */
public abstract class Link {
    /**
     * From
     */
    private Node from;

    /**
     * To
     */
    private Node to;

    /**
     * @param other Other node
     * @return Other extremity
     * @throws UninitializedLinkException When using an uninitialized link
     * @throws IllegalArgumentException If the `other` node is null
     * @throws NoLinkedNodeException The node `other` isn't an extremity of the link
     */
    public Node getLinkedNode(Node other) throws
            UninitializedLinkException,
            IllegalArgumentException,
            NoLinkedNodeException
    {
        if (from == null || to == null)
            throw new UninitializedLinkException("Uninitialized link");

        if (other == null)
            throw new IllegalArgumentException("Other node cannot be null");

        if (other == from)
            return to;
        else if (other == to)
            return from;
        else
            throw new NoLinkedNodeException("No other node");
    }

    /**
     * Remove the link from the knowledge graph
     */
    public void detach() {
        from.removeLink(this);
        to.removeLink(this);
        from = null;
        to = null;
    }

    /**
     * @return Origin of the link
     */
    public Node getFrom() {
        return from;
    }

    /**
     * @return Extremity of the link
     */
    public Node getTo() {
        return to;
    }

    /**
     * @param from New origin of the link
     */
    public void setFrom(Node from) {
        this.from = from;
    }

    /**
     * @param to New origin of the link
     */
    public void setTo(Node to) {
        this.to = to;
    }
}
