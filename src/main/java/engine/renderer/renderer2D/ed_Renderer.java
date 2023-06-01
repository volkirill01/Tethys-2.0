package engine.renderer.renderer2D;

import engine.entity.component.Component;

public class ed_Renderer extends Component {

    private boolean isDirty = true;

    public boolean isDirty() { return this.isDirty; }

    public void setDirty(boolean dirty) { isDirty = dirty; }
}
