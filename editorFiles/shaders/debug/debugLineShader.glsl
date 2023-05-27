#type vertex
layout (location = 0) in vec3 a_Pos;
layout (location = 1) in vec4 a_Color;

//uniform mat4 u_ProjectionMatrix;
//uniform mat4 u_ViewMatrix;

uniform u_SceneData {
    mat4 u_ProjectionMatrix;
    mat4 u_ViewMatrix;
};

out vec4 v_Color;

void main() {
    v_Color = a_Color;
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * vec4(a_Pos, 1.0);
}

#type fragment
in vec4 v_Color;

out vec4 out_Color;

void main() {
    out_Color = v_Color;
}
