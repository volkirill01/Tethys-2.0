package engine.entity;

import com.google.gson.*;
import engine.entity.component.Component;
import engine.entity.component.Transform;
import engine.logging.DebugLog;
import engine.profiling.Profiler;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {

    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        JsonObject tagComponent = jsonObject.getAsJsonObject("tagComponent");
        String name = tagComponent.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");

        DebugLog.logInfo("DeserializeGameObject: ", name);
        Profiler.startTimer(String.format("Deserialize GameObject - '%s'", name));
        GameObject go = new GameObject(name);
        for (JsonElement e : components) {
            Component c = context.deserialize(e, Component.class);
            go.addComponent(c);
        }
        go.transform = go.getComponent(Transform.class);

        Profiler.stopTimer(String.format("Deserialize GameObject - '%s'", name));

        return go;
    }
}
