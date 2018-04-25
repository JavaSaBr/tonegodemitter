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
    public void createData(@NotNull ParticleEmitterNode emitterNode, @NotNull ParticleData particleData, int dataId) {
        super.createData(emitterNode, particleData, dataId);
        particleData.initializeData(this, dataId, emitterNode.getParticleDataSize());
    }

    @Override
    public void storeUsedData(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            int dataId
    ) {
        super.storeUsedData(emitterNode, particleData, dataId);
        storeUsedData((D) particleData.getData(dataId));
    }

    @Override
    public void reset(@NotNull ParticleEmitterNode emitterNode, @NotNull ParticleData particleData, int dataId) {
        resetImpl(emitterNode, particleData, (D) particleData.getData(dataId));
    }

    @Override
    public void initialize(@NotNull ParticleEmitterNode emitterNode, @NotNull ParticleData particleData, int dataId) {
        super.initialize(emitterNode, particleData, dataId);
        initializeImpl(emitterNode, particleData, (D) particleData.getData(dataId));
    }

    @Override
    public void update(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            int dataId,
            float tpf
    ) {
        if (isEnabled()) {
            updateImpl(emitterNode, particleData, (D) particleData.getData(dataId), tpf);
        }
    }

    /**
     * Stores used data from some particle's data object.
     *
     * @param data the used data object.
     */
    protected void storeUsedData(@NotNull D data) {
    }

    /**
     * Resets the particle data to be used from this influencer in the next time.
     *
     * @param emitterNode  the emitter node.
     * @param particleData the particle data.
     * @param data         the influencer's data.
     */
    protected void resetImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull D data
    ) {
    }

    /**
     * Initializes the particle data to be used from this this influencer.
     *
     * @param emitterNode  the emitter node.
     * @param particleData the particle data.
     * @param data         the influencer's data.
     */
    protected void initializeImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull D data
    ) {
    }

    /**
     * Updates the particle data.
     *
     * @param emitterNode  the emitter node.
     * @param particleData the particle data.
     * @param data         the influencer's data.
     * @param tpf          the tpf.
     */
    protected void updateImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull D data,
            float tpf
    ) {
    }
}
