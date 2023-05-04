#type vertex
layout (location = 0) in vec3 a_Pos;
layout (location = 1) in vec2 a_TextureCoordinates;
layout (location = 1) in vec3 a_Normals;

uniform mat4 u_TransformationMatrix;
uniform mat4 u_ProjectionMatrix;
uniform mat4 u_ViewMatrix;

out vec2 v_TextureCoordinates;
out vec3 v_Normals;

void main() {
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * (u_TransformationMatrix * vec4(a_Pos, 1.0));

    v_TextureCoordinates = a_TextureCoordinates;
    v_Normals = a_Normals;
}

#type fragment
in vec2 v_TextureCoordinates;
in vec3 v_Normals;

out vec4 out_Color;

void main() {
    out_Color = vec4(1.0);
}
