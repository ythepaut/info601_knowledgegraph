package model.node;

import model.Property;

import java.util.HashMap;

public class InstanceNode extends Node {
    public InstanceNode(HashMap<String, Property<?>> properties) {
        super(properties);
    }
}
