package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * @author toneg0d
 * @edit JavaSaBr
 */
public class SwingIn extends Swing {

    public SwingIn(float scale, @NotNull final String name) {
        super(scale, name);
    }

    @Override
    public float apply(float a) {
        return a * a * ((scale + 1) * a - scale);
    }
}
