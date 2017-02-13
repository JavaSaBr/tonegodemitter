package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * @author toneg0d, JavaSaBr
 */
public class Swing extends AbstractInterpolation {

    protected final float scale;

    public Swing(final float scale, @NotNull final String name) {
        super(name);
        this.scale = scale * 2;
    }

    @Override
    public float apply(float a) {
        if (a <= 0.5f) {
            a *= 2;
            return a * a * ((scale + 1) * a - scale) / 2;
        }
        a--;
        a *= 2;
        return a * a * ((scale + 1) * a + scale) / 2 + 1;
    }
}
