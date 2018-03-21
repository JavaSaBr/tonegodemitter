package tonegod.emitter.influencers.impl;

import org.jetbrains.annotations.NotNull;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.particle.ParticleData;

/**
 * The base class to implement a particle influencer without additional data objects.
 *
 * @author JavaSaBr
 */
public abstract class AbstractWithoutDataParticleInfluencer extends AbstractParticleInfluencer<Void> {

    @Override
    public boolean isUsedDataObject() {
        return false;
    }

    @Override
    public void reset(@NotNull final ParticleEmitterNode emitterNode,
                      @NotNull final ParticleData particleData,
                      final int dataId) {
        resetImpl(emitterNode, particleData);
    }

    @Override
    public void initialize(@NotNull final ParticleEmitterNode emitterNode,
                           @NotNull final ParticleData particleData,
                           final int dataId) {

        super.initialize(emitterNode, particleData, dataId);
        initializeImpl(emitterNode, particleData);
    }

    @Override
    public void update(@NotNull final ParticleEmitterNode emitterNode,
                       @NotNull final ParticleData particleData,
                       final int dataId,
                       final float tpf) {

        if (isEnabled()) {
            updateImpl(emitterNode, particleData, tpf);
        }
    }

    /**
     * Resets the particle data to be used from this influencer in the next time.
     *
     * @param emitterNode  the emitter node.
     * @param particleData the particle data.
     */
    protected void resetImpl(@NotNull final ParticleEmitterNode emitterNode,
                             @NotNull final ParticleData particleData) {
    }

    /**
     * Initializes the particle data to be used from this this influencer.
     *
     * @param emitterNode  the emitter node.
     * @param particleData the particle data.
     */
    protected void initializeImpl(@NotNull final ParticleEmitterNode emitterNode,
                                  @NotNull final ParticleData particleData) {
    }

    /**
     * Updates the particle data.
     *
     * @param emitterNode  the emitter node.
     * @param particleData the particle data.
     * @param tpf          the tpf.
     */
    protected void updateImpl(@NotNull final ParticleEmitterNode emitterNode,
                              @NotNull final ParticleData particleData,
                              final float tpf) {
    }
}
