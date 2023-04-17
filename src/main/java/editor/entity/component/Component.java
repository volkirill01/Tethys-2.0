package editor.entity.component;

import editor.entity.GameObject;
import editor.stuff.customVariables.Color;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {

    private static int ID_COUNTER = 0; // TODO CHANGE THIS SYSTEM TO ACTUAL APROPEREATE UUID
    private int uid = -1;
    public transient GameObject gameObject = null;

    public void imgui() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if (isTransient)
                    continue;

                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate)
                    field.setAccessible(true);

                Class type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                if (type == int.class) {
                    int val = (int) value;
                    int[] imInt = { val };
                    if (ImGui.dragInt(name + ": ", imInt))
                        field.set(this, imInt[0]);
                } else if (type == float.class) {
                    float val = (float) value;
                    float[] imFloat = { val };
                    if (ImGui.dragFloat(name + ": ", imFloat))
                        field.set(this, imFloat[0]);
                } else if (type == boolean.class) {
                    boolean val = (boolean) value;
                    if (ImGui.checkbox(name + ": ", val))
                        field.set(this, !val);
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    float[] imVec = { val.x, val.y, val.z, val.w };
                    if (ImGui.dragFloat4(name + ": ", imVec))
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    float[] imVec = { val.x, val.y, val.z };
                    if (ImGui.dragFloat3(name + ": ", imVec))
                        val.set(imVec[0], imVec[1], imVec[2]);
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f) value;
                    float[] imVec = { val.x, val.y };
                    if (ImGui.dragFloat2(name + ": ", imVec))
                        val.set(imVec[0], imVec[1]);
                } else if (type == Color.class) {
                    Color val = (Color) value;
                    float[] imVec = { val.r / 255.0f, val.g / 255.0f, val.b / 255.0f, val.a / 255.0f };
                    if (ImGui.colorEdit4(name + ": ", imVec))
                        val.set(imVec[0] * 255.0f, imVec[1] * 255.0f, imVec[2] * 255.0f, imVec[3] * 255.0f);
                } else {
                    ImGui.text(name + ", " + type.getCanonicalName() + " - Custom inspector not added yet.");
                }

                if (isPrivate)
                    field.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void start() { }

    public void update() { }

    public void generateID() {
        if (this.uid == -1)
            this.uid = ID_COUNTER++;
    }

    public int getUid() { return this.uid; }

    public static void init(int maxID) { ID_COUNTER = maxID; }
}
