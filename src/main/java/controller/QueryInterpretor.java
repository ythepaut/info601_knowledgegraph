package controller;

import exceptions.IllegalLinkAssociationException;
import model.KnowledgeGraph;
import model.Property;
import model.node.*;
import model.link.*;
import scala.collection.mutable.HashMap$;
import view.GraphDisplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
            addNode(args);
        } else if (cmd.equals("node") && args[0].equals("del")) {
            deleteNode(args);
        } else if (cmd.equals("node") && args[0].equals("find")) {
            findNode(args);
        } else if (cmd.equals("link") && args[0].equals("add")) {
            addLink(args);
        } else if (cmd.equals("link") && args[0].equals("del")) {
            deleteLink(args);
        } else if (cmd.equals("display")) {
            GraphDisplayer.displayGraph(graph);
        } else {
            System.out.println("Error: no suitable command found");
        }
    }

    private void addNode(String[] args) {
        HashMap<String, Property<?>> properties = new HashMap<>();
        getNextProperties(properties, args, 2);

        Node node;
        if (args[1].equalsIgnoreCase("concept")) {
            node = new ConceptNode(properties);
        } else if (args[1].equalsIgnoreCase("instance")) {
            node = new InstanceNode(properties);
        } else {
            System.out.println("Error: " + args[1] + " is not a Node type");
            return;
        }

        graph.addNodes(node);
        System.out.println("Successfully created node " + node);
    }

    private void deleteNode(String[] args) {
        HashMap<String, Property<?>> properties = new HashMap<>();
        List<Node> nodes = new ArrayList<>();

        if (args[1].split(":").length != 2) {
            nodes.add(graph.findNode(args[1]));
        } else {
            getNextProperties(properties, args, 1);
            nodes = graph.findNodes(properties, Node.class);
        }

        if (nodes.size() > 0 && nodes.get(0) != null) {
            for (Node node : nodes) {
                graph.removeNodes(node);
            }
        } else {
            System.out.println("Error: no suitable nodes found " + nodes.size());
            return;
        }

        System.out.println("Deleted " + nodes.size() + " node(s).");
    }

    private void findNode(String[] args) {
        List<Node> nodes = new ArrayList<>();
        if (args[1].split(":").length != 2) {
            nodes.add(graph.findNode(args[1]));
        } else {
            HashMap<String, Property<?>> properties = new HashMap<>();
            getNextProperties(properties, args, 1);
            nodes = graph.findNodes(properties, Node.class);
        }

        if (nodes != null && nodes.size() > 0 && nodes.get(0) != null) {
            System.out.println("Corresponding Nodes\n===================");
            for (Node node : nodes) {
                System.out.print(node.toDetailedString());
                System.out.println("===================");
            }
        } else {
            System.out.println("Error: no corresponding node found");
        }
    }

    private void addLink(String[] args) {
        if (args.length < 4) {
            System.out.println("Error: not enough arguments");
            return;
        }
        int nodeIdIndex = 2;
        Link link;
        if (args[1].equalsIgnoreCase("ako")) {
            link = new AkoLink();
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
            if (args.length < 5) {
                System.out.println("Error: not enough arguments");
                return;
            }
            nodeIdIndex++;
        } else if (args[1].equalsIgnoreCase("instance")) {
            link = new InstanceLink();
        } else {
            System.out.println("Error: no valid TypeLink specified");
            return;
        }

        Node firstNode = graph.findNode(args[nodeIdIndex]);
        Node secondNode = graph.findNode(args[nodeIdIndex+1]);

        if (args.length > nodeIdIndex+2) {
            link.setName(args[nodeIdIndex+2]);
        }

        if (firstNode == null || secondNode == null) {
            System.out.println("Error: no corresponding nodes found");
        } else {
            if (graph.addLink(firstNode, secondNode, link)) {
                System.out.println("Success adding link between " + firstNode + " " + secondNode);
            }
        }
    }

    private void deleteLink(String[] args) {
        if (args.length < 4) {
            System.out.println("Error: not enough arguments");
            return;
        }

        Link link = null;
        int nodeIdIndex = 2;
        if (args[1].equalsIgnoreCase("ako")) {
            link = new AkoLink();
        } else if (args[1].equalsIgnoreCase("association")) {
            link = new AssociationLink();
        } else if (args[1].equalsIgnoreCase("composition")) {
            if (args.length < 5) {
                System.out.println("Error: not enough arguments");
                return;
            }
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

        Node firstNode = graph.findNode(args[nodeIdIndex]);
        Node secondNode = graph.findNode(args[nodeIdIndex+1]);

        boolean deleteAll = false;
        if (args.length >= nodeIdIndex+3) {
            link.setName(args[nodeIdIndex+2]);
        } else {
            deleteAll = true;
        }

        if (secondNode == null || firstNode == null) {
            System.out.println("Error: no nodes corresponding");
            return;
        }

        try {
            link.setFrom(firstNode);
            link.setTo(secondNode);
        } catch (IllegalLinkAssociationException e) {
            System.out.println("Error: illegal association");
            return;
        }

        //TODO: lfkjgdlgfhjogidsfjghlkerjdfrsmlgvjxclkgjdflkgjfdjkgjfdlkgjfdlkjglkfdjglkfdjglkfdjtoirtpdspldù
        //graph.todoLuchat();
        System.out.println("Success deleted link between " + firstNode + " " + secondNode);
    }

    private static void getNextProperties(HashMap<String, Property<?>> base, String[] args, int basePointer) {
        HashMap<String, Property<?>> toAdd = getNextProperties(args, basePointer);
        if (toAdd != null) {
            for (int i = 0; i < toAdd.size(); ++i) {
                base.put((String) toAdd.keySet().toArray()[i], (Property<?>) toAdd.values().toArray()[i]);
            }
        }
    }

    private static HashMap<String, Property<?>> getNextProperties (String[] args, int basePointer) {
        if (args.length <= basePointer) {
            return null;
        }

        HashMap<String, Property<?>> res = new HashMap<>();

        for (int i = basePointer; i < args.length; ++i) {
            String[] parsedString = args[i].split(":");
            if (parsedString.length != 2) {
                System.out.println("Error: bad query arguments");
            }
            res.put(parsedString[0], new Property<>(parsedString[1]));
        }

        return res;
    }

    private static void printHelp() {
        String[] helpString = {
                "Query help :",
                "",
                "help",
                "node add <NodeType> [Attribute name]:[Attribute value]",
                "node del <ID> [Attribute1 name]:[Attribute1 value] [Attribute2 name]:[Attribute2 value]...",
                "node find <ID> [Attribute1 name]:[Attribute1 value] [Attribute2 name]:[Attribute2 value]...",
                "link add <LinkType> [Link Mandatory Property] <IDNode1> <IDNode2> [LinkName]",
                "link del <LinkType> [Link Mandatory Property] <IDNode1> <IDNode2> [LinkName]",
                "find ",
                "display",
                "exit"
        };

        for (String str : helpString) {
            System.out.println(str);
        }
    }

}
