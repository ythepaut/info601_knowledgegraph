package model.link;

import exceptions.IllegalLinkAssociationException;
import exceptions.NoLinkedNodeException;
import exceptions.UninitializedLinkException;
import model.node.Node;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Link
 */
public abstract class Link {

    /**
     * Next free ID
     */
    private static int nextId = 0;

    private final String id;

    /**
     * Origin node
     */
    private Node from;

    /**
     * Destination node
     */
    private Node to;

    /**
     * Oriented
     */
    private final boolean oriented;

    /**
     * Link name
     */
    private String name;


    protected Link(String name, boolean oriented) {
        this.id = Integer.toString(Link.nextId++);

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

        if (other.equals(from))
            return to;
        else if (other.equals(to))
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

    public String getId() {
        return id;
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

    /**
     * @return Link to JSON object conversion
     */
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("from", from.getId());
        obj.put("to", to.getId());
        obj.put("name", name);
        obj.put("oriented", oriented);
        return obj;
    }

    /**
     * @param obj JSON object
     * @return Link from JSON object
     * @throws JSONException Bad JSON
     */
    public static Link fromJSONObject(JSONObject obj) throws JSONException {
        String name = obj.getString("name");
        boolean oriented = obj.getBoolean("oriented");

        switch (obj.getString("type")) {
            case "AKO":
                return new AkoLink();

            case "ASSOCIATION":
                return new AssociationLink(name, oriented);

            case "COMPOSITION":
                return new CompositionLink(name, oriented);

            case "INSTANCE":
                return new InstanceLink();

            default:
                throw new JSONException("Invalid type");
        }
    }
}
