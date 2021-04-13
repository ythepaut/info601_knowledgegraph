package model;

import exceptions.IllegalLinkAssociationException;
import exceptions.NoLinkedNodeException;
import model.link.Link;
import model.node.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class KnowledgeGraph {
    private final List<Node> nodes;
    private final List<Link> links;
    private boolean inherit;

    public KnowledgeGraph(boolean inherit) {
        this.inherit = inherit;
        nodes = new ArrayList<>();
        links = new ArrayList<>();
    }

    /**
     * Adds nodes to the graph
     *
     * @param nodes Node[]          Nodes to add
     */
    public void addNodes(Node... nodes) {
        for (Node node : nodes)
            if (!this.nodes.contains(node))
                this.nodes.add(node);
    }

    /**
     * Removes nodes from the graph
     *
     * @param nodes Node[]          Nodes to remove
     */
    public void removeNodes(Node... nodes) {
        this.nodes.removeAll(Arrays.asList(nodes));
    }

    public void addLink(Link link) {
        links.add(link);
    }

    /**
     * Initializes and adds a link to the graph
     *
     * @param nodeFrom Node            Link's origin node
     * @param nodeTo   Node            Link's destination node
     * @param link     Link            Link to add
     */
    public boolean addLink(Node nodeFrom, Node nodeTo, Link link) {
        try {
            link.setFrom(nodeFrom);
            link.setTo(nodeTo);
            nodeTo.addLink(link);
            nodeFrom.addLink(link);
            addNodes(nodeFrom, nodeTo);
            links.add(link);
            if (inherit)
                link.checkInheritProperties(nodeFrom, nodeTo);
            return true;
        } catch (IllegalLinkAssociationException e) {
            System.out.println("Error: illegal association");
            return false;
        }
    }

    /**
     * Delete and adds a link to the graph
     *
     * @param nodeFrom Node            Link's origin node
     * @param nodeTo   Node            Link's destination node
     * @param link     Link            Link to delete
     */
    public void removeLink(Node nodeFrom, Node nodeTo, Link link) {
        List<Link> links = nodeFrom.getLinks();
        Link myLink = null;
        for (Link linkList : links) {
            if (linkList.getClass() == link.getClass() && linkList == link) {
                try {
                    if (linkList.getLinkedNode(nodeFrom) == nodeTo) {
                        myLink = link;
                    }
                } catch (NoLinkedNodeException e) {
                    e.printStackTrace();
                }
            }
        }
        nodeFrom.removeLink(myLink);
        nodeTo.removeLink(myLink);
        links.remove(myLink);
    }

    /**
     * Deletes the link in argument form the graph.
     *
     * @param linkToRemove   Link        link to remove
     * @param deleteSameType boolean     if true, all link of the same type as linkToRemove will be removed
     * @return true if the link has been deleted
     */
    public boolean removeLink(Link linkToRemove, boolean deleteSameType) {

        // find the true link to remove in model
        Link linkToRemoveRaw = null;
        for (Link link : this.links) {
            if (link.getName().equals(linkToRemove.getName()) &&
                    link.getClass().equals(linkToRemove.getClass()) &&
                    link.getTo().equals(linkToRemove.getTo()) &&
                    link.getFrom().equals(linkToRemove.getFrom())) {
                    linkToRemoveRaw = link;
                    break;
            }
        }

        if (linkToRemoveRaw != null) {

            // node references deletion
            linkToRemoveRaw.getFrom().removeLink(linkToRemoveRaw);
            linkToRemoveRaw.getTo().removeLink(linkToRemoveRaw);

            // global list deletion
            this.links.remove(linkToRemoveRaw);

            // all other link of the same type deletion
            if (deleteSameType) {
                List<Link> linksToRemove = new ArrayList<>();

                for (Link link : this.links) {
                    // the link will be removed if it has the same class
                    // as the one passed in parameter
                    if ( link.getClass().isInstance(linkToRemoveRaw) ){
                        linksToRemove.add(link);
                    }
                }
                // we remove definitely the links in a new loop to avoid border effect
                for (Link link : linksToRemove) {
                    link.getFrom().removeLink(link);
                    link.getTo().removeLink(link);
                    this.links.remove(link);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Find all nodes in graph that matches the given properties
     *
     * @param properties Map<>           Properties filter
     * @return Nodes matching the filter
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
     *
     * @param properties Map<>           Properties filter
     * @return Nodes matching the filter
     */
    public List<Node> findNodes(Map<String, Property<?>> properties, Class<? extends Node> nodeType) {
        List<Node> found = new ArrayList<>();
        for (Node node : findNodes(properties))
            if (nodeType.isInstance(node))
                found.add(node);
        return found;
    }

    public List<Link> findLinks(Link template) {
        List<Link> found = new ArrayList<Link>();

        for (Link link : template.getTo().getLinks()) {
            try {
                if (link.getLinkedNode(template.getTo()).equals(template.getFrom())) {
                    found.add(link);
                }
            } catch (NoLinkedNodeException e) { }
        }

        return found;
    }

    /**
     * Find a node according to its id
     *
     * @param id Node ID
     * @return Corresponding node
     */
    public Node findNode(String id) {
        for (Node node : getNodes())
            if (node.getId().equals(id))
                return node;
        return null;
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

    public List<Link> getLinks() {
        return links;
    }

    public Link getLink(String id) {
        for (Link link : links) {
            if (link.getId().equals(id)) {
                return link;
            }
        }
        return null;
    }

    /**
     * @return JSON graph
     */
    public String toJSON() {
        JSONObject obj = new JSONObject();

        JSONArray nodes = new JSONArray();
        for (Node node : getNodes()) {
            nodes.put(node.toJSONObject());
        }
        obj.put("nodes", nodes);

        JSONArray links = new JSONArray();
        for (Link link : getLinks()) {
            links.put(link.toJSONObject());
        }
        obj.put("links", links);

        return obj.toString();
    }

    /**
     * @param json JSON graph
     * @return Graph
     * @throws JSONException Bad JSON
     */
    public static KnowledgeGraph fromJSON(String json, boolean inherit) throws JSONException {
        KnowledgeGraph graph = new KnowledgeGraph(inherit);
        JSONObject main = new JSONObject(json);

        int maxId = 0;

        JSONArray nodes = main.getJSONArray("nodes");
        for (Object nodeObject : nodes) {
            Node node = Node.fromJSONObject((JSONObject) nodeObject);
            graph.addNodes(node);
            if (Integer.parseInt(node.getId()) > maxId) {
                maxId = Integer.parseInt(node.getId());
            }
        }
        Node.setNextId(maxId + 1);

        JSONArray links = main.getJSONArray("links");
        for (Object linkObject : links) {
            JSONObject linkJSON = (JSONObject) linkObject;
            Link link = Link.fromJSONObject(linkJSON);
            Node from = graph.findNode(linkJSON.getString("from"));
            Node to = graph.findNode(linkJSON.getString("to"));
            graph.addLink(from, to, link);
        }

        return graph;
    }

    public KnowledgeGraph search(KnowledgeGraph searchedGraph) {
        KnowledgeGraph resultGraph = new KnowledgeGraph(false);

        for (Node searchedNode : searchedGraph.nodes) {
            if (!searchedNode.isSearched())
                continue;

            for (Link link : searchedGraph.links) {
                Node foundNodeInSearchedGraph;
                try {
                    foundNodeInSearchedGraph = link.getLinkedNode(searchedNode);
                } catch (Exception e) {
                    continue;
                }

                for (Node matchedNodeInOriginalGraph : nodes) {
                    if (!foundNodeInSearchedGraph.isSubsetOf(matchedNodeInOriginalGraph))
                        continue;

                    resultGraph.addNodes(matchedNodeInOriginalGraph);
                    System.out.println(matchedNodeInOriginalGraph);

                    for (Link possibleRelation : searchedNode.getLinks()) {
                        List<Link> matchingLinks = matchedNodeInOriginalGraph.getMatchingLinks(searchedNode, possibleRelation);

                        for (Link matchingLink : matchingLinks) {
                            try {
                                Node otherExtremity = matchingLink.getLinkedNode(matchedNodeInOriginalGraph);
                                System.out.println(otherExtremity);
                                resultGraph.addNodes(otherExtremity);
                                resultGraph.addLink(matchingLink);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        // lmao hacky af
        return KnowledgeGraph.fromJSON(resultGraph.toJSON(), true);
    }

    /**
     * Depth-First Search
     *
     * @param origin   Node from which we start
     * @param occurred Nodes we already ran through
     */
    public List<Node> depthFirstSearch(Node origin, Node destination, List<Node> occurred) {

        if (origin.equals(destination))
            return occurred;

        if (occurred == null)
            occurred = new ArrayList<>();
        occurred.add(origin);

        for (Link link : origin.getLinks())
            if (link.getFrom().equals(origin))
                if (!occurred.contains(link.getTo()))
                    return depthFirstSearch(link.getTo(), destination, occurred);

        return null;
    }

    public boolean shouldInherit() {
        return inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }
}
