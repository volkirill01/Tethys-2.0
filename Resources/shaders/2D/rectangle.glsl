#type vertex
layout (location = 0) in vec3 a_Pos;
layout (location = 1) in vec2 a_UV;
layout (location = 2) in vec4 a_Color;
layout (location = 3) in vec2 a_Size;
layout (location = 4) in float a_Thickness;
layout (location = 5) in float a_Fade;
layout (location = 6) in float a_CornerRardius;
layout (location = 7) in float a_EntityID;

uniform u_SceneData {
    mat4 u_ProjectionMatrix;
    mat4 u_ViewMatrix;
};

struct VertexOutput {
    vec2 UV;
    vec2 position;
    vec4 color;
    vec2 size;
    float thickness;
    float fade;
    float cornerRadius;
};
out float v_EntityID;
layout (location = 0) out VertexOutput v_Output;

void main() {
    v_Output.UV = a_UV;
    v_Output.position = a_Pos.xy;
    v_Output.color = a_Color;
    v_Output.size = a_Size;
    v_Output.thickness = a_Thickness;
    v_Output.fade = a_Fade;
    v_EntityID = a_EntityID;
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * vec4(a_Pos, 1.0);
}

#type fragment
struct VertexOutput {
    vec2 UV;
    vec2 position;
    vec4 color;
    vec2 size;
    float thickness;
    float fade;
    float cornerRadius;
};
in float v_EntityID;
layout (location = 0) in VertexOutput v_Input;

out vec4 out_Color;

void main() {
//    float width = v_Input.size.x;
//    float height = v_Input.size.y;
//
//    vec2 loc = gl_FragCoord.xy;
//
//    float center_x = v_Input.position.x;
//    float center_y = v_Input.position.y;
//
//    // Map fragment pos to first quadrant, taking rect center as origin
//    if (loc.x < center_x) {
//        loc.x += 2 * (center_x - loc.x);
//    }
//    if (loc.y < center_y) {
//        loc.y += 2 * (center_y - loc.y);
//    }
//
//    vec2 r0 = vec2(v_Input.position.x + width - v_Input.cornerRadius, v_Input.position.y - v_Input.cornerRadius);
//
//    out_Color = vec4(v_Input.color.rgb, smoothstep(-1.0, 1.0, distance(loc, r0) - v_Input.cornerRadius));
    out_Color = vec4(1.0, 0.0, 0.0, 1.0);
}
