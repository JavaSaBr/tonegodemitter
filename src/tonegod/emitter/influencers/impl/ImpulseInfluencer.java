package tonegod.emitter.influencers.impl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Random;

import tonegod.emitter.Messages;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.particle.ParticleData;
import tonegod.emitter.util.RandomUtils;

/**
 * The implementation of the {@link ParticleInfluencer} for impulse influence to particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class ImpulseInfluencer extends AbstractParticleInfluencer {

    /**
     * The temp vector.
     */
    @NotNull
    private final transient Vector3f temp;

    /**
     * The temp velocity store.
     */
    @NotNull
    private final transient Vector3f velocityStore;

    /**
     * The chance.
     */
    private float chance;

    /**
     * Thr magnitude.
     */
    private float magnitude;

    /**
     * The strength.
     */
    private float strength;

    public ImpulseInfluencer() {
        this.temp = new Vector3f();
        this.velocityStore = new Vector3f();
        this.chance = 0.02f;
        this.magnitude = 0.2f;
        this.strength = 3;
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.PARTICLE_INFLUENCER_IMPULSE;
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {

        final Random random = RandomUtils.getRandom();
        if (random.nextFloat() <= 1 - (chance + tpf)) return;

        velocityStore.set(particleData.velocity);

        temp.set(random.nextFloat() * strength,
                random.nextFloat() * strength,
                random.nextFloat() * strength);

        if (random.nextBoolean()) temp.x = -temp.x;
        if (random.nextBoolean()) temp.y = -temp.y;
        if (random.nextBoolean()) temp.z = -temp.z;

        temp.multLocal(velocityStore.length());
        velocityStore.interpolateLocal(temp, magnitude);

        particleData.velocity.interpolateLocal(velocityStore, magnitude);

        super.updateImpl(particleData, tpf);
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
     * Returns the chance the influencer has of successfully affecting the particle's velocity vector
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
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(chance, "chance", 0.02f);
        capsule.write(magnitude, "magnitude", 0.2f);
        capsule.write(strength, "strength", 3f);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        final InputCapsule capsule = importer.getCapsule(this);
        chance = capsule.readFloat("chance", 0.02f);
        magnitude = capsule.readFloat("magnitude", 0.2f);
        strength = capsule.readFloat("strength", 3f);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        final ImpulseInfluencer clone = (ImpulseInfluencer) super.clone();
        clone.setChance(chance);
        clone.setMagnitude(magnitude);
        clone.setStrength(strength);
        return clone;
    }
}
