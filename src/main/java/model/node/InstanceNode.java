package model.node;

import model.Property;
import org.json.JSONObject;

import java.util.Map;

public class InstanceNode extends Node {
    public InstanceNode(Map<String, Property<?>> properties) {
        super(properties);
    }

    protected InstanceNode(Map<String, Property<?>> properties, String id) {
        this(properties, id, false);
    }

    protected InstanceNode(Map<String, Property<?>> properties, String id, boolean search) {
        super(properties, id, search);
    }

    @Override
    public String getName() {
        return "instance";
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject obj = super.toJSONObject();
        obj.put("type", "INSTANCE");
        return obj;
    }
}
