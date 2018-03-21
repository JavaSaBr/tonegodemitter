package tonegod.emitter.influencers.impl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.particle.ParticleData;

import java.io.IOException;

/**
 * Base implementation of the {@link ParticleInfluencer}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractParticleInfluencer<D> implements ParticleInfluencer<D> {

    /**
     * The flag of enabling this influencer.
     */
    private boolean enabled;

    /**
     * The flag of initializing this influencer.
     */
    private boolean initialized;

    public AbstractParticleInfluencer() {
        this.enabled = true;
    }

    @Override
    public @NotNull D newDataObject() {
        throw new IllegalStateException("This influencer doesn't use its own data object.");
    }

    @Override
    public void initialize(@NotNull final ParticleEmitterNode emitterNode,
                           @NotNull final ParticleData particleData,
                           final int dataId) {

        if (!isInitialized()) {
            firstInitializeImpl(particleData);
            setInitialized(true);
        }
    }

    @Override
    public void createData(@NotNull final ParticleEmitterNode emitterNode,
                           @NotNull final ParticleData particleData,
                           final int dataId) {
    }

    @Override
    public void storeUsedData(@NotNull final ParticleEmitterNode emitterNode,
                              @NotNull final ParticleData particleData,
                              final int dataId) {
    }

    /**
     * Handle first initializing this influencer.
     *
     * @param particleData the particle data.
     */
    protected void firstInitializeImpl(@NotNull final ParticleData particleData) {
    }

    @Override
    public final void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public final boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns true if this influencer is initialized.
     *
     * @return true if this influencer is initialized.
     */
    private boolean isInitialized() {
        return initialized;
    }

    /**
     * Sets the flag of initializing this influencer.
     *
     * @param initialized the flag of initializing this influencer.
     */
    private void setInitialized(final boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(enabled, "enabled", true);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        final InputCapsule capsule = importer.getCapsule(this);
        enabled = capsule.readBoolean("enabled", true);
    }

    @Override
    public @NotNull ParticleInfluencer clone() {
        try {
            final AbstractParticleInfluencer clone = (AbstractParticleInfluencer) super.clone();
            clone.enabled = enabled;
            return clone;
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
