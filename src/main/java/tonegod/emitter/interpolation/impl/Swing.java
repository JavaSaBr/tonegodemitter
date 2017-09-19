package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * The type Swing.
 *
 * @author toneg0d, JavaSaBr
 */
public class Swing extends AbstractInterpolation {

    /**
     * The Scale.
     */
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
