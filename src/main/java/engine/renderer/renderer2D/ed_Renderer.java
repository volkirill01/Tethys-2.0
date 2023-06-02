package engine.renderer.renderer2D;

import engine.entity.component.Component;
import engine.renderer.EntityRenderer;

public class ed_Renderer extends Component {

    @Override
    public void start() { EntityRenderer.add(this.gameObject); }
}
