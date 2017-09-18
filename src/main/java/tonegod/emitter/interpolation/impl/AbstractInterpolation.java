package tonegod.emitter.interpolation.impl;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.interpolation.Interpolation;

import java.io.IOException;

/**
 * The base implementation of the {@link Interpolation}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractInterpolation implements Interpolation {

    /**
     * The name of this interpolation.
     */
    @NotNull
    private final String name;

    protected AbstractInterpolation(@NotNull final String name) {
        this.name = name;
    }

    @Override
    public float apply(final float a) {
        return 0;
    }

    @Override
    public float apply(final float start, final float end, final float a) {
        return start + (end - start) * apply(a);
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public void read(final JmeImporter im) throws IOException {
    }

    @Override
    public void write(final JmeExporter ex) throws IOException {
    }
}
