package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * @author toneg0d, JavaSaBr
 */
public class ExpIn extends Exp {

    public ExpIn(final float value, final float power, @NotNull final String name) {
        super(value, power, name);
    }

    @Override
    public float apply(float a) {
        return ((float) Math.pow(value, power * (a - 1)) - min) * scale;
    }
}
