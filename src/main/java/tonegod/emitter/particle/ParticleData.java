package tonegod.emitter.particle;

import static java.util.Objects.requireNonNull;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.util.SafeArrayList;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.EmitterMesh;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;

import java.util.HashMap;
import java.util.Map;

/**
 * The particle data class.
 *
 * @author t0neg0d, JavaSaBr
 */
public final class ParticleData implements Cloneable, JmeCloneable {

    public interface DataFactory<T> {
        @NotNull T create(@NotNull String name);
    }

    /**
     * The data map.
     */
    @NotNull
    private final Map<String, Object> data;

    /**
     * The color.
     */
    @NotNull
    public final ColorRGBA color;

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
    @NotNull
    public Interpolation alphaInterpolation;

    /** SIZE INFLUENCER */

    /**
     * The size.
     */
    @NotNull
    public final Vector3f size;

    /**
     * The start size.
     */
    @NotNull
    public final Vector3f startSize;

    /**
     * The end size.
     */
    @NotNull
    public final Vector3f endSize;

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
    @NotNull
    public Interpolation sizeInterpolation;

    /**
     * DESTINATION INFLUENCER
     */

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
    @NotNull
    public Interpolation destinationInterpolation;

    /** ROTATION INFLUENCER */

    /**
     * The rotation angle speed per axis (in radians).
     */
    @NotNull
    public final Vector3f rotationSpeed;

    /**
     * The start rotation speed.
     */
    @NotNull
    public final Vector3f startRotationSpeed;

    /**
     * The end rotation speed.
     */
    @NotNull
    public final Vector3f endRotationSpeed;

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
    @NotNull
    public Interpolation rotationInterpolation;

    /**
     * The direction each axis' rotation will rotate in
     */
    public boolean rotateDirectionX;
    /**
     * The Rotate direction y.
     */
    public boolean rotateDirectionY;
    /**
     * The Rotate direction z.
     */
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
     * The particle emitter node.
     */
    @Nullable
    public ParticleEmitterNode emitterNode;

    /**
     * The Initial position.
     */
    @NotNull
    public final Vector3f initialPosition;

    /**
     * The Random offset.
     */
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
    @NotNull
    public final Vector3f angles;

    /**
     * The UP vector.
     */
    @NotNull
    public final Vector3f upVec;

    /**
     * The temp vector.
     */
    @NotNull
    public final Vector3f tempV3;

    /**
     * The force at which the particle was emitted
     */
    public float force;
    /**
     * The Tangent force.
     */
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
        this.data = new HashMap<>(10, 0.4F);
        this.color = new ColorRGBA(1, 1, 1, 1);
        this.velocity = new Vector3f();
        this.reverseVelocity = new Vector3f();
        this.position = new Vector3f();

        this.alpha = 1;
        this.alphaDuration = 1;
        this.alphaInterpolation = Interpolation.LINEAR;

        this.initialPosition = new Vector3f();
        this.randomOffset = new Vector3f();

        this.size = new Vector3f(1f, 1f, 1f);
        this.startSize = new Vector3f(1, 1, 1);
        this.endSize = new Vector3f(0, 0, 0);
        this.sizeDuration = 1;
        this.sizeInterpolation = Interpolation.LINEAR;

        this.destinationDuration = 1;
        this.destinationInterpolation = Interpolation.LINEAR;

        this.rotationSpeed = new Vector3f();
        this.startRotationSpeed = new Vector3f();
        this.endRotationSpeed = new Vector3f();
        this.rotationDuration = 1;
        this.rotateDirectionX = true;
        this.rotateDirectionY = true;
        this.rotateDirectionZ = true;
        this.rotationInterpolation = Interpolation.LINEAR;

        this.angles = new Vector3f();

        this.spriteDuration = 1;

        this.upVec = new Vector3f(0, 1, 0);
        this.tempV3 = new Vector3f();
    }

    /**
     * Set the data be the name.
     *
     * @param name the data's name.
     * @param data the data.
     */
    public void setData(@NotNull final String name, @NotNull final Object data) {
        this.data.put(name, data);
    }

    /**
     * Get data by the name.
     *
     * @param name the data's name.
     * @param <T>  the data's type.
     * @return the saved data or null.
     */
    public @Nullable <T> T getData(@NotNull final String name) {
        return (T) this.data.get(name);
    }

    /**
     * Get or create data by the name.
     *
     * @param name    the data's name.
     * @param factory the data factory.
     * @param <T>     the data's type.
     * @return the saved data or created data.
     */
    public @NotNull <T> T getData(@NotNull final String name, @NotNull final DataFactory<T> factory) {

        Object result = data.get(name);
        if (result == null) {
            result = factory.create(name);
            data.put(name, result);
        }

        return (T) result;
    }

    /**
     * Removed data by the name.
     *
     * @param name the data's name.
     */
    public void removeData(@NotNull final String name) {
        this.data.remove(name);
    }

    @Override
    public @NotNull ParticleData clone() throws CloneNotSupportedException {
        return (ParticleData) super.clone();
    }

    /**
     * Get the emitter node.
     *
     * @return the particle emitter node.
     */
    public @NotNull ParticleEmitterNode getEmitterNode() {
        return requireNonNull(emitterNode);
    }

    /**
     * Set the emitter node.
     *
     * @param emitterNode the emitter node.
     */
    public void setEmitterNode(@NotNull final ParticleEmitterNode emitterNode) {
        this.emitterNode = emitterNode;
    }

    /**
     * Update state of this particle.
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

        final SafeArrayList<ParticleInfluencer> influencers = emitterNode.getInfluencers();
        for (final ParticleInfluencer influencer : influencers.getArray()) {
            influencer.update(this, tpf);
        }

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
     * Get the initial length.
     *
     * @return the initial length.
     */
    public float getInitialLength() {
        return initialLength;
    }

    /**
     * Called once per particle use when the particle is emitted.
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

        final SafeArrayList<ParticleInfluencer> influencers = emitterNode.getInfluencers();
        for (final ParticleInfluencer influencer : influencers.getArray()) {
            influencer.initialize(this);
        }

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
     * Get the size.
     *
     * @return the size.
     */
    public @NotNull Vector3f getSize() {
        return size;
    }

    /**
     * Get the angles.
     *
     * @return the angles.
     */
    public @NotNull Vector3f getAngles() {
        return angles;
    }

    /**
     * Get the velocity.
     *
     * @return the velocity.
     */
    public @NotNull Vector3f getVelocity() {
        return velocity;
    }

    /**
     * Get the reverse velocity.
     *
     * @return the reverse velocity.
     */
    public @NotNull Vector3f getReverseVelocity() {
        return reverseVelocity;
    }

    /**
     * Get the position.
     *
     * @return the position.
     */
    public @NotNull Vector3f getPosition() {
        return position;
    }

    /**
     * Get the random offset.
     *
     * @return the random offset.
     */
    public @NotNull Vector3f getRandomOffset() {
        return randomOffset;
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

        final SafeArrayList<ParticleInfluencer> influencers = emitterNode.getInfluencers();
        for (final ParticleInfluencer influencer : influencers.getArray()) {
            influencer.reset(this);
        }

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
     * Is active boolean.
     *
     * @return true if this particle is active.
     */
    public boolean isActive() {
        return active;
    }
}