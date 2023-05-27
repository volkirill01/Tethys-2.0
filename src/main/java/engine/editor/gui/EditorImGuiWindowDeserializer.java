package engine.editor.gui;

import com.google.gson.*;
import engine.profiling.Profiler;

import java.lang.reflect.Type;

public class EditorImGuiWindowDeserializer implements JsonSerializer<EditorGuiWindow>, JsonDeserializer<EditorGuiWindow> {

    @Override
    public EditorGuiWindow deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            Profiler.startTimer(String.format("Deserialize EditorWindow - '%s'", type));
            EditorGuiWindow w = context.deserialize(element, Class.forName(type));
            Profiler.stopTimer(String.format("Deserialize EditorWindow - '%s'", type));
            return w;
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(String.format("Unknown element type - '%s'", type), e);
        }
    }

    @Override
    public JsonElement serialize(EditorGuiWindow src, Type typeOfSrc, JsonSerializationContext context) {
        Profiler.startTimer(String.format("Serialize EditorWindow - '%s'", src.getClass().getCanonicalName()));
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        result.add("properties", context.serialize(src, src.getClass()));

        Profiler.stopTimer(String.format("Serialize EditorWindow - '%s'", src.getClass().getCanonicalName()));
        return result;
    }
}
