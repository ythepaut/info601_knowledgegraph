package model.link;

import model.node.ConceptNode;
import model.node.Node;

public class CompositionLink extends Link {

    public CompositionLink(boolean oriented) {
        super("Composition", oriented);
    }

    @Override
    public boolean isCompatible(Node from, Node to) {
        return from instanceof ConceptNode && to instanceof ConceptNode;
    }

    @Override
    public boolean isSameLink(Link other) {
        if (other instanceof CompositionLink) {
            return super.getName().equals(other.getName());
        }

        return false;
    }
}