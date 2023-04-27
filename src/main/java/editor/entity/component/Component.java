package editor.entity.component;

import editor.editor.gui.EditorGUI;
import editor.entity.GameObject;
import editor.stuff.Settings;
import editor.stuff.customVariables.Color;
import imgui.ImGui;
import imgui.type.ImInt;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {

    private static int ID_COUNTER = 0; // TODO CHANGE THIS SYSTEM TO ACTUAL APPROPRIATE UUID
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
                if (Settings.variableNamesStartsUpperCase)
                    name = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);

                if (type == int.class) {
                    int val = (int) value;
                    field.set(this, EditorGUI.field_Int(name, val));
                } else if (type == float.class) {
                    float val = (float) value;
                    field.set(this, EditorGUI.field_Float(name, val));
                } else if (type == boolean.class) {
                    boolean val = (boolean) value;
                    field.set(this, EditorGUI.field_Boolean(name, val));
                } else if (type == String.class) {
                    String val = (String) value;
                    field.set(this, EditorGUI.field_String(name, val));
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f) value;
                    EditorGUI.field_Vector2f(name, val);
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    EditorGUI.field_Vector3f(name, val);
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    EditorGUI.field_Vector4f(name, val);
                } else if (type == Color.class) {
                    Color val = (Color) value;
                    EditorGUI.field_Color(name, val);
                } else if (type.isEnum()) {
                    String[] enumValues = getEnumValues(type);
                    String enumType = ((Enum) value).name();
                    ImInt index = new ImInt(indexOf(enumType, enumValues));
                    if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)) {
                        field.set(this, type.getEnumConstants()[index.get()]);
                    }
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

    private <T extends Enum<T>> String[] getEnumValues(Class<T> enumType) {
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i = 0;
        for (T enumIntegerValue : enumType.getEnumConstants()) {
            enumValues[i] = enumIntegerValue.name();
            i++;
        }
        return enumValues;
    }

    private int indexOf(String str, String[] array) {
        for (int i = 0; i < array.length; i++)
            if (str.equals(array[i]))
                return i;
        return -1;
    }

    public void start() { }

    public void editorUpdate() { }

    public void update() { }

    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) { }

    public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) { }

    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) { } // One direction Example: (Pass throw bottom but solid on top)

    public void postSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) { } // One direction Example: (Pass throw top but solid on bottom)

    public void generateID() {
        if (this.uid == -1)
            this.uid = ID_COUNTER++;
    }

    public void destroy() { }

    public int getUid() { return this.uid; }

    public static void init(int maxID) { ID_COUNTER = maxID; }
}
