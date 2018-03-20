package tonegod.emitter.influencers;

import com.jme3.export.Savable;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.particle.ParticleData;

/**
 * The interface for implementing particle influencers.
 *
 * @author t0neg0d, JavaSaBr
 */
public interface ParticleInfluencer<D> extends Savable, Cloneable {

    /**
     * Gets the influencer's name.
     *
     * @return the influencer's name.
     */
    @NotNull String getName();

    /**
     * Clones the influencer instance.
     *
     * @return cloned instance.
     */
    @NotNull ParticleInfluencer clone();

    /**
     * Returns true if this influencer uses its own data object.
     *
     * @return true if this influencer uses its own data object.
     */
    boolean isUsedDataObject();

    /**
     * Creates a new data object of this influencer.
     *
     * @return the new data object of this influencer.
     */
    @NotNull D newDataObject();

    /**
     * Create and put influencer's data to the particle data.
     *
     * @param emitterNode  the particle emitter node.
     * @param particleData The particle data.
     * @param dataId       the influencer's data id.
     */
    void createData(@NotNull ParticleEmitterNode emitterNode, @NotNull ParticleData particleData, int dataId);

    /**
     * Stores if need unused data object.
     *
     * @param emitterNode  the particle emitter node.
     * @param particleData The particle data.
     * @param dataId       the influencer's data id.
     */
    void storeUsedData(@NotNull ParticleEmitterNode emitterNode, @NotNull ParticleData particleData, int dataId);

    /**
     * Updates state of the particle data from this influencers.
     *
     * @param emitterNode  the particle emitter node.
     * @param particleData The particle data.
     * @param dataId       the influencer's data id.
     * @param tpf          the time since last frame.
     */
    void update(@NotNull ParticleEmitterNode emitterNode, @NotNull ParticleData particleData, int dataId, float tpf);

    /**
     * Initialize the particle data to work with this influencers.
     *
     * @param emitterNode  the particle emitter node.
     * @param particleData The particle data.
     * @param dataId       the influencer's data id.
     */
    void initialize(@NotNull ParticleEmitterNode emitterNode, @NotNull ParticleData particleData, int dataId);

    /**
     * Called once the life span of the particle has been reached.
     *
     * @param emitterNode  the particle emitter node.
     * @param particleData The particle that was removed.
     * @param dataId       the influencer's data id.
     */
    void reset(@NotNull ParticleEmitterNode emitterNode, @NotNull ParticleData particleData, int dataId);

    /**
     * Enables/disables the influencer without removing it from the chain. It is worth noting that
     * initialize can still be used whether or not the influencer has been disabled.
     *
     * @param enable the enable
     */
    void setEnabled(boolean enable);

    /**
     * Returns if the influencer is currently enabled
     *
     * @return the boolean
     */
    boolean isEnabled();
}