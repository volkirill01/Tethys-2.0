#type vertex
layout (location = 0) in vec3 a_Pos;
layout (location = 1) in vec2 a_TextureCoordinates;
layout (location = 2) in vec3 a_Normals;

out vec2 v_TextureCoordinates;

uniform mat4 u_TransformationMatrix;

uniform u_SceneData {
    mat4 u_ProjectionMatrix;
    mat4 u_ViewMatrix;
};

void main() {
    v_TextureCoordinates = a_TextureCoordinates;
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * (u_TransformationMatrix * vec4(a_Pos, 1.0));
}

#type fragment
out vec4 out_Color;

in vec2 v_TextureCoordinates;

//uniform sampler2D u_Albedo;
//uniform float u_Metallic;
//uniform float u_Roughness;
//
//uniform vec3 u_LightPositions[1];
//uniform vec4 u_LightColors[1];
//
//uniform vec3 u_CameraPosition;
//
//const float PI = 3.14159265359;

void main() {
//    out_Color = texture(u_Albedo, v_TextureCoordinates);
    out_Color = vec4(0.7, 0.2, 0.3, 1.0);
}
