package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * @author toneg0d
 * @edit JavaSaBr
 */
public class ExpOut extends Exp {

    public ExpOut(final float value, final float power, @NotNull final String name) {
        super(value, power, name);
    }

    @Override
    public float apply(float a) {
        return 1 - ((float) Math.pow(value, -power * a) - min) * scale;
    }
}