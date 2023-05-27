package engine.entity.component;

import com.google.gson.*;
import engine.logging.DebugLog;
import engine.profiling.Profiler;

import java.lang.reflect.Type;

public class ComponentDeserializer implements JsonSerializer<Component>, JsonDeserializer<Component> {

    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        DebugLog.logInfo("DeserializeComponent: ", type);
        try {
            Profiler.startTimer(String.format("Deserialize Component - '%s'", type));
            Component c = context.deserialize(element, Class.forName(type));
            Profiler.stopTimer(String.format("Deserialize Component - '%s'", type));
            return c;
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(String.format("Unknown element type - '%s'", type), e);
        }
    }

    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        DebugLog.logInfo("SerializeComponent: ", src.getClass().getCanonicalName());

        Profiler.startTimer(String.format("Serialize Component - '%s'", src.getClass().getCanonicalName()));
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        result.add("properties", context.serialize(src, src.getClass()));

        Profiler.stopTimer(String.format("Serialize Component - '%s'", src.getClass().getCanonicalName()));
        return result;
    }
}
