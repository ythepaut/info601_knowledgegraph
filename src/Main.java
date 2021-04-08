import model.KnowledgeGraph;
import model.node.ConceptNode;

public class Main {

    private static KnowledgeGraph constructGraph() {
        KnowledgeGraph graph = new KnowledgeGraph();

        ConceptNode conceptNode = new ConceptNode();

        return graph;
    }

    public static void main(String[] args) {

        KnowledgeGraph graph = constructGraph();

        System.out.println(graph);

    }

}
