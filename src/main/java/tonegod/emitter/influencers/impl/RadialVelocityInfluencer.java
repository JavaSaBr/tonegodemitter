package tonegod.emitter.influencers.impl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.EmitterMesh;
import tonegod.emitter.Messages;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.particle.ParticleData;
import tonegod.emitter.util.RandomUtils;

import java.io.IOException;
import java.util.Random;

/**
 * The implementation of the {@link ParticleInfluencer} to radial rotation particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public class RadialVelocityInfluencer extends AbstractWithDataParticleInfluencer<RadialVelocityInfluencer.RadialVelocityData> {

    protected static class RadialVelocityData implements JmeCloneable {

        private float tangentForce;

        @Override
        public @NotNull Object jmeClone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void cloneFields(@NotNull Cloner cloner, @NotNull Object original) {
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final RadialVelocityData that = (RadialVelocityData) o;
            return Float.compare(that.tangentForce, tangentForce) == 0;
        }

        @Override
        public int hashCode() {
            return (tangentForce != +0.0f ? Float.floatToIntBits(tangentForce) : 0);
        }

        @Override
        public String toString() {
            return "RadialVelocityData{" + "tangentForce=" + tangentForce + '}';
        }
    }

    /**
     * The list of radial pull alignments.
     */
    public enum RadialPullAlignment {
        /**
         * Emission point radial pull alignment.
         */
        EMISSION_POINT(Messages.PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_ALIGNMENT_EMISSION_POINT),
        /**
         * Emitter center radial pull alignment.
         */
        EMITTER_CENTER(Messages.PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_ALIGNMENT_EMITTER_CENTER);

        private static final RadialPullAlignment[] VALUES = values();

        /**
         * Get a radial pull alignment by the index.
         *
         * @param index the index.
         * @return the radial pull alignment.
         */
        public static @NotNull RadialPullAlignment valueOf(int index) {
            return VALUES[index];
        }

        @NotNull
        private final String name;

        RadialPullAlignment(@NotNull String name) {
            this.name = name;
        }

        @Override
        public @NotNull String toString() {
            return name;
        }
    }

    /**
     * The list of radial pull centers.
     */
    public enum RadialPullCenter {
        /**
         * Absolute radial pull center.
         */
        ABSOLUTE(Messages.PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_ABSOLUTE),
        /**
         * Position x radial pull center.
         */
        POSITION_X(Messages.PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_POSITION_X),
        /**
         * Position y radial pull center.
         */
        POSITION_Y(Messages.PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_POSITION_Y),
        /**
         * Position z radial pull center.
         */
        POSITION_Z(Messages.PARTICLE_INFLUENCER_RADIAL_VELOCITY_PULL_CENTER_POSITION_Z);

        private static final RadialPullCenter[] VALUES = values();

        /**
         * Get a radial pull center by the index.
         *
         * @param index the index.
         * @return the radial pull center.
         */
        public static @NotNull RadialPullCenter valueOf(int index) {
            return VALUES[index];
        }

        @NotNull
        private final String name;

        RadialPullCenter(@NotNull String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * The list of radial up alignments.
     */
    public enum RadialUpAlignment {
        /**
         * Normal radial up alignment.
         */
        NORMAL(Messages.PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_NORMAL),
        /**
         * Unit x radial up alignment.
         */
        UNIT_X(Messages.PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_UNIT_X),
        /**
         * Unit y radial up alignment.
         */
        UNIT_Y(Messages.PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_UNIT_Y),
        /**
         * Unit z radial up alignment.
         */
        UNIT_Z(Messages.PARTICLE_INFLUENCER_RADIAL_VELOCITY_UP_ALIGNMENT_UNIT_Z);

        private static final RadialUpAlignment[] VALUES = values();

        /**
         * Get a radial up alignment by the index.
         *
         * @param index the index.
         * @return the radial up alignment.
         */
        public static @NotNull RadialUpAlignment valueOf(int index) {
            return VALUES[index];
        }

        @NotNull
        private final String name;

        RadialUpAlignment(@NotNull String name) {
            this.name = name;
        }

        @Override
        public @NotNull String toString() {
            return name;
        }
    }

    /**
     * The tangent.
     */
    @NotNull
    private final Vector3f tangent;

    /**
     * The vector store.
     */
    @NotNull
    private final Vector3f store;

    /**
     * The up vector.
     */
    @NotNull
    private final Vector3f up;

    /**
     * The left vector.
     */
    @NotNull
    private final Vector3f left;

    /**
     * The vector for storing up vector.
     */
    @NotNull
    private final Vector3f upStore;

    /**
     * The temp store vector.
     */
    @NotNull
    private final Vector3f tempStore;

    /**
     * The inverse rotation.
     */
    @NotNull
    private final Quaternion inverseRotation;

    /**
     * The radial pull alignment.
     */
    @NotNull
    private RadialPullAlignment pullAlignment;

    /**
     * The radial pull center.
     */
    @NotNull
    private RadialPullCenter pullCenter;

    /**
     * The radial up alignment.
     */
    @NotNull
    private RadialUpAlignment upAlignment;

    /**
     * The value of radial pulling.
     */
    private float radialPull;

    /**
     * The value of tangent force.
     */
    private float tangentForce;

    /**
     * The flag of using random directions.
     */
    private boolean randomDirection;

    public RadialVelocityInfluencer() {
        this.tangent = new Vector3f();
        this.store = new Vector3f();
        this.up = Vector3f.UNIT_Y.clone();
        this.left = new Vector3f();
        this.upStore = new Vector3f();
        this.tempStore = new Vector3f();
        this.inverseRotation = new Quaternion();
        this.pullAlignment = RadialPullAlignment.EMISSION_POINT;
        this.pullCenter = RadialPullCenter.ABSOLUTE;
        this.upAlignment = RadialUpAlignment.UNIT_Y;
        this.radialPull = 1;
        this.tangentForce = 1;
    }

    @Override
    public @NotNull String getName() {
        return Messages.PARTICLE_INFLUENCER_RADIAL_VELOCITY;
    }

    @Override
    public @NotNull RadialVelocityInfluencer.RadialVelocityData newDataObject() {
        return new RadialVelocityData();
    }

    @Override
    protected void updateImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull RadialVelocityInfluencer.RadialVelocityData data,
            float tpf
    ) {

        EmitterMesh emitterShape = emitterNode.getEmitterShape();
        Quaternion localRotation = emitterNode.getLocalRotation();

        processPullAlignment(particleData, emitterNode, emitterShape);
        processCenter(particleData);

        store.subtractLocal(particleData.getPosition())
            .normalizeLocal()
            .multLocal(particleData.getInitialLength() * radialPull)
            .multLocal(tpf);

        processUpAlignment(emitterNode, emitterShape);

        up.set(store).crossLocal(upStore)
            .normalizeLocal()
            .set(localRotation.mult(up, tempStore));

        left.set(store).crossLocal(up)
            .normalizeLocal();

        tangent.set(store)
            .crossLocal(left)
            .normalizeLocal()
            .multLocal(data.tangentForce)
            .multLocal(tpf);

        particleData.velocity.subtractLocal(tangent);
        particleData.velocity.addLocal(store.mult(radialPull, tempStore));

        super.updateImpl(emitterNode, particleData, data, tpf);
    }

    /**
     * Handle up alignment.
     */
    private void processUpAlignment(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull EmitterMesh emitterShape
    ) {

        switch (getRadialUpAlignment()) {
            case NORMAL: {
                inverseRotation.set(emitterNode.getLocalRotation()).inverseLocal();
                upStore.set(inverseRotation.mult(upStore.set(emitterShape.getNormal()), tempStore));
                break;
            }
            case UNIT_X: {
                upStore.set(Vector3f.UNIT_X);
                break;
            }
            case UNIT_Y: {
                upStore.set(Vector3f.UNIT_Y);
                break;
            }
            case UNIT_Z: {
                upStore.set(Vector3f.UNIT_Z);
                break;
            }
        }
    }

    /**
     * Handle pull center.
     */
    private void processCenter(@NotNull ParticleData particleData) {
        switch (getRadialPullCenter()) {
            case ABSOLUTE: {
                break;
            }
            case POSITION_X: {
                store.setX(particleData.position.x);
                break;
            }
            case POSITION_Y: {
                store.setY(particleData.position.y);
                break;
            }
            case POSITION_Z: {
                store.setZ(particleData.position.z);
                break;
            }
        }
    }

    /**
     * Handle pull alignment.
     */
    private void processPullAlignment(
            @NotNull ParticleData particleData,
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull EmitterMesh emitterShape
    ) {

        switch (getRadialPullAlignment()) {
            case EMISSION_POINT: {

                emitterShape.setNext(particleData.triangleIndex);

                if (emitterNode.isRandomEmissionPoint()) {
                    store.set(emitterShape.getNextTranslation()
                            .addLocal(particleData.getRandomOffset()));
                } else {
                    store.set(emitterShape.getNextTranslation());
                }

                break;
            }
            case EMITTER_CENTER: {
                store.set(emitterShape.getMesh().getBound().getCenter());
                break;
            }
        }
    }

    @Override
    protected void initializeImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull RadialVelocityInfluencer.RadialVelocityData data
    ) {

        if (!isRandomDirection()) {
            data.tangentForce = tangentForce;
            return;
        }

        Random random = RandomUtils.getRandom();

        if (random.nextBoolean()) {
            data.tangentForce = tangentForce;
        } else {
            data.tangentForce = -tangentForce;
        }

        super.initializeImpl(emitterNode, particleData, data);
    }

    /**
     * The tangent force to apply when updating the particles trajectory.
     *
     * @param tangentForce the tangent force.
     */
    public void setTangentForce(float tangentForce) {
        this.tangentForce = tangentForce;
    }

    /**
     * Returns the defined tangent force used when calculating the particles trajectory.
     *
     * @return the tangent force.
     */
    public float getTangentForce() {
        return tangentForce;
    }

    /**
     * Defines the point of origin that the particle will use in calculating it's trajectory.
     *
     * @param alignment the pullAlignment.
     */
    public void setRadialPullAlignment(@NotNull RadialPullAlignment alignment) {
        this.pullAlignment = alignment;
    }

    /**
     * Returns the defined point of origin parameter.
     *
     * @return the radial pull alignment.
     */
    public @NotNull RadialPullAlignment getRadialPullAlignment() {
        return pullAlignment;
    }

    /**
     * Alters how the particle will orbit it's radial pull pullAlignment.  For example, POSITION_Y, will use the X/Z
     * components of the point of origin vector, but use the individual particles Y component when calculating the
     * updated trajectory.
     *
     * @param center the center.
     */
    public void setRadialPullCenter(@NotNull RadialPullCenter center) {
        this.pullCenter = center;
    }

    /**
     * Returns the defined variant for the point of origin vector.
     *
     * @return the radial pull center.
     */
    public @NotNull RadialPullCenter getRadialPullCenter() {
        return pullCenter;
    }

    /**
     * Defines the gravitational force pulling against the tangent force - Or, how the orbit will tighten or decay over
     * time.
     *
     * @param radialPull the radial pull.
     */
    public void setRadialPull(float radialPull) {
        this.radialPull = radialPull;
    }

    /**
     * Returns the defined radial pull used when calculating the particles trajectory.
     *
     * @return the radial pull.
     */
    public float getRadialPull() {
        return radialPull;
    }

    /**
     * Defines the up vector used to calculate rotation around a pullCenter point.
     *
     * @param upAlignment the up alignment.
     */
    public void setRadialUpAlignment(@NotNull RadialUpAlignment upAlignment) {
        this.upAlignment = upAlignment;
    }

    /**
     * Returns the defined up vector parameter.
     *
     * @return the radial up alignment.
     */
    public @NotNull RadialUpAlignment getRadialUpAlignment() {
        return upAlignment;
    }

    /**
     * Allows the influencer to randomly select the negative of the defined tangentForce to reverse the direction of
     * rotation
     *
     * @param randomDirection the random direction
     */
    public void setRandomDirection(boolean randomDirection) {
        this.randomDirection = randomDirection;
    }

    /**
     * Returns true if the influencer allows random reverse rotation.
     *
     * @return true if the influencer allows random reverse rotation.
     */
    public boolean isRandomDirection() {
        return randomDirection;
    }

    @Override
    public void write(@NotNull JmeExporter exporter) throws IOException {
        super.write(exporter);

        OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(radialPull, "radialPull", 1.0f);
        capsule.write(tangentForce, "tangentForce", 1.0f);
        capsule.write(pullAlignment.ordinal(), "pullAlignment", RadialPullAlignment.EMISSION_POINT.ordinal());
        capsule.write(pullCenter.ordinal(), "pullCenter", RadialPullCenter.ABSOLUTE.ordinal());
        capsule.write(upAlignment.ordinal(), "upAlignment", RadialUpAlignment.UNIT_Y.ordinal());
    }

    @Override
    public void read(@NotNull JmeImporter importer) throws IOException {
        super.read(importer);

        InputCapsule capsule = importer.getCapsule(this);
        radialPull = capsule.readFloat("radialPull", 1.0f);
        tangentForce = capsule.readFloat("tangentForce", 1.0f);
        pullAlignment = RadialPullAlignment.valueOf(capsule.readInt("pullAlignment", capsule.readInt("alignment", RadialPullAlignment.EMISSION_POINT.ordinal())));
        pullCenter = RadialPullCenter.valueOf(capsule.readInt("pullCenter", capsule.readInt("center", RadialPullCenter.ABSOLUTE.ordinal())));
        upAlignment = RadialUpAlignment.valueOf(capsule.readInt("upAlignment", RadialUpAlignment.UNIT_Y.ordinal()));
    }

    @Override
    public @NotNull ParticleInfluencer clone() {
        RadialVelocityInfluencer clone = (RadialVelocityInfluencer) super.clone();
        clone.setRadialPull(radialPull);
        clone.setTangentForce(tangentForce);
        clone.setRadialPullAlignment(pullAlignment);
        clone.setRadialPullCenter(pullCenter);
        clone.setRadialUpAlignment(upAlignment);
        return clone;
    }
}
