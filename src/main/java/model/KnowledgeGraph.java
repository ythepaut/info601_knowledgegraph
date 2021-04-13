package model;

import exceptions.IllegalLinkAssociationException;
import exceptions.NoLinkedNodeException;
import model.link.Link;
import model.node.Node;

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

    public KnowledgeGraph() {
        nodes = new ArrayList<>();
        links = new ArrayList<>();
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

    public void addLink(Link link) {
        links.add(link);
    }

    /**
     * Initializes and adds a link to the graph
     * @param nodeFrom          Node            Link's origin node
     * @param nodeTo            Node            Link's destination node
     * @param link              Link            Link to add
     */
    public boolean addLink(Node nodeFrom, Node nodeTo, Link link) {
        try {
            link.setFrom(nodeFrom);
            link.setTo(nodeTo);
            nodeTo.addLink(link);
            nodeFrom.addLink(link);
            addNodes(nodeFrom, nodeTo);
            links.add(link);
            return true;
        } catch (IllegalLinkAssociationException e) {
            System.out.println("Error: illegal association");
            return false;
        }
    }

    /**
     * Delete and adds a link to the graph
     * @param nodeFrom          Node            Link's origin node
     * @param nodeTo            Node            Link's destination node
     * @param link              Link            Link to delete
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
     * @param linkToRemove      Link        link to remove
     * @param deleteSameType    boolean     if true, all link of the same type as linkToRemove will be removed
     * @return                              true if the link has been deleted
     */
    public boolean removeLink(Link linkToRemove, boolean deleteSameType) {
        if (this.links.contains(linkToRemove)) {

            // node references deletion
            linkToRemove.getFrom().removeLink(linkToRemove);
            linkToRemove.getTo().removeLink(linkToRemove);

            // global list deletion
            this.links.remove(linkToRemove);

            // all other link of the same type deletion
            if (deleteSameType) {
                List<Link> linksToRemove = new ArrayList<>();

                for (Link link : this.links) {
                    // the link will be removed if it has the same class
                    // as the one passed in parameter
                    if ( link.getClass().isInstance(linkToRemove) ){
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
            if (nodeType.isInstance(node))
                found.add(node);
        return found;
    }

    /**
     * Find a node according to its id
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
    public static KnowledgeGraph fromJSON(String json) throws JSONException {
        KnowledgeGraph graph = new KnowledgeGraph();
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

    public static KnowledgeGraph fromFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String currentLine;
        StringBuilder total = new StringBuilder();
        while ((currentLine = reader.readLine()) != null) {
            total.append(currentLine);
        }

        return KnowledgeGraph.fromJSON(total.toString());
    }

    public KnowledgeGraph search(KnowledgeGraph searchedGraph) {
        KnowledgeGraph resultGraph = new KnowledgeGraph();

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

        /*
        System.out.println("");

        for (Node node : resultGraph.nodes) {
            System.out.println(node);
        }

        System.out.println("");

        for (Link link : resultGraph.links) {
            System.out.println(link.getFrom().getId());
            System.out.println(link.getTo().getId());
        }

        System.out.println("");
        */

        return resultGraph;
    }

    public KnowledgeGraph path(Node origin, Node destination) {
        return new KnowledgeGraph();
    }
}
