package model.node;

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
public abstract class Node {
    /**
     * Next free ID
     */
    private static int NEXT_ID = 0;

    private final List<Link> links;
    private final Map<String, Property<?>> properties;

    private final String id;

    public Node(Map<String, Property<?>> properties) {
        this.id = Integer.toString(Node.NEXT_ID++);
        this.properties = properties;
        this.properties.put("id", new Property<>(this.id));
        this.links = new ArrayList<>();
    }

    public Node(Map<String, Property<?>> properties, String id) {
        this.id = id;
        this.properties = properties;
        this.properties.put("id", new Property<>(this.id));
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
     * @return                                  Node tpye name
     */
    public abstract String getName();

    /**
     * Returns all of the connected links of the node
     * which equals to the one passed in parameter.
     * @param link              Link            link to compare
     * @return                  List<Link>
     */
    public List<Link> getLinks(Link link) {
        List<Link> links = new ArrayList<>();
        for (Link l : this.links)
            if (link.isSameLink(link))
                links.add(link);
        return links;
    }

    /**
     * Returns all of the connected links of the node
     * which are of the same class as the one passed in paramater.
     * @param linkClass         Class           class to compare
     * @return                  List<Link>
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
     * @param link              Link            Link to remove
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
        String res = "";

        Object[] keys = this.properties.keySet().toArray();
        Object[] values = this.properties.values().toArray();
        for (int i = 0; i < keys.length; ++i) {
            res += keys[i] + " : " + ((Property) values[i]).getValue() + "\n";
        }

        return res;
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

        switch (obj.getString("type")) {
            case "CONCEPT":
                return new ConceptNode(properties, id);

            case "INSTANCE":
                return new InstanceNode(properties, id);

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
}
