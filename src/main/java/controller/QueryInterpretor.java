package controller;

import model.KnowledgeGraph;
import model.Property;
import model.node.*;
import model.link.*;
import scala.collection.mutable.HashMap$;
import view.GraphDisplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class QueryInterpretor {

    private final KnowledgeGraph graph;

    public QueryInterpretor(KnowledgeGraph graph) {
        this.graph = graph;
    }

    /**
     * Listens to user inputs
     */
    public void queryListener() {

        System.out.println("Enter your queries. (Type \"help\" for help)");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String rawQuery;
        do {
            System.out.print("\n> ");
            try {
                rawQuery = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            String[] args = rawQuery.split("[ ]+");
            executeQuery(args[0], Arrays.copyOfRange(args, 1, args.length));

        } while (!rawQuery.equals("exit"));
        System.out.println("bye <3");
    }

    /**
     * Process a query
     * @param cmd               String          Command
     * @param args              String[]        Command's arguments
     */
    private void executeQuery(String cmd, String[] args) {
        if (cmd.equals("help")) {
            printHelp();
        } else if (cmd.equals("node") && args[0].equals("add") && !args[1].equals("")) {
            HashMap<String, Property<?>> properties = new HashMap<>();
            String id = args[1];
            getNextProperties(properties, args, 2);

            Node node;

            if (args[1].equalsIgnoreCase("concept")) {
                node = new ConceptNode(properties, id);
            } else if (args[1].equalsIgnoreCase("instance")) {
                node = new InstanceNode(properties, id);
            } else {
                System.out.println("Error: " + args[1] + " is not a Node type");
                return;
            }

            graph.addNodes(node);
            System.out.println("Successfully created node " + node);
        } else if (cmd.equals("node") && args[0].equals("del")) {
            HashMap<String, Property<?>> properties = new HashMap<>();
            List<Node> nodes = new ArrayList<Node>();
            //
            if (args[1].split(":").length > 0) {
                nodes.add(graph.findNode(args[1]));
            } else {
                nodes = graph.findNodes(properties, Node.class);
            }
            getNextProperties(properties, args, 2);

            for (Node node : nodes) {
                graph.removeNodes(node);
            }

            System.out.println("Deleted " + nodes.size() + " node(s).");
        } else if (cmd.equals("link") && args[0].equals("add")) {
            int nodeIdIndex = 2;
            Link link;
            if (args[1].equalsIgnoreCase("ako")) {
                link = new AssociationLink();
            } else if (args[1].equalsIgnoreCase("association")) {
                link = new AssociationLink();
            } else if (args[1].equalsIgnoreCase("composition")) {
                if (args[2].equalsIgnoreCase("oriented")) {
                    link = new CompositionLink(true);
                } else if (args[2].equalsIgnoreCase("nonoriented")) {
                    link = new CompositionLink(false);
                } else {
                    System.out.println("Error: no orientation specified");
                    return;
                }
                nodeIdIndex++;
            } else if (args[1].equalsIgnoreCase("instance")) {
                link = new InstanceLink();
            } else {
                System.out.println("Error: no valid TypeLink specified");
                return;
            }
            HashMap<String, Property<?>> properties = new HashMap<>();
            properties.put("id", new Property<>(args[nodeIdIndex]));
            List<Node> firstNode = graph.findNodes(properties);
            properties = new HashMap<>();
            properties.put("id", new Property<>(args[nodeIdIndex+1]));
            List<Node> secondNode = graph.findNodes(properties);

            if (firstNode.size() > 1 || secondNode.size() > 1) {
                System.out.println("Error: arguments can be interpreted in several ways");
            } else {
                graph.addLink(firstNode.get(0), secondNode.get(0), link);
            }

        } else if (cmd.equals("link") && args[0].equals("del")) {

        } else if (cmd.equals("display")) {
            GraphDisplayer.displayGraph(graph);
        }
    }

    private static void getNextProperties(HashMap<String, Property<?>> base, String[] args, int basePointer) {
        HashMap<String, Property<?>> toAdd = getNextProperties(args, basePointer);
        for (int i = 0; i < toAdd.keySet().size(); ++i) {
            base.put((String) toAdd.keySet().toArray()[i], (Property) toAdd.values().toArray()[i]);
        }
    }

    private static HashMap<String, Property<?>> getNextProperties (String[] args, int basePointer) {
        if (args.length <= basePointer) {
            return null;
        }

        HashMap<String, Property<?>> res = new HashMap<>();

        for (int i = basePointer; i < args.length; ++i) {
            String[] parsedString = args[i].split(":");
            if (parsedString.length > 2 || parsedString.length <= 0) {
                System.out.println("Error: bad query arguments");
            }
            res.put(parsedString[0], new Property<>(parsedString[1]));
        }

        return res;
    }

    private static void printHelp() {
        String[] helpString = {
                "Aide commandes :",
                "",
                "help",
                "node add <NodeType> <ID> [Attribute name]:[Attribute value]",
                "node del <ID>",
                "link add <LinkType> [LinkProperties] <IDNode1> <IDNode2>",
                "link del <LinkType> <IDNode1> <IDNode2>",
                "display",
                "exit"
        };

        for (String str : helpString) {
            System.out.println(str);
        }
    }

}
