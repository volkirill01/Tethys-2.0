package editor.stuff;

import org.joml.*;

import java.lang.Math;
import java.util.Arrays;

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

    public static void decomposeTransformationMatrix(float[] transformationMatrixArray, Vector3f translation, Vector3f rotation, Vector3f scale) {
        Matrix4f matrix = new Matrix4f().identity();
        matrix.set(transformationMatrixArray);
        decomposeTransformationMatrix(matrix, translation, rotation, scale);
    }

    public static boolean decomposeTransformationMatrix(Matrix4f transformationMatrix, Vector3f translation, Vector3f rotation, Vector3f scale) {
        Matrix4f localMatrix = new Matrix4f(transformationMatrix);

        // Normalize the matrix
        if (localMatrix.get(3, 3) == Math.ulp(0))
            return false;

        // First, isolate perspective.  This is the messiest.
        if (localMatrix.get(0, 3) != Math.ulp(0) || localMatrix.get(1, 3) != Math.ulp(0) || localMatrix.get(2, 3) != Math.ulp(0)) {
            // Clear the perspective partition
            localMatrix.set(0, 3, 0);
            localMatrix.set(1, 3, 0);
            localMatrix.set(2, 3, 0);
            localMatrix.set(3, 3, 1);
        }

        // Next take care of translation (easy).
        translation.set(localMatrix.get(3, 0), localMatrix.get(3, 1), localMatrix.get(3, 2));
        localMatrix.set(3, 0, 0);
        localMatrix.set(3, 1, 0);
        localMatrix.set(3, 2, 0);

        Vector3f[] row = new Vector3f[3];
        Vector3f pdum3;

        for (int i = 0; i < 3; i++)
            row[i] = new Vector3f(0.0f);

        // Now get scale and shear.
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                switch (j) {
                    case 0: row[i].x = localMatrix.get(i, j);
                    case 1: row[i].y = localMatrix.get(i, j);
                    case 2: row[i].z = localMatrix.get(i, j);
                }
            }

        // Compute X scale factor and normalize first row.
        scale.x = row[0].length();
        row[0] = row[0].normalize();
        scale.y = row[1].length();
        row[1] = row[1].normalize();
        scale.z = row[2].length();
        row[2] = row[2].normalize();

        // At this point, the matrix (in rows[]) is orthonormal.
        // Check for a coordinate system is flip. If the determinate.
        // Is -1, then negate the matrix and the scaling factors.

        pdum3 = row[1].cross(row[2]);
        if (row[0].dot(pdum3) < 0) {
            for (int i = 0; i < 3; i++) {
                switch (i) {
                    case 0: scale.x *= 1;
                    case 1: scale.y *= 1;
                    case 2: scale.z *= 1;
                }
                row[i] = row[i].mul(-1);
            }
        }

//        rotation.y = (float) Math.asin(-row[0].z); // TODO FIX ROTATION CALCULATION
//        if (Math.cos(rotation.y) != 0) {
//            rotation.x = (float) Math.atan2(row[1].z, row[2].z);
//            rotation.z = (float) Math.atan2(row[0].y, row[0].x);
//        } else {
//            rotation.x = (float) Math.atan2(-row[2].x, row[1].y);
//            rotation.z = 0.0f;
//        }

        return true;
    }

    public static void rotateX(Vector3f point, float angleDeg) {
        float newAngle = (float) (Math.atan2(point.z, point.y) + Math.toRadians(angleDeg + 90.0f));
        float distance = (float) Math.pow(Math.pow(point.y, 2.0f) + Math.pow(point.z, 2.0f), 0.5f);

        point.z = (float) (distance * Math.cos(newAngle));
        point.y = (float) (distance * Math.sin(newAngle));

//        float sinTheta = (float) Math.sin(Math.toRadians(-angleDeg + 90.0f));
//        float cosTheta = (float) Math.cos(Math.toRadians(-angleDeg + 90.0f));
//        float y = point.y;
//        float z = point.z;
//        point.y = z * cosTheta - y * sinTheta;
//        point.z = y * cosTheta + z * sinTheta;

//        point.y = (float) (point.y * Math.cos(Math.toRadians(angleDeg)) - point.z * Math.sin(Math.toRadians(angleDeg)));
//        point.z = (float) (point.y * Math.sin(Math.toRadians(angleDeg)) + point.z * Math.cos(Math.toRadians(angleDeg)));

//        float newAngle = (float) (Math.atan2(point.y, point.z) + Math.toRadians(-angleDeg));
//        float distance = (float) Math.pow(Math.pow(point.y, 2.0f) + Math.pow(point.z, 2.0f), 0.5f);
//        point.y = (float) (distance * Math.cos(newAngle));
//        point.z = (float) (distance * Math.sin(newAngle));
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

    public static boolean compare(float x, float y, float epsilon) {
        return Math.abs(x - y) <= epsilon * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon);
    }

    public static boolean compare(float x, float y) {
        return Math.abs(x - y) <= Float.MIN_VALUE * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2) {
        return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y);
    }

    public static float clamp(float value, float min, float max) { return Math.max(min, Math.min(max, value)); }

    public static int clamp(int value, int min, int max) { return Math.max(min, Math.min(max, value)); }

    public static float normalize(float value, float min, float max) { return ((value - min) / (max - min)); }
}
