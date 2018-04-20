package tonegod.emitter.interpolation.impl;

import org.jetbrains.annotations.NotNull;

/**
 * The type Exp out.
 *
 * @author toneg0d, JavaSaBr
 */
public class ExpOut extends Exp {

    public ExpOut(float value, float power, @NotNull String name) {
        super(value, power, name);
    }

    @Override
    public float apply(float a) {
        return 1 - ((float) Math.pow(value, -power * a) - min) * scale;
    }
}
