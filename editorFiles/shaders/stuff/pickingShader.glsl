#type vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTextureCoordinates;
layout (location = 3) in float aTextureID;
layout (location = 4) in float aEntityID;

uniform mat4 uProjectionMatrix;
uniform mat4 uViewMatrix;

out vec4 fColor;
out vec2 fTextureCoordinates;
out float fTextureID;
out float fEntityID;

void main() {
    fColor = aColor;
    fTextureCoordinates = aTextureCoordinates;
    fTextureID = aTextureID;
    fEntityID = aEntityID;
    gl_Position = uProjectionMatrix * uViewMatrix * vec4(aPos, 1.0);
}

#type fragment
#version 330 core
in vec4 fColor;
in vec2 fTextureCoordinates;
in float fTextureID;
in float fEntityID;

// TODO CHANGE CONSTANT 8 TEXTURE SLOTS, TO USERS GPU TEXTURES SLOTS COUNT
uniform sampler2D uTextureIDs[8];

out vec3 out_Color;

void main() {
    vec4 textreColor = vec4(1.0, 1.0, 1.0, 1.0);
    if (fTextureID > 0) {
        int id = int(fTextureID);
        textreColor = fColor * texture(uTextureIDs[id], fTextureCoordinates);
    }

    // Apply threshold
    if (textreColor.a < 0.5)
        discard;

    out_Color = vec3(fEntityID);
}
