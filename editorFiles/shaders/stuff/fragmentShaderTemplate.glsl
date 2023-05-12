#sourceFirst

uniform int u_EntityID;
layout (location = 1) out vec4 out_PickingTexture;

void main() {
    if (u_EntityID == -99)
        out_PickingTexture = vec4(-1.0);
    else if (u_EntityID != -1)
        out_PickingTexture = vec4(u_EntityID, u_EntityID, u_EntityID, 1.0);
    #else block
    else
        out_PickingTexture = vec4(v_EntityID, v_EntityID, v_EntityID, 1.0);
    #end else block
#sourceSecond