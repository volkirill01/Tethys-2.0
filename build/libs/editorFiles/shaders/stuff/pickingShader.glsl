#type vertex
layout (location = 0) in vec3 a_Pos;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in vec2 a_TextureCoordinates;
layout (location = 3) in float a_TextureID;
layout (location = 4) in float a_EntityID;

uniform mat4 u_TransformationMatrix;
uniform mat4 u_ProjectionMatrix;
uniform mat4 u_ViewMatrix;
uniform float u_EntityID;

out vec4 v_Color;
out vec2 v_TextureCoordinates;
out float v_TextureID;
out float v_EntityID;

void main() {
    v_Color = a_Color;
    v_TextureCoordinates = a_TextureCoordinates;
    v_TextureID = a_TextureID;
    if (u_EntityID != -1)
        v_EntityID = u_EntityID;
    else
        v_EntityID = a_EntityID;
    gl_Position = u_ProjectionMatrix * u_ViewMatrix * (u_TransformationMatrix * vec4(a_Pos, 1.0));
}

#type fragment
in vec4 v_Color;
in vec2 v_TextureCoordinates;
in float v_TextureID;
in float v_EntityID;

// TODO CHANGE CONSTANT 8 TEXTURE SLOTS, TO USERS GPU TEXTURES SLOTS COUNT
uniform sampler2D u_TextureIDs[8];

out vec3 out_Color;

void main() {
    vec4 textreColor = vec4(1.0, 1.0, 1.0, 1.0);
    if (v_TextureID > 0) {
        int id = int(v_TextureID);
        textreColor = v_Color * texture(u_TextureIDs[id], v_TextureCoordinates);
    }

    // Apply threshold
    if (textreColor.a < 0.5)
        discard;

    out_Color = vec3(v_EntityID);
}
