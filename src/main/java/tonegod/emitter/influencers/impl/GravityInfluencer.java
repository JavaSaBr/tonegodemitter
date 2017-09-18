package tonegod.emitter.influencers.impl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.EmitterMesh;
import tonegod.emitter.Messages;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.particle.ParticleData;

import java.io.IOException;

/**
 * The implementation of the {@link ParticleInfluencer} for gravity influence to particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public class GravityInfluencer extends AbstractParticleInfluencer {

    /**
     * The enum Gravity alignment.
     */
    public enum GravityAlignment {
        /**
         * World gravity alignment.
         */
        WORLD(Messages.PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_WORLD),
        /**
         * Reverse velocity gravity alignment.
         */
        REVERSE_VELOCITY(Messages.PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_REVERSE_VELOCITY),
        /**
         * Emission point gravity alignment.
         */
        EMISSION_POINT(Messages.PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_EMISSION_POINT),
        /**
         * Emitter center gravity alignment.
         */
        EMITTER_CENTER(Messages.PARTICLE_INFLUENCER_GRAVITY_ALIGNMENT_EMITTER_CENTER);

        private static final GravityAlignment[] VALUES = values();

        /**
         * Value of gravity alignment.
         *
         * @param index the index
         * @return the gravity alignment
         */
        public static GravityAlignment valueOf(final int index) {
            return VALUES[index];
        }

        @NotNull
        private final String name;

        GravityAlignment(@NotNull final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    /**
     * The vector for storing results.
     */
    @NotNull
    private final transient Vector3f store;

    /**
     * The gravity vector.
     */
    @NotNull
    private final Vector3f gravity;

    /**
     * The gravity alignment.
     */
    @NotNull
    private GravityAlignment alignment;

    /**
     * The magnitude.
     */
    private float magnitude;

    /**
     * The flag of using negate velocity.
     */
    private boolean negativeVelocity;

    public GravityInfluencer() {
        this.alignment = GravityAlignment.WORLD;
        this.gravity = new Vector3f(0, 1f, 0);
        this.store = new Vector3f();
        this.magnitude = 1;
    }

    @Override
    public @NotNull String getName() {
        return Messages.PARTICLE_INFLUENCER_GRAVITY;
    }

    @Override
    public void update(@NotNull final ParticleData particleData, final float tpf) {
        final ParticleEmitterNode emitterNode = particleData.getEmitterNode();
        if (emitterNode.isStaticParticles()) return;
        super.update(particleData, tpf);
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {

        final Vector3f velocity = particleData.getVelocity();
        final Vector3f store = getStore();

        switch (getAlignment()) {
            case WORLD: {
                store.set(getGravity()).multLocal(tpf);
                velocity.subtractLocal(store);
                break;
            }
            case REVERSE_VELOCITY: {
                store.set(particleData.getReverseVelocity()).multLocal(tpf);
                velocity.addLocal(store);
                break;
            }
            case EMISSION_POINT: {

                final ParticleEmitterNode emitterNode = particleData.getEmitterNode();
                final EmitterMesh emitterShape = emitterNode.getEmitterShape();
                emitterShape.setNext(particleData.triangleIndex);

                if (emitterNode.isRandomEmissionPoint()) {
                    store.set(emitterShape.getNextTranslation()
                            .addLocal(particleData.getRandomOffset()));
                } else {
                    store.set(emitterShape.getNextTranslation())
                            .subtractLocal(particleData.getPosition())
                            .multLocal(particleData.getInitialLength() * getMagnitude())
                            .multLocal(tpf);
                }

                velocity.addLocal(store);
                break;
            }
            case EMITTER_CENTER: {

                final ParticleEmitterNode emitterNode = particleData.getEmitterNode();
                final EmitterMesh emitterShape = emitterNode.getEmitterShape();

                store.set(emitterShape.getMesh().getBound().getCenter())
                        .subtractLocal(particleData.getPosition())
                        .multLocal(particleData.getInitialLength() * getMagnitude())
                        .multLocal(tpf);

                velocity.addLocal(store);
                break;
            }
        }

        super.updateImpl(particleData, tpf);
    }

    @Override
    protected void initializeImpl(@NotNull final ParticleData particleData) {

        store.set(particleData.getVelocity())
                .negateLocal()
                .multLocal(magnitude);

        particleData.reverseVelocity.set(store);

        super.initializeImpl(particleData);
    }

    /**
     * Aligns the gravity to the specified GravityAlignment
     *
     * @param alignment the alignment
     */
    public final void setAlignment(@NotNull final GravityAlignment alignment) {
        this.alignment = alignment;
    }

    /**
     * Returns the specified GravityAlignment
     *
     * @return the alignment
     */
    public final @NotNull GravityAlignment getAlignment() {
        return alignment;
    }

    /**
     * Gravity multiplier
     *
     * @param magnitude the magnitude
     */
    public final void setMagnitude(final float magnitude) {
        this.magnitude = magnitude;
    }

    /**
     * Returns the current magnitude
     *
     * @return the magnitude
     */
    public final float getMagnitude() {
        return magnitude;
    }

    /**
     * Sets gravity to the provided Vector3f
     *
     * @param gravity Vector3f representing gravity
     */
    public final void setGravity(@NotNull final Vector3f gravity) {
        this.gravity.set(gravity);
    }

    /**
     * Sets gravity per axis to the specified values
     *
     * @param x Gravity along the x axis
     * @param y Gravity along the y axis
     * @param z Gravity along the z axis
     */
    public final void setGravity(final float x, final float y, final float z) {
        gravity.set(x, y, z);
    }

    /**
     * Returns the current gravity as a Vector3f
     *
     * @return the gravity
     */
    public final @NotNull Vector3f getGravity() {
        return gravity;
    }

    /**
     * Gets store.
     *
     * @return the store.
     */
    protected final @NotNull Vector3f getStore() {
        return store;
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(gravity, "gravity", new Vector3f(0, 1, 0));
        capsule.write(negativeVelocity, "negativeVelocity", false);
        capsule.write(magnitude, "magnitude", 1f);
        capsule.write(alignment.ordinal(), "alignmentOrder", GravityAlignment.WORLD.ordinal());
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        final InputCapsule capsule = importer.getCapsule(this);
        gravity.set((Vector3f) capsule.readSavable("gravity", new Vector3f(0, 1, 0)));
        negativeVelocity = capsule.readBoolean("negativeVelocity", false);
        magnitude = capsule.readFloat("magnitude", 1);
        alignment = GravityAlignment.valueOf(capsule.readInt("alignmentOrder",
                GravityAlignment.valueOf(capsule.readString("alignment", GravityAlignment.WORLD.name())).ordinal()));
    }

    @Override
    public @NotNull ParticleInfluencer clone() {
        final GravityInfluencer clone = (GravityInfluencer) super.clone();
        clone.setGravity(gravity);
        clone.negativeVelocity = false;
        clone.magnitude = 1;
        clone.alignment = alignment;
        return clone;
    }
}
