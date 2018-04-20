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

    protected AbstractInterpolation(@NotNull String name) {
        this.name = name;
    }

    @Override
    public float apply(float a) {
        return 0;
    }

    @Override
    public float apply(float start, float end, float a) {
        return start + (end - start) * apply(a);
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
    }
}
