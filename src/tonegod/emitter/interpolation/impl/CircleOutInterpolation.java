package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * @author toneg0d
 * @edit JavaSaBr
 */
public class CircleOutInterpolation extends AbstractInterpolation {

    public CircleOutInterpolation(@NotNull final String name) {
        super(name);
    }

    @Override
    public float apply(float a) {
        a--;
        return (float) Math.sqrt(1 - a * a);
    }
}
