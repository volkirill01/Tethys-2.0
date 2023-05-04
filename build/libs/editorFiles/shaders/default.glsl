#type vertex
layout (location = 0) in vec3 a_Pos;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in vec2 a_TextureCoordinates;
layout (location = 3) in float a_TextureID;

uniform mat4 u_ProjectionMatrix;
uniform mat4 u_ViewMatrix;

out vec4 v_Color;
out vec2 v_TextureCoordinates;
out float v_TextureID;

void main() {
    v_Color = a_Color;
    v_TextureCoordinates = a_TextureCoordinates;
    v_TextureID = a_TextureID;
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * vec4(a_Pos, 1.0);
}

#type fragment
in vec4 v_Color;
in vec2 v_TextureCoordinates;
in float v_TextureID;

// TODO CHANGE CONSTANT 8 TEXTURE SLOTS, TO USERS GPU TEXTURES SLOTS COUNT
uniform sampler2D u_TextureIDs[8];

out vec4 out_Color;

void main() {
//    out_Color = vec4(f_TextureCoordinates, 0, 1); // UV
    if (v_TextureID > 0) {
        int id = int(v_TextureID);
        out_Color = v_Color * texture(u_TextureIDs[id], v_TextureCoordinates);
    } else
        out_Color = v_Color;
}
