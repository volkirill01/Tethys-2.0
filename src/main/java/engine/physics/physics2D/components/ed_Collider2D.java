package engine.physics.physics2D.components;

import engine.entity.component.Component;
import engine.physics.physics2D.Physics2D;

public class ed_Collider2D extends Component {

    @Override
    public void start() { Physics2D.add(this.gameObject); }

    @Override
    public void destroy() { Physics2D.destroyGameObject(this.gameObject); }
}
