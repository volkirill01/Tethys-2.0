#type vertex
layout (location = 0) in vec3 a_Pos;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in vec2 a_TextureCoordinates;
layout (location = 3) in float a_TextureID;
layout (location = 4) in float a_EntityID;
layout (location = 5) in vec2 a_Tiling;

uniform mat4 u_ProjectionMatrix;
uniform mat4 u_ViewMatrix;

out vec4 v_Color;
out vec2 v_TextureCoordinates;
out float v_TextureID;
out float v_EntityID;
out vec2 v_Tiling;

void main() {
    v_Color = a_Color;
    v_TextureCoordinates = a_TextureCoordinates;
    v_TextureID = a_TextureID;
    v_EntityID = a_EntityID;
    v_Tiling = a_Tiling;
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * vec4(a_Pos, 1.0);
}

#type fragment
in vec4 v_Color;
in vec2 v_TextureCoordinates;
in float v_TextureID;
in float v_EntityID;
in vec2 v_Tiling;

// TODO CHANGE CONSTANT 32 TEXTURE SLOTS, TO USERS GPU TEXTURES SLOTS COUNT
uniform sampler2D u_TextureIDs[32];

out vec4 out_Color;

void main() {
    if (v_TextureID > 0) {
        int id = int(v_TextureID);
        out_Color = v_Color * texture(u_TextureIDs[id], v_TextureCoordinates * v_Tiling);
    } else
        out_Color = v_Color;
}
