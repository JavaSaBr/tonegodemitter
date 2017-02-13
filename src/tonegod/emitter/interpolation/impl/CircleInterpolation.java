package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * @author toneg0d, JavaSaBr
 */
public class CircleInterpolation extends AbstractInterpolation {

    public CircleInterpolation(@NotNull final String name) {
        super(name);
    }

    @Override
    public float apply(float a) {
        if (a <= 0.5f) {
            a *= 2;
            return (1 - (float) Math.sqrt(1 - a * a)) / 2;
        }
        a--;
        a *= 2;
        return ((float) Math.sqrt(1 - a * a) + 1) / 2;
    }
}
