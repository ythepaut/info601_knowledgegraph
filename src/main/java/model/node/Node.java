package model.node;

import model.Property;
import model.link.Link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Node {
    private static int NEXT_ID = 0;

    private final List<Link> links;
    private final Map<String, Property<?>> properties;

    private final String id;

    public Node(HashMap<String, Property<?>> properties) {
        this.id = Integer.toString(Node.NEXT_ID++);
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
        return getId();
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
}
