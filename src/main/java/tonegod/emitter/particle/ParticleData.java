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

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The particle objectData class.
 *
 * @author t0neg0d, JavaSaBr
 */
public final class ParticleData implements Cloneable, JmeCloneable {

    @NotNull
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    @NotNull
    private static final int[] EMPTY_INT_ARRAY = new int[0];

    @NotNull
    private static final float[] EMPTY_FLOAT_ARRAY = new float[0];

    /**
     * The object objectData id factory.
     */
    @NotNull
    private static final AtomicInteger OBJECT_DATA_ID_FACTORY = new AtomicInteger(0);

    /**
     * The int objectData id factory.
     */
    @NotNull
    private static final AtomicInteger INT_DATA_ID_FACTORY = new AtomicInteger(0);

    /**
     * The float objectData id factory.
     */
    @NotNull
    private static final AtomicInteger FLOAT_DATA_ID_FACTORY = new AtomicInteger(0);

    /**
     * The current max object objectData id.
     */
    private static int currentObjectMaxDataId = -1;

    /**
     * The current max int objectData id.
     */
    private static int currentIntMaxDataId = -1;

    /**
     * The current max float objectData id.
     */
    private static int currentFloatMaxDataId = -1;

    /**
     * Reserve the new object objectData id.
     *
     * @return the new object objectData id.
     */
    public static int reserveObjectDataId() {
        return OBJECT_DATA_ID_FACTORY.incrementAndGet();
    }

    /**
     * Reserve the new int objectData id.
     *
     * @return the new int objectData id.
     */
    public static int reserveIntDataId() {
        return INT_DATA_ID_FACTORY.incrementAndGet();
    }

    /**
     * Reserve the new float objectData id.
     *
     * @return the new float objectData id.
     */
    public static int reserveFloatDataId() {
        return FLOAT_DATA_ID_FACTORY.incrementAndGet();
    }

    /**
     * Get the current max object objectData id.
     *
     * @return the current max object objectData id.
     */
    private static int getCurrentMaxObjectDataId() {
        if (currentObjectMaxDataId == -1) {
            synchronized (ParticleData.class) {
                if (currentObjectMaxDataId == -1) {
                    final int lastReservedId = OBJECT_DATA_ID_FACTORY.get();
                    ParticleData.currentObjectMaxDataId = lastReservedId;
                    return lastReservedId;
                }
            }
        }
        return currentObjectMaxDataId;
    }

    /**
     * Get the current max int objectData id.
     *
     * @return the current max int objectData id.
     */
    private static int getCurrentMaxIntDataId() {
        if (currentIntMaxDataId == -1) {
            synchronized (ParticleData.class) {
                if (currentIntMaxDataId == -1) {
                    final int lastReservedId = INT_DATA_ID_FACTORY.get();
                    ParticleData.currentIntMaxDataId = lastReservedId;
                    return lastReservedId;
                }
            }
        }
        return currentIntMaxDataId;
    }

    /**
     * Get the current max float objectData id.
     *
     * @return the current max float objectData id.
     */
    private static int getCurrentMaxFloatDataId() {
        if (currentFloatMaxDataId == -1) {
            synchronized (ParticleData.class) {
                if (currentFloatMaxDataId == -1) {
                    final int lastReservedId = FLOAT_DATA_ID_FACTORY.get();
                    ParticleData.currentFloatMaxDataId = lastReservedId;
                    return lastReservedId;
                }
            }
        }
        return currentFloatMaxDataId;
    }

    /**
     * The color.
     */
    @NotNull
    public final ColorRGBA color;

    /**
     * The object data map.
     */
    @NotNull
    private Object[] objectData;

    /**
     * The int data map.
     */
    @NotNull
    private int[] intData;

    /**
     * The float data map.
     */
    @NotNull
    private float[] floatData;

    /**
     * The alpha.
     */
    public float alpha;


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
        this.objectData = EMPTY_OBJECT_ARRAY;
        this.intData = EMPTY_INT_ARRAY;
        this.floatData = EMPTY_FLOAT_ARRAY;
        this.color = new ColorRGBA(1, 1, 1, 1);
        this.velocity = new Vector3f();
        this.reverseVelocity = new Vector3f();
        this.position = new Vector3f();

        this.alpha = 1;

        this.initialPosition = new Vector3f();
        this.randomOffset = new Vector3f();

        this.size = new Vector3f(1f, 1f, 1f);
        this.startSize = new Vector3f(1, 1, 1);
        this.endSize = new Vector3f(0, 0, 0);
        this.sizeDuration = 1;
        this.sizeInterpolation = Interpolation.LINEAR;

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
     * Reserve an object slot for the data id.
     *
     * @param dataId the data id.
     */
    public void reserveObjectData(final int dataId) {
        if (objectData == EMPTY_OBJECT_ARRAY) {
            objectData = new Object[Math.max(getCurrentMaxObjectDataId(), dataId + 1)];
        } else if (dataId >= objectData.length) {
            objectData = Arrays.copyOf(objectData, dataId + 1);
        }
    }

    /**
     * Reverse a slot for an object data and create the data if it doesn't exists.
     *
     * @param dataId  the data id.
     * @param factory the data factory.
     */
    public void initializeObjectData(final int dataId, @NotNull final Callable<?> factory) {
        reserveObjectData(dataId);

        if (!hasObjectData(dataId)) {
            try {
                setObjectData(dataId, factory.call());
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Reserve an int slot for the data id.
     *
     * @param dataId the data id.
     */
    public void reserveIntData(final int dataId) {
        if (intData == EMPTY_INT_ARRAY) {
            intData = new int[Math.max(getCurrentMaxIntDataId(), dataId + 1)];
        } else if (dataId >= objectData.length) {
            intData = Arrays.copyOf(intData, dataId + 1);
        }
    }

    /**
     * Reverse a slot for an int data and set the initialized data.
     *
     * @param dataId the data id.
     * @param data   the initialized data.
     */
    public void initializIntData(final int dataId, final int data) {
        reserveIntData(dataId);
        setIntData(dataId, data);
    }

    /**
     * Reserve a float slot for the data id.
     *
     * @param dataId the data id.
     */
    public void reserveFloatData(final int dataId) {
        if (floatData == EMPTY_FLOAT_ARRAY) {
            floatData = new float[Math.max(getCurrentMaxFloatDataId(), dataId + 1)];
        } else if (dataId >= objectData.length) {
            floatData = Arrays.copyOf(floatData, dataId + 1);
        }
    }

    /**
     * Reverse a slot for an float data and set the initialized data.
     *
     * @param dataId the data id.
     * @param data   the initialized data.
     */
    public void initializFloatData(final int dataId, final float data) {
        reserveFloatData(dataId);
        setFloatData(dataId, data);
    }

    /**
     * Set the object data by the data id.
     *
     * @param dataId the data id.
     * @param data   the object data.
     */
    public void setObjectData(final int dataId, @NotNull final Object data) {
        this.objectData[dataId] = data;
    }

    /**
     * Set the int data by the data id.
     *
     * @param dataId the data id.
     * @param data   the int data.
     */
    public void setIntData(final int dataId, final int data) {
        this.intData[dataId] = data;
    }

    /**
     * Set the float data by the data id.
     *
     * @param dataId the data id.
     * @param data   the float data.
     */
    public void setFloatData(final int dataId, final float data) {
        this.floatData[dataId] = data;
    }

    /**
     * Return true if data be the data id is exist.
     *
     * @param dataId the data id.
     * @return true if data be the data id is exist.
     */
    public boolean hasObjectData(final int dataId) {
        return objectData[dataId] != null;
    }

    /**
     * Get object data by the data id.
     *
     * @param dataId the data id.
     * @param <T>    the object data's type.
     * @return the saved object data or null.
     */
    public @NotNull <T> T getObjectData(final int dataId) {
        return (T) objectData[dataId];
    }

    /**
     * Get int data by the data id.
     *
     * @param dataId the data id.
     * @return the saved int data or 0.
     */
    public int getIntData(final int dataId) {
        return intData[dataId];
    }

    /**
     * Get float data by the data id.
     *
     * @param dataId the data id.
     * @return the float object data or -.
     */
    public float getFloatData(final int dataId) {
        return floatData[dataId];
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