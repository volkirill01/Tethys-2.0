#type vertex
#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTextureCoordinates;

uniform mat4 uProjectionMatrix;
uniform mat4 uViewMatrix;

out vec4 fColor;
out vec2 fTextureCoordinates;

void main() {
    fColor = aColor;
    fTextureCoordinates = aTextureCoordinates;
    gl_Position = uProjectionMatrix * uViewMatrix * vec4(aPos, 1.0);
}

#type fragment
#version 330 core
in vec4 fColor;
in vec2 fTextureCoordinates;

uniform float uTime;
uniform sampler2D TEX_SAMPLER;

out vec4 out_Color;

void main() {
    out_Color = texture(TEX_SAMPLER, fTextureCoordinates);
}
