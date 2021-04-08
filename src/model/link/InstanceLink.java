package model.link;

import model.node.ConceptNode;
import model.node.InstanceNode;
import model.node.Node;

public class InstanceLink extends Link {

    @Override
    public boolean isCompatible(Node from, Node to) {
        return (from instanceof ConceptNode && to instanceof InstanceNode) ||
                (from instanceof InstanceNode && to instanceof ConceptNode);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof InstanceLink;
    }
}
