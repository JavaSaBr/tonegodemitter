package tonegod.emitter.particle;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import rlib.util.array.Array;
import tonegod.emitter.EmitterMesh;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;

/**
 * @author t0neg0d
 */
public final class ParticleData implements Cloneable, JmeCloneable {

    /** COLOR INFLUENCER */

    /**
     * The color.
     */
    public final ColorRGBA color;

    /**
     * The color index.
     */
    public int colorIndex;

    /**
     * The color interval.
     */
    public float colorInterval;

    /**
     * The duration.
     */
    public float colorDuration;

    /**
     * The color interpolation.
     */
    public Interpolation colorInterpolation;

    /** ALPHA INFLUENCER */

    /**
     * The alpha.
     */
    public float alpha;

    /**
     * The alpha interval.
     */
    public float alphaInterval;

    /**
     * The alpha duration.
     */
    public float alphaDuration;

    /**
     * The alpha index.
     */
    public int alphaIndex;

    /**
     * The alpha interpolation.
     */
    public Interpolation alphaInterpolation;

    /** SIZE INFLUENCER */

    /**
     * The size.
     */
    public Vector3f size;

    /**
     * THe start size.
     */
    public Vector3f startSize;

    /**
     * The end size.
     */
    public Vector3f endSize;

    /**
     * The size index.
     */
    public int sizeIndex;

    /**
     * The size interval.
     */
    public float sizeInterval;

    /**
     * The size duration.
     */
    public float sizeDuration;

    /**
     * The size interpolation.
     */
    public Interpolation sizeInterpolation;

    /**
     * DESTINATION INFLUENCER
     */

    public Vector3f previousPosition;

    /**
     * The destination index.
     */
    public int destinationIndex;

    /**
     * The destination interval.
     */
    public float destinationInterval;

    /**
     * The destination duration.
     */
    public float destinationDuration;

    /**
     * The destination interpolation.
     */
    public Interpolation destinationInterpolation;

    /** DIRECTION INFLUENCER */

    /**
     * The direction index.
     */
    public int directionIndex;

    /**
     * The direction interval.
     */
    public float directionInterval;

    /**
     * The direction duration.
     */
    public float directionDuration;

    /**
     * The direction interpolation.
     */
    public Interpolation directionInterpolation;

    /** ROTATION INFLUENCER */

    /**
     * The rotation angle speed per axis (in radians).
     */
    public Vector3f rotationSpeed;

    /**
     * The start rotation speed.
     */
    public Vector3f startRotationSpeed;

    /**
     * The end rotation speed.
     */
    public Vector3f endRotationSpeed;

    /**
     * The rotation index.
     */
    public int rotationIndex;

    /**
     * The rotation interval.
     */
    public float rotationInterval;

    /**
     * The rotation duration.
     */
    public float rotationDuration;

    /**
     * The rotation interpolation.
     */
    public Interpolation rotationInterpolation;

    /**
     * The direction each axis' rotation will rotate in
     */
    public boolean rotateDirectionX;
    public boolean rotateDirectionY;
    public boolean rotateDirectionZ;

    /** SPRITE INFLUENCER */

    /**
     * The sprite columns.
     */
    public int spriteCol;

    /**
     * The sprite rows.
     */
    public int spriteRow;

    /**
     * The sprite index.
     */
    public int spriteIndex;

    /**
     * The sprite interval.
     */
    public float spriteInterval;

    /**
     * The sprite duration.
     */
    public float spriteDuration;

    /**
     * THe sprite interpolation.
     */
    public Interpolation spriteInterpolation;

    /** PHYSICS INFLUENCER */

    /**
     * The collision flag.
     */
    public boolean collision;

    /**
     * The collision interval.
     */
    public float collisionInterval;

    /** PARTICLE DATA */

    /**
     * A strage facility for per-particle data used by influencers
     */
    public Map<String, Object> data;

    /**
     * The parent particle emitter
     */
    @Nullable
    public ParticleEmitterNode emitterNode;

    /**
     * The position of the emitter when the particle was released.
     */
    @NotNull
    public final Vector3f emitterPosition;

    @NotNull
    public final Vector3f initialPosition;

    @NotNull
    public final Vector3f randomOffset;

    /**
     * The velocity.
     */
    @NotNull
    public final Vector3f velocity;

    /**
     * The reverse velocity.
     */
    @NotNull
    public final Vector3f reverseVelocity;

    /**
     * THe current particle position.
     */
    @NotNull
    public final Vector3f position;

    /**
     * ParticleData rotation angle per axis (in radians).
     */
    public Vector3f angles;

    /**
     * The UP vector.
     */
    public Vector3f upVec;

    /**
     * The temp vector.
     */
    public Vector3f tempV3;

    /**
     * The force at which the particle was emitted
     */
    public float force;
    public float tangentForce;

    /**
     * The life, in seconds.
     */
    public float life;

    /**
     * The total particle lifespan.
     */
    public float startlife;

    /**
     * The current blend value.
     */
    public float blend;

    /**
     * The interpolated blend value.
     */
    public float interpBlend;

    /**
     * The index of the emitter shape's mesh triangle the particle was emitted from
     */
    public int triangleIndex;

    /**
     * The initial length.
     */
    public float initialLength;

    /**
     * The particles index
     */
    public int index;

    /**
     * The state of the particle
     */
    public boolean active;

    public ParticleData() {
        velocity = new Vector3f();
        reverseVelocity = new Vector3f();
        position = new Vector3f();

        color = new ColorRGBA(1, 1, 1, 1);
        colorDuration = 1f;

        alpha = 1;
        alphaDuration = 1;

        emitterPosition = new Vector3f();
        initialPosition = new Vector3f();
        randomOffset = new Vector3f();

        size = new Vector3f(1f, 1f, 1f);
        startSize = new Vector3f(1, 1, 1);
        endSize = new Vector3f(0, 0, 0);
        sizeDuration = 1;

        previousPosition = new Vector3f();
        destinationDuration = 1;

        directionDuration = 1;

        rotationSpeed = new Vector3f();
        startRotationSpeed = new Vector3f();
        endRotationSpeed = new Vector3f();
        rotationDuration = 1;
        rotateDirectionX = true;
        rotateDirectionY = true;
        rotateDirectionZ = true;

        angles = new Vector3f();

        spriteDuration = 1;

        upVec = new Vector3f(0, 1, 0);
        tempV3 = new Vector3f();

        data = new HashMap<>();
    }

    /**
     * Sets data to store with the particle
     *
     * @param key  The data's map key
     * @param data The data
     */
    public void setData(@NotNull final String key, @Nullable final Object data) {
        this.data.put(key, data);
    }

    /**
     * Returns the stored per-particle data
     *
     * @param key The data's map key
     * @return The data
     */
    @Nullable
    public Object getData(final String key) {
        return data.get(key);
    }

    @NotNull
    @Override
    public ParticleData clone() throws CloneNotSupportedException {
        return (ParticleData) super.clone();
    }

    /**
     * @return the parent particle emitter.
     */
    @NotNull
    public ParticleEmitterNode getEmitterNode() {
        return Objects.requireNonNull(emitterNode);
    }

    /**
     * @param emitterNode the parent particle emitter.
     */
    public void setEmitterNode(@NotNull final ParticleEmitterNode emitterNode) {
        this.emitterNode = emitterNode;
    }

    /**
     * Update a state of this particle.
     *
     * @param tpf the time per frame.
     */
    public void update(final float tpf) {

        final ParticleEmitterNode emitterNode = getEmitterNode();

        if (!emitterNode.isStaticParticles()) {
            life -= tpf;

            if (life <= 0) {
                reset();
                return;
            }

            final Interpolation interpolation = emitterNode.getInterpolation();

            blend = 1.0f * (startlife - life) / startlife;
            interpBlend = interpolation.apply(blend);
        }

        final Array<ParticleInfluencer> influencers = emitterNode.getInfluencers();
        influencers.forEach(tpf, this, (influencer, frames, node) -> influencer.update(node, frames));

        tempV3.set(velocity).multLocal(tpf);
        position.addLocal(tempV3);

        // TODO: Test this!
        if (emitterNode.isStaticParticles()) {

            final EmitterMesh emitterShape = emitterNode.getEmitterShape();
            emitterShape.setNext(triangleIndex);

            if (emitterNode.isRandomEmissionPoint()) {
                position.set(emitterShape.getNextTranslation().addLocal(randomOffset));
            } else {
                position.set(emitterShape.getNextTranslation());
            }
        }
    }

    /**
     * Called once per particle use when the particle is emitted
     */
    public void initialize() {

        final ParticleEmitterNode emitterNode = getEmitterNode();
        emitterNode.incActiveParticleCount();

        final float lifeMin = emitterNode.getLifeMin();
        final float lifeMax = emitterNode.getLifeMax();

        active = true;
        blend = 0;
        size.set(0, 0, 0);

        if (lifeMin != lifeMax) {
            startlife = (lifeMax - lifeMin) * FastMath.nextRandomFloat() + lifeMin;
        } else {
            startlife = lifeMax;
        }

        life = startlife;

        final float forceMin = emitterNode.getForceMin();
        final float forceMax = emitterNode.getForceMax();

        if (forceMin != forceMax) {
            force = (forceMax - forceMin) * FastMath.nextRandomFloat() + forceMin;
        } else {
            force = forceMax;
        }

        final EmitterMesh emitterShape = emitterNode.getEmitterShape();
        emitterShape.setNext();

        triangleIndex = emitterShape.getTriangleIndex();

        if (!emitterNode.isRandomEmissionPoint()) {
            position.set(emitterShape.getNextTranslation());
        } else {
            randomOffset.set(emitterShape.getRandomTranslation());
            position.set(emitterShape.getNextTranslation().add(randomOffset));
        }

        velocity.set(emitterShape.getNextDirection())
                .normalizeLocal()
                .multLocal(force);

        initialLength = velocity.length();
        initialPosition.set(emitterNode.getWorldTranslation());

        final Array<ParticleInfluencer> influencers = emitterNode.getInfluencers();
        influencers.forEach(this, ParticleInfluencer::initialize);

        switch (emitterNode.getEmissionPoint()) {
            case EDGE_BOTTOM: {
                tempV3.set(emitterShape.getNextDirection()).normalizeLocal();
                tempV3.multLocal(startSize.getY());
                position.addLocal(tempV3);
                break;
            }
            case EDGE_TOP: {
                tempV3.set(emitterShape.getNextDirection()).normalizeLocal();
                tempV3.multLocal(startSize.getY());
                position.subtractLocal(tempV3);
                break;
            }
        }
    }

    /**
     * @return the velocity.
     */
    @NotNull
    public Vector3f getVelocity() {
        return velocity;
    }

    /**
     * Called once per particle use when the particle finishes it's life cycle
     */
    public void reset() {
        active = false;

        final ParticleEmitterNode emitterNode = getEmitterNode();

        if (emitterNode.getActiveParticleCount() > 0) {
            emitterNode.decActiveParticleCount();
        }

        final Array<ParticleInfluencer> influencers = emitterNode.getInfluencers();
        influencers.forEach(this, ParticleInfluencer::reset);

        emitterNode.setNextIndex(index);
    }

    @Override
    public Object jmeClone() {
        try {
            return super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cloneFields(final Cloner cloner, final Object original) {
    }

    /**
     * @return true if this particle is active.
     */
    public boolean isActive() {
        return active;
    }
}