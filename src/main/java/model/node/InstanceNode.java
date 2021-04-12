package model.node;

import model.Property;
import org.json.JSONObject;

import java.util.Map;

public class InstanceNode extends Node {
    public InstanceNode(Map<String, Property<?>> properties) {
        super(properties);
    }

    public InstanceNode(Map<String, Property<?>> properties, String id) {
        super(properties, id);
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject obj = super.toJSONObject();
        obj.put("type", "INSTANCE");
        return obj;
    }
}
