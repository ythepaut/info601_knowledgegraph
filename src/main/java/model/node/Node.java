package model.node;

import model.GraphElement;
import model.Property;
import model.link.Link;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Node
 */
public abstract class Node extends GraphElement {
    /**
     * Next free ID
     */
    private static int NEXT_ID = 0;

    private final List<Link> links;
    private final Map<String, Property<?>> properties;

    private final String id;

    public Node(Map<String, Property<?>> properties) {
        this(properties, Integer.toString(Node.NEXT_ID++), false);
    }

    public Node(Map<String, Property<?>> properties, String id, boolean search) {
        super(search);
        this.id = id;
        this.properties = properties;
        this.links = new ArrayList<>();
    }

    public Node() {
        this(new HashMap<>());
    }

    public void addLink(Link link) {
        links.add(link);
    }

    /**
     * Gets the node type name (i.e. Concept, Instance)
     *
     * @return Node type name
     */
    public abstract String getName();

    /**
     * Returns all of the connected links of the node
     * which equals to the one passed in parameter.
     *
     * @param link Link            link to compare
     * @return List<Link>
     */
    public List<Link> getLinks(Link link) {
        List<Link> links = new ArrayList<>();
        for (Link lnk : this.links)
            if (link.isSameLink(lnk))
                links.add(lnk);
        return links;
    }

    /**
     * Returns all of the connected links of the node
     * which equals to the one passed in parameter.
     *
     * @param other Node
     * @param relation Link
     * @return List<Link>
     */
    public List<Link> getMatchingLinks(Node other, Link relation) {
        List<Link> links = new ArrayList<>();
        for (Link link : this.links)
            if (relation.isSubsetOf(link))
                links.add(link);

        return links;
    }

    /**
     * Returns all of the connected links of the node
     * which are of the same class as the one passed in parameter.
     *
     * @param linkClass Class           class to compare
     * @return List<Link>
     */
    public List<Link> getLinks(Class<? extends Link> linkClass) {
        List<Link> links = new ArrayList<>();
        for (Link link : this.links) {
            if (link.getClass().equals(linkClass)) {
                links.add(link);
            }
        }
        return links;
    }

    /**
     * Removes the node attached link passed in parameters.
     *
     * @param link Link            Link to remove
     */
    public void removeLink(Link link) {
        this.links.remove(link);
    }


    @Override
    public String toString() {
        if (properties.get("name") != null) {
            return (String) properties.get("name").getValue();
        }
        return "#" + getId();
    }

    public String toDetailedString() {
        StringBuilder res = new StringBuilder();

        res.append("id : ").append(this.id).append("\n");

        Object[] keys = this.properties.keySet().toArray();
        Object[] values = this.properties.values().toArray();
        for (int i = 0; i < keys.length; ++i) {
            res.append(keys[i]).append(" : ").append(((Property<?>) values[i]).getValue()).append("\n");
        }

        res.append("Links:\n");
        for (Link link : getLinks()) {
            res.append("\t").append(link.toDetailedString(this)).append("\n");
        }

        return res.toString();
    }

    public List<Link> getLinks() {
        return this.links;
    }

    public Map<String, Property<?>> getProperties() {
        return properties;
    }

    public String getId() {
        return id;
    }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        JSONObject content = new JSONObject();

        for (String key : properties.keySet()) {
            content.put(key, properties.get(key).getValue());
        }

        obj.put("id", getId());
        obj.put("type", JSONObject.NULL);
        obj.put("content", content);
        return obj;
    }

    /**
     * @param obj JSON object
     * @return Node from JSON object
     * @throws JSONException Bad JSON
     */
    public static Node fromJSONObject(JSONObject obj) throws JSONException {
        Map<String, Property<?>> properties = getPropertiesFromJSON(obj.getJSONObject("content"));
        String id = obj.getString("id");

        boolean search = false;
        try {
            search = obj.getBoolean("search");
        } catch (JSONException ignored) {}

        switch (obj.getString("type")) {
            case "CONCEPT":
                return new ConceptNode(properties, id, search);

            case "INSTANCE":
                return new InstanceNode(properties, id, search);

            default:
                throw new JSONException("Invalid type");
        }
    }

    /**
     * @param content JSON object
     * @return Map
     */
    protected static Map<String, Property<?>> getPropertiesFromJSON(JSONObject content) {
        Map<String, Property<?>> properties = new HashMap<>();
        for (String key : content.keySet()) {
            properties.put(key, new Property<>(content.get(key)));
        }
        return properties;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Node)
            return id.equals(((Node) other).id);

        return false;
    }

    public boolean isIdentical(Node other) {
        return properties.equals(other.properties);
    }

    public boolean isSubsetOf(Node other) {
        if (!getClass().isInstance(other))
            return false;

        for (String key : properties.keySet()) {
            if (other.properties.get(key) == null || !other.properties.get(key).equals(properties.get(key)))
                return false;
        }

        return true;
    }

    public List<Node> getNeighbours() {
        List<Node> neighbours = new ArrayList<>();
        for (Link link : links)
            if (link.getFrom().equals(this))
                neighbours.add(link.getTo());
            else
                neighbours.add(link.getFrom());
        return neighbours;
    }

    public static void setNextId(int nextId) {
        NEXT_ID = nextId;
    }
}
