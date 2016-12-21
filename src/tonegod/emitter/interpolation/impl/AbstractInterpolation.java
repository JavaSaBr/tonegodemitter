package tonegod.emitter.interpolation.impl;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import tonegod.emitter.interpolation.Interpolation;

/**
 * The base implementation of the {@link Interpolation}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractInterpolation implements Interpolation {

    /**
     * The name of this interpolation.
     */
    private final String name;

    protected AbstractInterpolation(final String name) {
        this.name = name;
    }

    @Override
    public float apply(final float a) {
        return 0;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void read(final JmeImporter im) throws IOException {
    }

    @Override
    public void write(final JmeExporter ex) throws IOException {
    }
}
