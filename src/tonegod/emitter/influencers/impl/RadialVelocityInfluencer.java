package tonegod.emitter.influencers.impl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import tonegod.emitter.EmitterMesh;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.particle.ParticleData;

/**
 * The implementation of the {@link ParticleInfluencer} for radial velocity influence to particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class RadialVelocityInfluencer extends AbstractParticleInfluencer {

    public enum RadialPullAlignment {
        EMISSION_POINT,
        EMITTER_CENTER;

        private static final RadialPullAlignment[] VALUES = values();

        public static RadialPullAlignment valueOf(final int index) {
            return VALUES[index];
        }
    }

    public enum RadialPullCenter {
        ABSOLUTE,
        VARIABLE_X,
        VARIABLE_Y,
        VARIABLE_Z;

        private static final RadialPullCenter[] VALUES = values();

        public static RadialPullCenter valueOf(final int index) {
            return VALUES[index];
        }
    }

    public enum RadialUpAlignment {
        NORMAL,
        UNIT_X,
        UNIT_Y,
        UNIT_Z;

        private static final RadialUpAlignment[] VALUES = values();

        public static RadialUpAlignment valueOf(final int index) {
            return VALUES[index];
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
    private RadialPullAlignment alignment;

    /**
     * The radial pull center.
     */
    @NotNull
    private RadialPullCenter center;

    /**
     * The radial upp alignment.
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
        this.alignment = RadialPullAlignment.EMISSION_POINT;
        this.center = RadialPullCenter.ABSOLUTE;
        this.upAlignment = RadialUpAlignment.UNIT_Y;
        this.radialPull = 1;
        this.tangentForce = 1;
    }

    @NotNull
    @Override
    public String getName() {
        return "Radial velocity influencer";
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {

        final ParticleEmitterNode emitterNode = particleData.getEmitterNode();
        final EmitterMesh emitterShape = emitterNode.getEmitterShape();
        final Quaternion localRotation = emitterNode.getLocalRotation();

        processAlignment(particleData, emitterNode, emitterShape);
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
                .multLocal(particleData.tangentForce)
                .multLocal(tpf);

        particleData.velocity.subtractLocal(tangent);
        particleData.velocity.addLocal(store.mult(radialPull, tempStore));

        super.updateImpl(particleData, tpf);
    }

    /**
     * Handle up alignment.
     */
    private void processUpAlignment(@NotNull final ParticleEmitterNode emitterNode,
                                    @NotNull final EmitterMesh emitterShape) {

        switch (upAlignment) {
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
     * Handle center.
     */
    private void processCenter(final @NotNull ParticleData particleData) {
        switch (center) {
            case ABSOLUTE: {
                break;
            }
            case VARIABLE_X: {
                store.setX(particleData.position.x);
                break;
            }
            case VARIABLE_Y: {
                store.setY(particleData.position.y);
                break;
            }
            case VARIABLE_Z: {
                store.setZ(particleData.position.z);
                break;
            }
        }
    }

    /**
     * Handle alignment.
     */
    private void processAlignment(@NotNull final ParticleData particleData,
                                  @NotNull final ParticleEmitterNode emitterNode,
                                  @NotNull final EmitterMesh emitterShape) {
        switch (alignment) {
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
    protected void initializeImpl(@NotNull final ParticleData particleData) {

        if (!isRandomDirection()) {
            particleData.tangentForce = tangentForce;
            return;
        }

        if (FastMath.rand.nextBoolean()) {
            particleData.tangentForce = tangentForce;
        } else {
            particleData.tangentForce = -tangentForce;
        }

        super.initializeImpl(particleData);
    }

    /**
     * The tangent force to apply when updating the particles trajectory
     */
    public void setTangentForce(final float force) {
        this.tangentForce = force;
    }

    /**
     * Returns the defined tangent force used when calculating the particles trajectory
     */
    public float getTangentForce() {
        return tangentForce;
    }

    /**
     * Defines the point of origin that the particle will use in calculating it's trajectory
     *
     * @param alignment the alignment.
     */
    public void setRadialPullAlignment(@NotNull final RadialPullAlignment alignment) {
        this.alignment = alignment;
    }

    /**
     * Returns the defined point of origin parameter
     */
    @NotNull
    public RadialPullAlignment getRadialPullAlignment() {
        return alignment;
    }

    /**
     * Alters how the particle will orbit it's radial pull alignment.  For example, VARIABLE_Y, will use the X/Z
     * components of the point of origin vector, but use the individual particles Y component when calculating the
     * updated trajectory.
     */
    public void setRadialPullCenter(@NotNull final RadialPullCenter center) {
        this.center = center;
    }

    /**
     * Returns the defined varient for the point of origin vector
     */
    @NotNull
    public RadialPullCenter getRadialPullCenter() {
        return center;
    }

    /**
     * Defines the gravitational force pulling against the tangent force - Or, how the orbit will tighten or decay over
     * time
     */
    public void setRadialPull(final float radialPull) {
        this.radialPull = radialPull;
    }

    /**
     * Returns the defined radial pull used when calculating the particles trajectory
     */
    public float getRadialPull() {
        return radialPull;
    }

    /**
     * Defines the up vector used to calculate rotation around a center point
     */
    public void setRadialUpAlignment(@NotNull final RadialUpAlignment upAlignment) {
        this.upAlignment = upAlignment;
    }

    /**
     * Returns the defined up vector parameter
     */
    @NotNull
    public RadialUpAlignment getRadialUpAlignment() {
        return upAlignment;
    }

    /**
     * Allows the influencer to randomly select the negative of the defined tangentForce to reverse the direction of
     * rotation
     */
    public void setRandomDirection(final boolean randomDirection) {
        this.randomDirection = randomDirection;
    }

    /**
     * Returns if the influencer allows random reverse rotation
     */
    public boolean isRandomDirection() {
        return randomDirection;
    }

    @Override
    public void write(@NotNull final JmeExporter ex) throws IOException {
        final OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(radialPull, "radialPull", 1.0f);
        capsule.write(tangentForce, "tangentForce", 1.0f);
        capsule.write(alignment.ordinal(), "alignment", RadialPullAlignment.EMISSION_POINT.ordinal());
        capsule.write(center.ordinal(), "center", RadialPullCenter.ABSOLUTE.ordinal());
        capsule.write(upAlignment.ordinal(), "upAlignment", RadialUpAlignment.UNIT_Y.ordinal());
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        final InputCapsule capsule = importer.getCapsule(this);
        radialPull = capsule.readFloat("radialPull", 1.0f);
        tangentForce = capsule.readFloat("tangentForce", 1.0f);
        alignment = RadialPullAlignment.valueOf(capsule.readInt("alignment", RadialPullAlignment.EMISSION_POINT.ordinal()));
        center = RadialPullCenter.valueOf(capsule.readInt("center", RadialPullCenter.ABSOLUTE.ordinal()));
        upAlignment = RadialUpAlignment.valueOf(capsule.readInt("upAlignment", RadialUpAlignment.UNIT_Y.ordinal()));
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        final RadialVelocityInfluencer clone = (RadialVelocityInfluencer) super.clone();
        clone.setRadialPull(radialPull);
        clone.setTangentForce(tangentForce);
        clone.setRadialPullAlignment(alignment);
        clone.setRadialPullCenter(center);
        clone.setRadialUpAlignment(upAlignment);
        return clone;
    }
}
