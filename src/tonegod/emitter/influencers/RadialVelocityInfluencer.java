package tonegod.emitter.influencers;

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
import tonegod.emitter.particle.ParticleData;

/**
 * The implementation of the {@link ParticleInfluencer} for radial velocity influence to particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class RadialVelocityInfluencer implements ParticleInfluencer {

    public enum RadialPullAlignment {
        EMISSION_POINT,
        EMITTER_CENTER
    }

    public enum RadialPullCenter {
        ABSOLUTE,
        VARIABLE_X,
        VARIABLE_Y,
        VARIABLE_Z
    }

    public enum RadialUpAlignment {
        NORMAL,
        UNIT_X,
        UNIT_Y,
        UNIT_Z
    }

    private final Vector3f tangent;
    private final Vector3f store;
    private final Vector3f up;
    private final Vector3f left;
    private final Vector3f upStore;

    private final Quaternion quaternion;

    private RadialPullAlignment alignment;
    private RadialPullCenter center;
    private RadialUpAlignment upAlignment;

    private float radialPull;
    private float tangentForce;

    private boolean randomDirection;
    private boolean enabled;

    public RadialVelocityInfluencer() {
        this.tangent = new Vector3f();
        this.store = new Vector3f();
        this.up = Vector3f.UNIT_Y.clone();
        this.left = new Vector3f();
        this.upStore = new Vector3f();
        this.alignment = RadialPullAlignment.EMISSION_POINT;
        this.center = RadialPullCenter.ABSOLUTE;
        this.upAlignment = RadialUpAlignment.UNIT_Y;
        this.quaternion = new Quaternion();
        this.radialPull = 1;
        this.tangentForce = 1;
        this.enabled = true;
    }

    @NotNull
    @Override
    public String getName() {
        return "Radial velocity influencer";
    }

    @Override
    public void update(@NotNull final ParticleData particleData, final float tpf) {
        if (!enabled) return;

        final ParticleEmitterNode emitterNode = particleData.emitterNode;
        final EmitterMesh emitterShape = emitterNode.getEmitterShape();

        switch (alignment) {
            case EMISSION_POINT: {

                emitterShape.setNext(particleData.triangleIndex);

                if (emitterNode.isRandomEmissionPoint()) {
                    store.set(emitterShape.getNextTranslation().addLocal(particleData.randomOffset));
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

        store.subtractLocal(particleData.position).normalizeLocal().multLocal(particleData.initialLength * radialPull).multLocal(tpf);

        switch (upAlignment) {
            case NORMAL: {
                upStore.set(emitterNode.getLocalRotation().inverse().mult(upStore.set(emitterShape.getNormal())));
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

        up.set(store).crossLocal(upStore).normalizeLocal();
        //FIXME memory problem
        up.set(emitterNode.getLocalRotation().mult(up));
        left.set(store).crossLocal(up).normalizeLocal();

        tangent.set(store).crossLocal(left).normalizeLocal().multLocal(particleData.tangentForce).multLocal(tpf);

        particleData.velocity.subtractLocal(tangent);
        particleData.velocity.addLocal(store.mult(radialPull));
    }

    @Override
    public void initialize(@NotNull final ParticleData particleData) {
        if (!randomDirection) {
            particleData.tangentForce = tangentForce;
            return;
        }

        if (FastMath.rand.nextBoolean()) {
            particleData.tangentForce = tangentForce;
        } else {
            particleData.tangentForce = -tangentForce;
        }
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
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
     * @param alignment \
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
     * Alters how the particle will orbit it's radial pull alignment.  For example, VARIABLE_Y, will
     * use the X/Z components of the point of origin vector, but use the individual particles Y
     * component when calculating the updated trajectory.
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
     * Defines the gravitational force pulling against the tangent force - Or, how the orbit will
     * tighten or decay over time
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
     * Allows the influencer to randomly select the negative of the defined tangentForce to reverse
     * the direction of rotation
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
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(enabled, "enabled", true);
        oc.write(radialPull, "radialPull", 1.0f);
        oc.write(tangentForce, "tangentForce", 1.0f);
        oc.write(alignment.name(), "alignment", RadialPullAlignment.EMISSION_POINT.name());
        oc.write(center.name(), "center", RadialPullCenter.ABSOLUTE.name());
        oc.write(upAlignment.name(), "upAlignment", RadialUpAlignment.UNIT_Y.name());
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        enabled = ic.readBoolean("enabled", true);
        radialPull = ic.readFloat("radialPull", 1.0f);
        tangentForce = ic.readFloat("tangentForce", 1.0f);
        alignment = RadialPullAlignment.valueOf(ic.readString("alignment", RadialPullAlignment.EMISSION_POINT.name()));
        center = RadialPullCenter.valueOf(ic.readString("center", RadialPullCenter.ABSOLUTE.name()));
        upAlignment = RadialUpAlignment.valueOf(ic.readString("upAlignment", RadialUpAlignment.UNIT_Y.name()));
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        try {
            RadialVelocityInfluencer clone = (RadialVelocityInfluencer) super.clone();
            clone.setEnabled(enabled);
            clone.setRadialPull(radialPull);
            clone.setTangentForce(tangentForce);
            clone.setRadialPullAlignment(alignment);
            clone.setRadialPullCenter(center);
            clone.setRadialUpAlignment(upAlignment);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
