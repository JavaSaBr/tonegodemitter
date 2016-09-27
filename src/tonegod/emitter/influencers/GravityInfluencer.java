package tonegod.emitter.influencers;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import tonegod.emitter.particle.ParticleData;

/**
 * The implementation of the {@link ParticleInfluencer} for gravity influence to particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class GravityInfluencer implements ParticleInfluencer {

    public enum GravityAlignment {
        WORLD,
        REVERSE_VELOCITY,
        EMISSION_POINT,
        EMITTER_CENTER
    }

    private final transient Vector3f store;
    private final Vector3f gravity;

    private GravityAlignment alignment;

    private float magnitude;

    private boolean negativeVelocity;
    private boolean enabled;

    public GravityInfluencer() {
        this.alignment = GravityAlignment.WORLD;
        this.gravity = new Vector3f(0, 1f, 0);
        this.store = new Vector3f();
        this.magnitude = 1;
        this.enabled = true;
    }

    @NotNull
    @Override
    public String getName() {
        return "Gravity influencer";
    }

    @Override
    public void update(@NotNull final ParticleData particleData, final float tpf) {
        if (!enabled || particleData.emitterNode.isStaticParticles()) return;

        switch (alignment) {
            case WORLD:
                store.set(gravity).multLocal(tpf);
                particleData.velocity.subtractLocal(store);
                break;
            case REVERSE_VELOCITY:
                store.set(particleData.reverseVelocity).multLocal(tpf);
                particleData.velocity.addLocal(store);
                break;
            case EMISSION_POINT:
                particleData.emitterNode.getEmitterShape().setNext(particleData.triangleIndex);
                if (particleData.emitterNode.isRandomEmissionPoint())
                    store.set(particleData.emitterNode.getEmitterShape().getNextTranslation().addLocal(particleData.randomOffset));
                else
                    store.set(particleData.emitterNode.getEmitterShape().getNextTranslation());
                store.subtractLocal(particleData.position).multLocal(particleData.initialLength * magnitude).multLocal(tpf);
                particleData.velocity.addLocal(store);
                break;
            case EMITTER_CENTER:
                store.set(particleData.emitterNode.getEmitterShape().getMesh().getBound().getCenter());
                store.subtractLocal(particleData.position).multLocal(particleData.initialLength * magnitude).multLocal(tpf);
                particleData.velocity.addLocal(store);
                break;
        }
    }

    @Override
    public void initialize(@NotNull final ParticleData particleData) {
        particleData.reverseVelocity.set(particleData.velocity.negate().mult(magnitude));
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
    }

    /**
     * Aligns the gravity to the specified GravityAlignment
     */
    public void setAlignment(@NotNull final GravityAlignment alignment) {
        this.alignment = alignment;
    }

    /**
     * Returns the specified GravityAlignment
     */
    @NotNull
    public GravityAlignment getAlignment() {
        return alignment;
    }

    /**
     * Gravity multiplier
     */
    public void setMagnitude(final float magnitude) {
        this.magnitude = magnitude;
    }

    /**
     * Returns the current magnitude
     */
    public float getMagnitude() {
        return magnitude;
    }

    /**
     * Sets gravity to the provided Vector3f
     *
     * @param gravity Vector3f representing gravity
     */
    public void setGravity(@NotNull final Vector3f gravity) {
        this.gravity.set(gravity);
    }

    /**
     * Sets gravity per axis to the specified values
     *
     * @param x Gravity along the x axis
     * @param y Gravity along the y axis
     * @param z Gravity along the z axis
     */
    public void setGravity(final float x, final float y, final float z) {
        gravity.set(x, y, z);
    }

    /**
     * Returns the current gravity as a Vector3f
     */
    @NotNull
    public Vector3f getGravity() {
        return gravity;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(gravity, "gravity", new Vector3f(0, 1, 0));
        oc.write(enabled, "enabled", true);
        oc.write(negativeVelocity, "negativeVelocity", false);
        oc.write(magnitude, "magnitude", 1f);
        oc.write(alignment.name(), "alignment", GravityAlignment.WORLD.name());
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        gravity.set((Vector3f) ic.readSavable("gravity", new Vector3f(0, 1, 0)));
        enabled = ic.readBoolean("enabled", true);
        negativeVelocity = ic.readBoolean("negativeVelocity", false);
        magnitude = ic.readFloat("magnitude", 1);
        alignment = GravityAlignment.valueOf(ic.readString("alignment", GravityAlignment.WORLD.name()));
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        try {
            GravityInfluencer clone = (GravityInfluencer) super.clone();
            clone.setGravity(gravity);
            clone.enabled = enabled;
            clone.negativeVelocity = false;
            clone.magnitude = 1;
            clone.alignment = alignment;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
