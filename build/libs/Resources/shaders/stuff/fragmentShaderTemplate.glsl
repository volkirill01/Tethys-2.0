#sourceFirst

uniform int u_EntityID;
layout (location = 1) out int out_PickingTexture;

void main() {
    if (u_EntityID == -99)
        out_PickingTexture = -1;
    else if (u_EntityID != -1)
        out_PickingTexture = u_EntityID;
    #else block
    else
        out_PickingTexture = int(v_EntityID);
    #end else block
#sourceSecond