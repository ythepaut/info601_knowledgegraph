package view;

import model.KnowledgeGraph;
import model.link.Link;
import model.node.Node;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.HashMap;

public class GraphDisplayer {

    /**
     * Displays the graph
     * @param graph             KnowledgeGraph  Knowledge graph to display
     */
    public static void displayGraph(KnowledgeGraph graph) {
        System.setProperty("org.graphstream.ui", "swing");
        convertGraph(graph).display().getDefaultView();
    }

    /**
     * Converts the graph from the project model to the library model
     * @param graph             KnowledgeGraph  Our graph from the project
     * @return                                  Graph (GraphStream's model)
     */
    private static Graph convertGraph(KnowledgeGraph graph) {

        Graph result = new SingleGraph("KnowledgeGraph");

        HashMap<String, org.graphstream.graph.Node> resultNodes = new HashMap<>();

        for (Node node : graph.getNodes()) {
            org.graphstream.graph.Node resultNode = result.addNode(node.getId());
            resultNode.setAttribute("ui.label", node.toString());
            resultNodes.put(node.getId(), resultNode);
        }

        for (Node node : graph.getNodes()) {
            for (Link link : node.getLinks()) {
                if (link.getFrom().equals(node)) {
                    Node from = link.getFrom();
                    Node to = link.getTo();
                    result.addEdge(from.getId() + "-" + to.getId(), resultNodes.get(from.getId()), resultNodes.get(to.getId()));
                }
            }
        }

        return result;
    }
}
