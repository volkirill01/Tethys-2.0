package editor.stuff.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import editor.entity.GameObject;
import editor.entity.GameObjectDeserializer;
import editor.entity.component.Component;
import editor.entity.component.ComponentDeserializer;

public class EditorGson {

    public static Gson getGsonBuilder() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .create();
    }
}
