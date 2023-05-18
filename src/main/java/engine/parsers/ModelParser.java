package engine.parsers;

import de.javagl.obj.*;
import engine.editor.console.Console;
import engine.editor.console.LogType;
import engine.profiling.Profiler;
import engine.renderer.buffers.IndexBuffer;
import engine.renderer.buffers.VertexArray;
import engine.renderer.buffers.VertexBuffer;
import engine.renderer.buffers.bufferLayout.BufferElement;
import engine.renderer.buffers.bufferLayout.BufferLayout;
import engine.renderer.buffers.bufferLayout.ShaderDataType;
import engine.renderer.renderer3D.mesh.Mesh;
import engine.renderer.renderer3D.mesh.RawModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ModelParser {

    public static Mesh loadFromFile(String filepath) {
        if (filepath.endsWith(".obj"))
            return loadOBJ(filepath);
        Console.log(String.format("Unknown model extension - '%s'", filepath), LogType.Error);
        return null;
    }

    private static Mesh loadOBJ(String filepath) {
        Profiler.startTimer("Parse OBJ File");
        List<RawModel> models = new ArrayList<>();

        InputStream inputStream;
        try {
            inputStream = new FileInputStream(filepath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Obj obj;
        try {
            obj = ObjUtils.convertToRenderable(ObjReader.read(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Obj> materialGroups = ObjSplitting.splitByMaterialGroups(obj);

        if (materialGroups.size() > 1) {
            for (String matObjKey : materialGroups.keySet()) {
                Obj matObj = materialGroups.get(matObjKey);
                float[] verticesB = ObjData.getVerticesArray(matObj);
                float[] texCoordsB = ObjData.getTexCoordsArray(matObj, 2);
                float[] normalsB = ObjData.getNormalsArray(matObj);
                int[] indicesB = ObjData.getFaceVertexIndicesArray(matObj);

                models.add(loadToVAO(verticesB, texCoordsB, normalsB, indicesB, matObjKey));
            }
        } else {
            float[] verticesB = ObjData.getVerticesArray(obj);
            float[] texCoordsB = ObjData.getTexCoordsArray(obj, 2);
            float[] normalsB = ObjData.getNormalsArray(obj);
            int[] indicesB = ObjData.getFaceVertexIndicesArray(obj);

            models.add(loadToVAO(verticesB, texCoordsB, normalsB, indicesB, "Material"));
        }

        Profiler.stopTimer("Parse OBJ File");
        return new Mesh(models, filepath);
    }

    public static RawModel loadToVAO(float[] positions, float[] textureCoordinates, float[] normals, int[] indices, String materialGroup) {
        VertexArray vao = new VertexArray();

        BufferLayout layout = new BufferLayout(Arrays.asList(
                new BufferElement(ShaderDataType.Float3, "a_Position"),
                new BufferElement(ShaderDataType.Float2, "a_TextureCoordinates"),
                new BufferElement(ShaderDataType.Float3, "a_Normals")
        ));

        float[] vertices = new float[8 * (positions.length / 3)];
        int positionOffset = 0;
        int textureCoordinatesOffset = 0;
        int normalsOffset = 0;
        for (int i = 0; i < vertices.length; i += 8) {
            vertices[i] = positions[positionOffset];
            vertices[i + 1] = positions[positionOffset + 1];
            vertices[i + 2] = positions[positionOffset + 2];
            positionOffset += 3;

            vertices[i + 3] = textureCoordinates[textureCoordinatesOffset];
            vertices[i + 4] = textureCoordinates[textureCoordinatesOffset + 1];
            textureCoordinatesOffset += 2;

            vertices[i + 5] = normals[normalsOffset];
            vertices[i + 6] = normals[normalsOffset + 1];
            vertices[i + 7] = normals[normalsOffset + 2];
            normalsOffset += 3;
        }
        VertexBuffer vbo = new VertexBuffer(vertices);
        vbo.setLayout(layout);
        vao.setVertexBuffer(vbo);

        IndexBuffer elements = new IndexBuffer(indices, indices.length);
        vao.setIndexBuffer(elements);

        vao.unbind();
        return new RawModel(vao, materialGroup);
    }
}
