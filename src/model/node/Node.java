package model.node;

import model.Property;
import model.link.Link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Node {

    private final List<Link> links;
    private final Map<String, Property<?>> properties;

    public Node() {
        this.links = new ArrayList<>();
        this.properties = new HashMap<>();
    }

    /**
     * Returns all of the connected links of the node
     * which equals to the one passed in parameter.
     * @param link              Link            link to compare
     * @return                  List<Link>
     */
    public List<Link> getLinks(Link link) {
        List<Link> links = new ArrayList<>();
        for (Link l : this.links) {
            if (l.equals(link)) {
                links.add(link);
            }
        }
        return links;
    }

    /**
     * Returns all of the connected links of the node
     * which are of the same class as the one passed in paramater.
     * @param linkClass         Class           class to compare
     * @return                  List<Link>
     */
    public List<Link> getLinks(Class linkClass) {
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
     * @param link
     */
    public void removeLink(Link link) {
        for(Link l : this.links) {
            if (l.equals(link)){
                this.links.remove(this.links.indexOf(l));
            }
        }
    }


    public List<Link> getLinks() {
        return this.links;
    }

    public Map<String, Property<?>> getProperties() {
        return properties;
    }
}
