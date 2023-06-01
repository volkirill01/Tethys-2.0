#type vertex
layout (location = 0) in vec3 a_Pos;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in vec2 a_TextureCoordinates;
layout (location = 3) in float a_TextureID;
layout (location = 4) in float a_EntityID;
layout (location = 5) in vec2 a_Tiling;

uniform u_SceneData {
    mat4 u_ProjectionMatrix;
    mat4 u_ViewMatrix;
};

struct VertexOutput {
    vec4 color;
    vec2 textureCoordinates;
    float textureID;
    vec2 tiling;
};
out float v_EntityID;
layout (location = 0) out VertexOutput v_Output;

void main() {
    v_Output.color = a_Color;
    v_Output.textureCoordinates = a_TextureCoordinates;
    v_Output.textureID = a_TextureID;
    v_Output.tiling = a_Tiling;
    v_EntityID = a_EntityID;
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * vec4(a_Pos, 1.0);
}

#type fragment

struct VertexOutput {
    vec4 color;
    vec2 textureCoordinates;
    float textureID;
    vec2 tiling;
};
in float v_EntityID;
layout (location = 0) in VertexOutput v_Input;

// TODO CHANGE CONSTANT 32 TEXTURE SLOTS, TO USERS GPU TEXTURES SLOTS COUNT
uniform sampler2D u_TextureIDs[32];

out vec4 out_Color;

void main() {
    if (v_Input.textureID > 0) {
        int id = int(v_Input.textureID);
        out_Color = v_Input.color * texture(u_TextureIDs[id], v_Input.textureCoordinates * v_Input.tiling);
    } else
        out_Color = v_Input.color;
}
