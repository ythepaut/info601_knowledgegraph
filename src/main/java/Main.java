import controller.QueryInterpretor;
import model.KnowledgeGraph;
import model.Property;
import model.link.InstanceLink;
import model.node.ConceptNode;
import model.node.InstanceNode;
import view.GraphDisplayer;

import java.util.HashMap;
import java.util.Map;

public class Main {
    private static KnowledgeGraph constructGraph() {
        KnowledgeGraph graph = new KnowledgeGraph();

        // Antalgic
        Map<String, Property<?>> propsAntalgic = new HashMap<>();
        Property<String> name = new Property<>("Antalgique");
        propsAntalgic.put("name", name);
        ConceptNode antalgic = new ConceptNode(propsAntalgic);
        graph.addNodes(antalgic);

        // Doliprane
        Map<String, Property<?>> propsDoliprane = new HashMap<>();
        name = new Property<>("Doliprane");
        propsDoliprane.put("name", name);
        InstanceNode doliprane = new InstanceNode(propsDoliprane);
        graph.addNodes(doliprane);

        // Antalgic ako Doliprane
        InstanceLink linkAntalgicDoliprane = new InstanceLink();
        graph.addLink(antalgic, doliprane, linkAntalgicDoliprane);

        return graph;
    }

    public static void main(String[] args) {
        /*
        KnowledgeGraph graph = constructGraph();
        GraphDisplayer.displayGraph(graph);

        System.out.println(graph);
        */
        KnowledgeGraph graph = new KnowledgeGraph();
        QueryInterpretor queryInterpretor = new QueryInterpretor(graph);
        queryInterpretor.queryListener();
    }
}
