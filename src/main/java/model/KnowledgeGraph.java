package model;

import exceptions.IllegalLinkAssociationException;
import exceptions.NoLinkedNodeException;
import model.link.Link;
import model.node.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KnowledgeGraph {

    private final List<Node> nodes;

    public KnowledgeGraph() {
        nodes = new ArrayList<>();
    }


    /**
     * Adds nodes to the graph
     * @param nodes             Node[]          Nodes to add
     */
    public void addNodes(Node... nodes) {
        for (Node node : nodes)
            if (!this.nodes.contains(node))
                this.nodes.add(node);
    }

    /**
     * Removes nodes from the graph
     * @param nodes             Node[]          Nodes to remove
     */
    public void removeNodes(Node... nodes) {
        this.nodes.removeAll(Arrays.asList(nodes));
    }

    /**
     * Initializes and adds a link to the graph
     * @param nodeFrom          Node            Link's origin node
     * @param nodeTo            Node            Link's destination node
     * @param link              Link            Link to add
     */
    public void addLink(Node nodeFrom, Node nodeTo, Link link) {
        try {
            link.setFrom(nodeFrom);
            link.setTo(nodeTo);
            nodeTo.addLink(link);
            nodeFrom.addLink(link);
            addNodes(nodeFrom, nodeTo);
        } catch (IllegalLinkAssociationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete and adds a link to the graph
     * @param nodeFrom          Node            Link's origin node
     * @param nodeTo            Node            Link's destination node
     * @param linkType          Class           Link type to delete
     */
    public void removeLink(Node nodeFrom, Node nodeTo, Class<? extends Link> linkType) {
        try {
            List<Link> links = nodeFrom.getLinks();
            Link myLink = null;
            for (Link link : links) {
                if (link.getClass() == linkType) {
                    try {
                        if (link.getLinkedNode(nodeFrom) == nodnodeTo) {
                            myLink = link;
                        }
                    } catch (NoLinkedNodeException e) {
                        e.printStackTrace();
                    }
                }
            }
            nodeFrom.removeLink(myLink);
            nodeTo.removeLink(myLink);
        } catch (IllegalLinkAssociationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find all nodes in graph that matches the given properties
     * @param properties        Map<>           Properties filter
     * @return                                  Nodes matching the filter
     */
    public List<Node> findNodes(Map<String, Property<?>> properties) {
        List<Node> found = new ArrayList<>();

        for (Node node : nodes) {

            // Comparing properties
            boolean match = true;
            for (String propertyName : properties.keySet()) {
                if (node.getProperties().get(propertyName) == null ||
                        !node.getProperties().get(propertyName).equals(properties.get(propertyName))) {
                    match = false;
                    break;
                }
            }

            // Adding to return list if properties match
            if (match) {
                found.add(node);
            }
        }

        return found;
    }

    /**
     * Find all nodes in graph that matches the given properties
     * for a given node type (instance)
     * @param properties        Map<>           Properties filter
     * @return                                  Nodes matching the filter
     */
    public List<Node> findNodes(Map<String, Property<?>> properties, Class<? extends Node> nodeType) {
        List<Node> found = new ArrayList<>();
        for (Node node : findNodes(properties))
            if (node.getClass().isAssignableFrom(nodeType)) // FIXME: might not work
                found.add(node);
        return found;
    }


    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("KnowledgeGraph :\n");
        for (Node node : this.nodes) {
            for (Link link : node.getLinks()) {
                if (link.getFrom().equals(node)) {
                    res.append(node)
                            .append(" ---[ ")
                            .append(link.getName())
                            .append(" ]--> ")
                            .append(link.getTo().toString())
                            .append("\n");
                }
            }
        }
        return res.toString();
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
