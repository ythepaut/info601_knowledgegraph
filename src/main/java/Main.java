import controller.QueryInterpretor;
import model.KnowledgeGraph;
import model.Property;
import model.link.CompositionLink;
import model.link.InstanceLink;
import model.node.ConceptNode;
import model.node.InstanceNode;
import model.node.Node;
import utils.FileManager;
import view.GraphDisplayer;

import java.io.IOException;
import java.util.List;
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
        propsDoliprane.put("proprete", new Property<>(1));
        propsDoliprane.put("quantite", new Property<>(90));
        InstanceNode doliprane = new InstanceNode(propsDoliprane);
        graph.addNodes(doliprane);

        // Médoc
        Map<String, Property<?>> propsMedoc = new HashMap<>();
        name = new Property<>("Medoc");
        propsMedoc.put("name", name);
        InstanceNode medoc = new InstanceNode(propsMedoc);
        graph.addNodes(medoc);

        // Antalgic ako Doliprane
        InstanceLink linkAntalgicDoliprane = new InstanceLink();
        graph.addLink(antalgic, doliprane, linkAntalgicDoliprane);

        // Antalgic ako Médoc
        InstanceLink linkAntalgicMedoc = new InstanceLink();
        graph.addLink(antalgic, medoc, linkAntalgicMedoc);

        // graph.removeLink(linkAntalgicMedoc, true); // test suppression

        return graph;
    }

    public static void main(String[] args) throws IOException {
        KnowledgeGraph hardcodedGraph = KnowledgeGraph.fromJSON(FileManager.readFile("./graph.json"));
        KnowledgeGraph searchGraph = KnowledgeGraph.fromJSON(FileManager.readFile("./searchGraph.json"));
        KnowledgeGraph resultGraph = hardcodedGraph.search(searchGraph);

        GraphDisplayer.displayGraph(resultGraph);

        KnowledgeGraph graph = new KnowledgeGraph();
        QueryInterpretor queryInterpretor = new QueryInterpretor(graph);
        queryInterpretor.queryListener();
    }
}
