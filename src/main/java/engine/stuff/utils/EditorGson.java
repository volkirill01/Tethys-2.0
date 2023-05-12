package engine.stuff.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.entity.GameObject;
import engine.entity.GameObjectDeserializer;
import engine.entity.component.Component;
import engine.entity.component.ComponentDeserializer;

public class EditorGson {

    public static Gson getGsonBuilder() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .enableComplexMapKeySerialization()
                .create();
    }
}
