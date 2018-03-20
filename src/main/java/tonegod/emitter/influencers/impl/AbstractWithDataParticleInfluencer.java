package tonegod.emitter.influencers.impl;

import org.jetbrains.annotations.NotNull;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.particle.ParticleData;

/**
 * The base class to implement a particle influencer with additional data objects.
 *
 * @author JavaSaBr
 */
public abstract class AbstractWithDataParticleInfluencer<D> extends AbstractParticleInfluencer<D> {

    @Override
    public boolean isUsedDataObject() {
        return true;
    }

    @Override
    public void reset(@NotNull final ParticleEmitterNode emitterNode,
                      @NotNull final ParticleData particleData,
                      final int dataId) {
        resetImpl(emitterNode, particleData, (D) particleData.getData(dataId));
    }

    @Override
    public void initialize(@NotNull final ParticleEmitterNode emitterNode,
                           @NotNull final ParticleData particleData,
                           final int dataId) {

        super.initialize(emitterNode, particleData, dataId);
        initializeImpl(particleData, (D) particleData.getData(dataId));
    }

    @Override
    public void update(@NotNull final ParticleEmitterNode emitterNode,
                       @NotNull final ParticleData particleData,
                       final int dataId,
                       final float tpf) {

        if (isEnabled()) {
            updateImpl(emitterNode, particleData, (D) particleData.getData(dataId), tpf);
        }
    }

    /**
     * Resets the particle data to be used from this influencer in the next time.
     *
     * @param emitterNode  the emitter node.
     * @param particleData the particle data.
     * @param data         the influencer's data.
     */
    protected void resetImpl(@NotNull final ParticleEmitterNode emitterNode,
                             @NotNull final ParticleData particleData,
                             @NotNull final D data) {
    }

    /**
     * Initializes the particle data to be used from this this influencer.
     *
     * @param particleData the particle data.
     * @param data         the influencer's data.
     */
    protected void initializeImpl(@NotNull final ParticleData particleData, @NotNull final D data) {
    }

    /**
     * Updates the particle data.
     *
     * @param emitterNode  the emitter node.
     * @param particleData the particle data.
     * @param data         the influencer's data.
     * @param tpf          the tpf.
     */
    protected void updateImpl(@NotNull final ParticleEmitterNode emitterNode,
                              @NotNull final ParticleData particleData,
                              @NotNull final D data,
                              final float tpf) {
    }
}
