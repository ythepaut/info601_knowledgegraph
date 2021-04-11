package model.link;

import exceptions.IllegalLinkAssociationException;
import exceptions.NoLinkedNodeException;
import exceptions.UninitializedLinkException;
import model.node.Node;

/**
 * Link
 */
public abstract class Link {
    /**
     * Origin node
     */
    private Node from;

    /**
     * Destination node
     */
    private Node to;

    private final boolean oriented;

    private String name;

    protected Link(String name, boolean oriented) {
        if (name == null)
            throw new IllegalArgumentException("Link name cannot be null");

        this.name = name;
        this.oriented = oriented;
    }

    protected Link(boolean oriented) {
        this("link", oriented);
    }

    public abstract boolean isCompatible(Node from, Node to);

    public abstract boolean isSameLink(Link other);

    /**
     * Get a note depending of the other extremity
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
    
    public String getName() {
        return name;
    }

    public boolean isOriented() {
        return oriented;
    }

    /**
     * @param from New origin of the link
     * @throws IllegalLinkAssociationException Thrown when incompatible link and nodes association
     */
    public void setFrom(Node from) throws IllegalLinkAssociationException {
        if (this.to != null && !this.isCompatible(from, this.to)) {
            throw new IllegalLinkAssociationException("Link (" + getClass().getName() + ") is not compatible with " +
                    "nodes from (" + from.getClass().getName() + ") and to (" + this.to.getClass().getName() + ").");
        }
        this.from = from;
    }

    /**
     * @param to New origin of the link
     * @throws IllegalLinkAssociationException Thrown when incompatible link and nodes association
     */
    public void setTo(Node to) throws IllegalLinkAssociationException {
        if (this.from != null && !this.isCompatible(this.from, to)) {
            throw new IllegalLinkAssociationException("Link (" + getClass().getName() + ") is not compatible with " +
                    "nodes from (" + this.from.getClass().getName() + ") and to (" + to.getClass().getName() + ").");
        }
        this.to = to;
    }
    
    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException("Name can't be null");

        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}