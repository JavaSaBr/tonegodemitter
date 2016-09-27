package tonegod.emitter.influencers;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import tonegod.emitter.particle.ParticleData;

/**
 * The implementation of the {@link ParticleInfluencer} for impulse influence to particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class ImpulseInfluencer implements ParticleInfluencer {

    private final transient Vector3f temp;
    private final transient Vector3f velocityStore;

    private float chance;
    private float magnitude;
    private float strength;

    private boolean enabled;

    public ImpulseInfluencer() {
        this.temp = new Vector3f();
        this.velocityStore = new Vector3f();
        this.chance = 0.02f;
        this.magnitude = 0.2f;
        this.strength = 3;
        this.enabled = true;
    }

    @NotNull
    @Override
    public String getName() {
        return "Impulse influencer";
    }

    @Override
    public void update(@NotNull final ParticleData particleData, final float tpf) {
        if (!enabled) return;
        if (FastMath.rand.nextFloat() <= 1 - (chance + tpf)) return;

        velocityStore.set(particleData.velocity);

        temp.set(FastMath.nextRandomFloat() * strength,
                FastMath.nextRandomFloat() * strength,
                FastMath.nextRandomFloat() * strength
        );

        if (FastMath.rand.nextBoolean()) temp.x = -temp.x;
        if (FastMath.rand.nextBoolean()) temp.y = -temp.y;
        if (FastMath.rand.nextBoolean()) temp.z = -temp.z;

        temp.multLocal(velocityStore.length());
        velocityStore.interpolateLocal(temp, magnitude);
        particleData.velocity.interpolateLocal(velocityStore, magnitude);
    }

    @Override
    public void initialize(@NotNull final ParticleData particleData) {
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
    }

    /**
     * Sets the chance the influencer has of successfully affecting the particle's velocity vector
     *
     * @param chance float
     */
    public void setChance(final float chance) {
        this.chance = chance;
    }

    /**
     * Returns the chance the influencer has of successfully affecting the particle's velocity
     * vector
     *
     * @return float
     */
    public float getChance() {
        return chance;
    }

    /**
     * Sets the magnitude at which the impulse will effect the particle's velocity vector
     *
     * @param magnitude float
     */
    public void setMagnitude(final float magnitude) {
        this.magnitude = magnitude;
    }

    /**
     * Returns  the magnitude at which the impulse will effect the particle's velocity vector
     *
     * @return float
     */
    public float getMagnitude() {
        return magnitude;
    }

    /**
     * Sets the strength of the full impulse
     *
     * @param strength float
     */
    public void setStrength(final float strength) {
        this.strength = strength;
    }

    /**
     * Returns the strength of the full impulse
     *
     * @return float
     */
    public float getStrength() {
        return strength;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(chance, "chance", 0.02f);
        oc.write(magnitude, "magnitude", 0.2f);
        oc.write(strength, "strength", 3f);
        oc.write(enabled, "enabled", true);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        chance = ic.readFloat("chance", 0.02f);
        magnitude = ic.readFloat("magnitude", 0.2f);
        strength = ic.readFloat("strength", 3f);
        enabled = ic.readBoolean("enabled", true);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        try {
            ImpulseInfluencer clone = (ImpulseInfluencer) super.clone();
            clone.setChance(chance);
            clone.setMagnitude(magnitude);
            clone.setStrength(strength);
            clone.setEnabled(enabled);
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
