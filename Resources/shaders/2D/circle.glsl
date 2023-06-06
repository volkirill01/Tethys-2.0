#type vertex
layout (location = 0) in vec3 a_Pos;
layout (location = 1) in vec2 a_UV;
layout (location = 2) in vec4 a_Color;
layout (location = 3) in float a_Thickness;
layout (location = 4) in float a_Fade;
layout (location = 5) in float a_EntityID;

uniform u_SceneData {
    mat4 u_ProjectionMatrix;
    mat4 u_ViewMatrix;
};

struct VertexOutput {
    vec2 UV;
    vec4 color;
    float thickness;
    float fade;
};
out float v_EntityID;
layout (location = 0) out VertexOutput v_Output;

void main() {
    v_Output.UV = a_UV;
    v_Output.color = a_Color;
    v_Output.thickness = a_Thickness;
    v_Output.fade = a_Fade;
    v_EntityID = a_EntityID;
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * vec4(a_Pos, 1.0);
}

#type fragment

struct VertexOutput {
    vec2 UV;
    vec4 color;
    float thickness;
    float fade;
};
in float v_EntityID;
layout (location = 0) in VertexOutput v_Input;

out vec4 out_Color;

void main() {
    float distance = 1.0 - length(v_Input.UV);
    float circleAlpha = smoothstep(0.0, v_Input.fade, distance);
    circleAlpha *= smoothstep(v_Input.thickness + v_Input.fade, v_Input.thickness, distance);

    out_Color = vec4(circleAlpha) * v_Input.color;

    if (circleAlpha <= 0.0)
        discard;
}
