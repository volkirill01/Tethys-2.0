package engine.stuff;

import org.joml.*;

import java.lang.Math;

public class Maths {

    public static float lerp(float a, float b, float interpolation) { return a + interpolation * (b - a); }

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
        transformationMatrix.rotateXYZ(rotation.x, rotation.y, rotation.z);
        transformationMatrix.scale(scale.x, scale.y, scale.z);

        return transformationMatrix;
    }

//    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
//        Matrix4f mat = new Matrix4f().identity();
//
//        Matrix4f[] rot = {
//                new Matrix4f().identity(),
//                new Matrix4f().identity(),
//                new Matrix4f().identity()
//        };
//        for (int i = 0; i < 3; i++) {
//            switch (i) {
//                case 0 -> rot[i].rotate(rotation.x * DEG2RAD, new Vector3f(1.0f, 0.0f, 0.0f));
//                case 1 -> rot[i].rotate(rotation.y * DEG2RAD, new Vector3f(0.0f, 1.0f, 0.0f));
//                case 2 -> rot[i].rotate(rotation.z * DEG2RAD, new Vector3f(0.0f, 0.0f, 1.0f));
//            }
//        }
//
//        mat.mul(rot[0]).mul(rot[1]).mul(rot[2]);
//
//        float[] validScale = new float[3];
//        for (int i = 0; i < 3; i++) {
//            switch (i) {
//                case 0 -> {
//                    if (Math.abs(scale.x) < 0.0001f)
//                        validScale[i] = 0.0001f;
//                    else
//                        validScale[i] = scale.x;
//                }
//                case 1 -> {
//                    if (Math.abs(scale.y) < 0.0001f)
//                        validScale[i] = 0.0001f;
//                    else
//                        validScale[i] = scale.y;
//                }
//                case 2 -> {
//                    if (Math.abs(scale.z) < 0.0001f)
//                        validScale[i] = 0.0001f;
//                    else
//                        validScale[i] = scale.z;
//                }
//            }
//
//        }
//        mat.scale(validScale[0], validScale[1], validScale[2]);
////        mat.v.right *= validScale[0];
////        mat.v.up *= validScale[1];
////        mat.v.dir *= validScale[2];
////        mat.v.position.Set(translation[0], translation[1], translation[2], 1.f);
//        mat.translate(translation.x, translation.y, translation.z);
//
//        return mat;
//    }

//    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
//        float[] matrixArray = new float[4 * 4];
//        float[] translationArray = { translation.x, translation.y, translation.z };
//        float[] rotationArray = { rotation.x, rotation.y, rotation.z };
//        float[] scaleArray = { scale.x, scale.y, scale.z };
//        ImGuizmo.recomposeMatrixFromComponents(matrixArray, translationArray, rotationArray, scaleArray);
//
//        return new Matrix4f().set(matrixArray);
//    }

//    public static boolean decomposeTransformationMatrix(Matrix4f transformationMatrix, Vector3f outTranslation, Vector3f outRotation, Vector3f outScale) {
//        Matrix4f matrix = new Matrix4f(transformationMatrix);
//
//        if (epsilonEqual(matrix.get(3, 3), 0.0f))
//            return false;
//
//        // First, isolate perspective.  This is the messiest.
//        if (!epsilonEqual(matrix.m03(), 0.0f) || !epsilonEqual(matrix.m13(), 0.0f) || !epsilonEqual(matrix.m23(), 0.0f)) {
//            // Clear the perspective partition
//            matrix.m03(0.0f);
//            matrix.m13(0.0f);
//            matrix.m23(0.0f);
//            matrix.m33(1.0f);
//        }
//
//        // Next take care of translation (easy).
//        outTranslation.set(matrix.m03(), matrix.m13(), matrix.m23());
//        matrix.m03(0.0f);
//        matrix.m13(0.0f);
//        matrix.m23(0.0f);
//
//        Vector3f[] row = new Vector3f[]{
//                new Vector3f(),
//                new Vector3f(),
//                new Vector3f()
//        };
//
//        // Now get scale and shear.
//        for (int i = 0; i < 3; i++)
//            for (int j = 0; j < 3; j++) {
//                if (j == 0)
//                    row[i].x = matrix.get(i, j);
//                else if (j == 1)
//                    row[i].y = matrix.get(i, j);
//                else
//                    row[i].z = matrix.get(i, j);
//            }
//
//        // Compute X scale factor and normalize first row.
//        outScale.x = row[0].length();
//        row[0] = row[0].mul(1.0f);
//
//        outScale.y = row[1].length();
//        row[1] = row[1].mul(1.0f);
//
//        outScale.z = row[2].length();
//        row[2] = row[2].mul(1.0f);
//
//        outRotation.y = (float) org.joml.Math.toDegrees(org.joml.Math.asin(-row[0].z));
//        if (org.joml.Math.cos(outRotation.y) != 0) {
//            outRotation.x = org.joml.Math.atan2(row[1].z, row[2].z);
//            outRotation.z = org.joml.Math.atan2(row[0].y, row[0].x);
//        } else {
//            outRotation.x = org.joml.Math.atan2(-row[2].x, row[1].y);
//            outRotation.z = 0.0f;
//        }
//
//        return true;
//    }
//    private static boolean epsilonEqual(float a, float b) { double result = a / b; return (Math.abs(result - 1.0f) < 0.001f); }

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
