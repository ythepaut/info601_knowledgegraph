package model.node;

import model.Property;
import org.json.JSONObject;

import java.util.Map;

public class ConceptNode extends Node {
    public ConceptNode(Map<String, Property<?>> properties) {
        super(properties);
    }

    protected ConceptNode(Map<String, Property<?>> properties, String id) {
        this(properties, id, false);
    }

    protected ConceptNode(Map<String, Property<?>> properties, String id, boolean search) {
        super(properties, id, search);
    }

    @Override
    public String getName() {
        return "concept";
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject obj = super.toJSONObject();
        obj.put("type", "CONCEPT");
        return obj;
    }
}
