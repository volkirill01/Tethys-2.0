package editor.stuff;

import org.joml.*;

import java.lang.Math;

public class Maths {

    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
        Matrix4f transformationMatrix = new Matrix4f().identity();

        transformationMatrix.translate(translation.x, translation.y, translation.z);
        transformationMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0));
        transformationMatrix.rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        transformationMatrix.rotate((float)Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
        transformationMatrix.scale(scale.x, scale.y, scale.z);

        return transformationMatrix;
    }

    public static void rotateX(Vector3f point, float angleDeg) {
        float newAngle = (float) (Math.atan2(point.z, point.y) + Math.toRadians(angleDeg + 90.0f));
        float distance = (float) Math.pow(Math.pow(point.y, 2.0f) + Math.pow(point.z, 2.0f), 0.5f);

        point.z = (float) (distance * Math.cos(newAngle));
        point.y = (float) (distance * Math.sin(newAngle));
    }

    public static void rotateY(Vector3f point, float angleDeg) {
        float newAngle = (float) (Math.atan2(point.z, point.x) + Math.toRadians(angleDeg + 90.0f));
        float distance = (float) Math.pow(Math.pow(point.x, 2.0f) + Math.pow(point.z, 2.0f), 0.5f);

        point.z = (float) (distance * Math.cos(newAngle));
        point.x = (float) (distance * Math.sin(newAngle));
    }

    public static void rotateZ(Vector3f point, float angleDeg) {
        float newAngle = (float) (Math.atan2(point.x, point.y) - Math.toRadians(angleDeg + 90.0f));
        float distance = (float) Math.pow(Math.pow(point.x, 2.0f) + Math.pow(point.y, 2.0f), 0.5f);

        point.y = (float) (distance * Math.cos(newAngle));
        point.x = (float) (distance * Math.sin(newAngle));
    }

    public static void rotate3D(Vector3f point, Vector3f rotation) {
        Vector4f tmp = new Vector4f(point.x, point.y, point.z, 1.0f);
        Matrix4f rotationMatrix = Maths.createTransformationMatrix(new Vector3f(0.0f), rotation, new Vector3f(1.0f));

        tmp.mul(rotationMatrix);

        point.set(tmp.x, tmp.y, tmp.z);
    }

    public static void rotate3DVertices(Vector3f[] points, Vector3f origin, Vector3f rotation) {
        for (Vector3f point : points) {
            point.sub(origin);
            rotate3D(point, rotation);
            point.add(origin);
        }
    }

    public static void rotate(Vector2f vec, float angleDeg, Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float cos = (float)Math.cos(Math.toRadians(angleDeg));
        float sin = (float)Math.sin(Math.toRadians(angleDeg));

        float xPrime = (x * cos) - (y * sin);
        float yPrime = (x * sin) + (y * cos);

        xPrime += origin.x;
        yPrime += origin.y;

        vec.x = xPrime;
        vec.y = yPrime;
    }

    public static float clamp(float value, float min, float max) { return Math.max(min, Math.min(max, value)); }

    public static int clamp(int value, int min, int max) { return Math.max(min, Math.min(max, value)); }

    public static float normalize(float value, float min, float max) { return ((value - min) / (max - min)); }
}
