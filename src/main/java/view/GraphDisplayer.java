package view;

import model.KnowledgeGraph;
import model.link.Link;
import model.node.Node;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import java.util.HashMap;

public class GraphDisplayer {

    private static Viewer viewer;

    /**
     * Displays the graph
     * @param graph             KnowledgeGraph  Knowledge graph to display
     */
    public static void displayGraph(KnowledgeGraph graph) {
        Graph convertedGraph = convertGraph(graph);
        convertedGraph.setAttribute("ui.quality");
        convertedGraph.setAttribute("ui.antialias");

        System.setProperty("org.graphstream.ui", "swing");
        if (viewer != null) {
            viewer.getDefaultView().close(viewer.getGraphicGraph());
        }

        viewer = convertedGraph.display();
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

    private static String getCSS() {
        return "node {" +
                "text-background-mode: plain;" +
                "}";
    }
}
