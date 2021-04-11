package model.link;

import model.node.ConceptNode;
import model.node.InstanceNode;
import model.node.Node;

public class InstanceLink extends Link {

    public InstanceLink() {
        super("instance", true);
    }

    @Override
    public boolean isCompatible(Node from, Node to) {
        return (from instanceof ConceptNode && to instanceof InstanceNode) ||
               (from instanceof InstanceNode && to instanceof ConceptNode);
    }

    @Override
    public boolean isSameLink(Link other) {
        return other instanceof InstanceLink;
    }
}
