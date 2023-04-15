#type vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTextureCoordinates;
layout (location = 3) in float aTextureID;

uniform mat4 uProjectionMatrix;
uniform mat4 uViewMatrix;

out vec4 fColor;
out vec2 fTextureCoordinates;
out float fTextureID;

void main() {
    fColor = aColor;
    fTextureCoordinates = aTextureCoordinates;
    fTextureID = aTextureID;
    gl_Position = uProjectionMatrix * uViewMatrix * vec4(aPos, 1.0);
}

#type fragment
#version 330 core
in vec4 fColor;
in vec2 fTextureCoordinates;
in float fTextureID;

uniform sampler2D uTextureIDs[8];

out vec4 out_Color;

void main() {
//    out_Color = vec4(fTextureCoordinates, 0, 1); // UV
    if (fTextureID > 0) {
        int id = int(fTextureID);
        out_Color = fColor * texture(uTextureIDs[id], fTextureCoordinates);
    } else
        out_Color = fColor;
}
