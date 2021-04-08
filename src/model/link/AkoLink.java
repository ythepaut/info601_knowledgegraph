package model.link;

import model.node.ConceptNode;
import model.node.Node;

public class AkoLink extends Link {

    @Override
    public boolean isCompatible(Node from, Node to) {
        return from instanceof ConceptNode && to instanceof ConceptNode;
    }

    @Override
    public boolean equals(Object other) {
        // FIXME: It may be a problem later ; how can we differentiate two Ako links then ?
        return other instanceof AkoLink;
    }
}
