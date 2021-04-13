package controller;

import exceptions.IllegalLinkAssociationException;
import model.KnowledgeGraph;
import model.Property;
import model.node.*;
import model.link.*;
import utils.FileManager;
import view.GraphDisplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class QueryInterpretor {

    private KnowledgeGraph graph;

    private KnowledgeGraph querygraph;

    private boolean query;

    public QueryInterpretor(KnowledgeGraph graph) {
        this.graph = graph;
        this.querygraph = new KnowledgeGraph(false);
        query = false;
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
     *
     * @param cmd  String          Command
     * @param args String[]        Command's arguments
     */
    private void executeQuery(String cmd, String[] args) {
        if (cmd.equals("help")) {
            printHelp();
        } else if (cmd.equals("node") && args.length > 0 && args[0].equals("add") && !args[1].equals("")) {
            addNode(args);
        } else if (cmd.equals("node") && args.length > 0 && args[0].equals("del")) {
            deleteNode(args);
        } else if (cmd.equals("node") && args.length > 0 && args[0].equals("find")) {
            findNode(args);
        } else if (cmd.equals("node") && args.length > 0 && args[0].equals("list")) {
            listNode();
        } else if (cmd.equals("link") && args.length > 0 && args[0].equals("add")) {
            addLink(args);
        } else if (cmd.equals("link") && args.length > 0 && args[0].equals("del")) {
            deleteLink(args);
        } else if (cmd.equals("link") && args.length > 0 && args[0].equals("find")) {
            findLink(args);
        } else if (cmd.equals("link") && args.length > 0 && args[0].equals("list")) {
            linkList();
        } else if (cmd.equals("switchGraph")) {
            switchGraph();
        } else if (cmd.equals("findQuery")) {
            System.out.println("trying to find the patterns with the query Knowledge Graph ...");
            if (query) {
                GraphDisplayer.displayGraph(querygraph.search(graph));
            } else {
                GraphDisplayer.displayGraph(graph.search(querygraph));
            }
        } else if (cmd.equals("clear")) {
            if (query) {
                graph = new KnowledgeGraph(false);
            } else {
                querygraph = new KnowledgeGraph(false);
            }

            System.out.println("Success : cleared query graph");
        } else if (cmd.equals("graph") && args[0].equals("export")) {
            exportGraph(args);
        } else if (cmd.equals("graph") && args[0].equals("import")) {
            importGraph(args);
        } else if (cmd.equals("display")) {
            GraphDisplayer.displayGraph(graph);
        } else {
            System.err.println("Error: command not found");
        }
    }

    private void switchGraph() {
        this.query = !this.query;

        if (this.query) {
            System.out.println("Switching to the query knowledge grah\n you can now enter your query items");
        } else {
            System.out.println("Switching to the knowledge grah\n you can now enter your data");
        }

        KnowledgeGraph tmp = graph;
        graph = querygraph;
        querygraph = tmp;
    }

    private void addNode(String[] args) {
        if (args.length < 2) {
            System.out.println("Error: not enough arguments");
            return;
        }

        HashMap<String, Property<?>> properties = new HashMap<>();
        getNextProperties(properties, args, 2);
        boolean search = getSearch(properties);

        Node node;
        if (args[1].equalsIgnoreCase("concept")) {
            node = new ConceptNode(properties);
        } else if (args[1].equalsIgnoreCase("instance")) {
            node = new InstanceNode(properties);
        } else {
            System.err.println("Error: " + args[1] + " is not a Node type");
            return;
        }
        node.setSearch(search);

        graph.addNodes(node);
        System.out.println("Successfully created node " + node);
    }

    private void deleteNode(String[] args) {
        if (args.length < 2) {
            System.out.println("Error: not enough arguments");
            return;
        }

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
            System.err.println("Error: no suitable nodes found " + nodes.size());
            return;
        }

        System.out.println("Deleted " + nodes.size() + " node(s).");
    }

    private void findNode(String[] args) {
        if (args.length < 2) {
            System.out.println("Error: not enough arguments");
            return;
        }

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
            System.err.println("Error: no corresponding node found");
        }
    }

    private void listNode() {
        System.out.println("All Nodes\n===================");
        for (Node node : graph.getNodes()) {
            System.out.print(node.toDetailedString());
            System.out.println("===================");
        }
    }

    private void addLink(String[] args) {
        if (args.length < 4) {
            System.err.println("Syntax error. Use `link add <LinkType> [Link Mandatory Property] <IDNode1> <IDNode2> [LinkName]`");
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
                System.err.println("Error: no orientation specified");
                return;
            }
            if (args.length < 5) {
                System.err.println("Error: not enough arguments");
                return;
            }
            nodeIdIndex++;
        } else if (args[1].equalsIgnoreCase("instance")) {
            link = new InstanceLink();
        } else {
            System.err.println("Error: no valid TypeLink specified");
            return;
        }

        Node firstNode = graph.findNode(args[nodeIdIndex]);
        Node secondNode = graph.findNode(args[nodeIdIndex + 1]);

        if (args.length > nodeIdIndex + 2) {
            link.setName(args[nodeIdIndex + 2]);
        }

        if (firstNode == null || secondNode == null) {
            System.err.println("Error: no corresponding nodes found");
        } else {
            if (graph.addLink(firstNode, secondNode, link)) {
                System.out.println("Successfully added link between " + firstNode + " " + secondNode);
            }
        }
    }

    private void deleteLink(String[] args) {
        if (args.length < 4) {
            System.err.println("Syntax error. Use `link del [LinkType] [Link Mandatory Property] <IDNode1> <IDNode2> [LinkName]`");
            return;
        }

        Link link;
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
                System.err.println("Error: no orientation specified");
                return;
            }
            nodeIdIndex++;
        } else if (args[1].equalsIgnoreCase("instance")) {
            link = new InstanceLink();
        } else {
            System.err.println("Error: no valid TypeLink specified");
            return;
        }

        Node firstNode = graph.findNode(args[nodeIdIndex]);
        Node secondNode = graph.findNode(args[nodeIdIndex + 1]);

        boolean deleteAll = false;
        if (args.length >= nodeIdIndex + 3) {
            link.setName(args[nodeIdIndex + 2]);
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

        if (graph.removeLink(link, deleteAll)) {
            System.out.println("Success deleted link between " + firstNode + " " + secondNode);
        } else {
            System.out.println("Error : unsuccessful link deletion " + firstNode + " " + secondNode);
        }
    }

    private void findLink(String[] args) {
        if (args.length < 4) {
            System.err.println("Syntax error. Use `link find [LinkType] [Link Mandatory Property] <IDNode1> <IDNode2> [LinkName]`");
            return;
        }

        Link link;
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
                System.err.println("Error: no orientation specified");
                return;
            }
            nodeIdIndex++;
        } else if (args[1].equalsIgnoreCase("instance")) {
            link = new InstanceLink();
        } else {
            System.err.println("Error: no valid TypeLink specified");
            return;
        }

        Node firstNode = graph.findNode(args[nodeIdIndex]);
        Node secondNode = graph.findNode(args[nodeIdIndex + 1]);

        if (secondNode == null || firstNode == null) {
            System.out.println("Error: no nodes corresponding");
            return;
        }

        try {
            link.setFrom(firstNode);
            link.setTo(secondNode);
        } catch (IllegalLinkAssociationException e) {
            System.err.println("Error : link cannot exist illegal association");
            return;
        }

        List<Link> links = graph.findLinks(link);
        if (links.size() == 0) {
            System.err.println("Error : no corresponding link found");
        } else {
            System.out.println("Links found\n===================");
            for (Link linkPrint : links) {
                System.out.println(linkPrint.toDetailedString(null));
                System.out.println("===================");
            }
        }
    }

    private void linkList() {
        System.out.println("All Links\n===================");
        for (Link link : graph.getLinks()) {
            System.out.println(link.toDetailedString(null));
            System.out.println("===================");
        }
    }

    private void exportGraph(String[] args) {
        if (args.length == 2) {
            try {
                FileManager.writeFile(args[1], graph.toJSON());
            } catch (IOException e) {
                System.err.println("Could not export graph to file (permission denied or invalid path).");
            }
        } else {
            System.err.println("Syntax error. Use `graph export <URI>`");
        }
    }

    private void importGraph(String[] args) {
        if (args.length == 2) {
            try {
                graph = KnowledgeGraph.fromJSON(FileManager.readFile(args[1]), false);
            } catch (IOException e) {
                System.err.println("Could not import JSON file (format error).");
            }
        } else {
            System.err.println("Syntax error. Use `graph import <URI>`");
        }
    }

    private static void getNextProperties(HashMap<String, Property<?>> base, String[] args, int basePointer) {
        HashMap<String, Property<?>> toAdd = getNextProperties(args, basePointer);
        if (toAdd != null) {
            for (int i = 0; i < toAdd.size(); ++i) {
                base.put((String) toAdd.keySet().toArray()[i], (Property<?>) toAdd.values().toArray()[i]);
            }
        }
    }

    private boolean getSearch(HashMap<String, Property<?>> properties) {
        for (String key : properties.keySet()) {
            if (key.equalsIgnoreCase("search")) {
                boolean isTrue = properties.get(key).getValue().equals("true");
                properties.remove(key, properties.get(key));
                if (isTrue) {
                    return true;
                }
            }
        }
        return false;
    }

    private static HashMap<String, Property<?>> getNextProperties(String[] args, int basePointer) {
        if (args.length <= basePointer) {
            return null;
        }

        HashMap<String, Property<?>> res = new HashMap<>();

        for (int i = basePointer; i < args.length; ++i) {
            String[] parsedString = args[i].split(":");
            if (parsedString.length != 2) {
                System.out.println("Error: bad query arguments");
                return null;
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
                "node del [ID] | [Attribute1 name]:[Attribute1 value] [Attribute2 name]:[Attribute2 value]...",
                "node find [ID] | [Attribute1 name]:[Attribute1 value] [Attribute2 name]:[Attribute2 value]...",
                "node list",
                "link add <LinkType> [Link Mandatory Property] <IDNode1> <IDNode2> [LinkName]",
                "link del <LinkType> [Link Mandatory Property] <IDNode1> <IDNode2> [LinkName]",
                "link find <LinkType> [Link Mandatory Property] <IDNode1> <IDNode2> [LinkName]",
                "link list",
                "switchGraph",
                "findQuery",
                "clear",
                "graph import <graphPath>",
                "graph export <graphPath>",
                "display",
                "exit"
        };

        for (String str : helpString) {
            System.out.println(str);
        }
    }

}
