package tonegod.emitter.influencers.impl;

import com.jme3.export.*;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.util.SafeArrayList;
import com.jme3.util.clone.Cloner;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.Messages;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.particle.ParticleData;
import tonegod.emitter.util.RandomUtils;

import java.io.IOException;
import java.util.Random;

/**
 * The implementation of the {@link ParticleInfluencer} to rotation particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public final class RotationInfluencer extends AbstractInterpolatedParticleInfluencer<RotationInfluencer.RotationInfluencerData> {

    protected static class RotationInfluencerData extends BaseInterpolationData {

        /**
         * The rotation angle speed per axis (in radians).
         */
        @NotNull
        public Vector3f speed;

        /**
         * The start rotation speed.
         */
        @NotNull
        public Vector3f startSpeed;

        /**
         * The end rotation speed.
         */
        @NotNull
        public Vector3f endSpeed;

        /**
         * The direction each axis' rotation will rotate in
         */
        public boolean rotateDirectionX;

        /**
         * The rotate direction Y.
         */
        public boolean rotateDirectionY;

        /**
         * The rotate direction Z.
         */
        public boolean rotateDirectionZ;

        private RotationInfluencerData() {
            this.speed = new Vector3f();
            this.startSpeed = new Vector3f();
            this.endSpeed = new Vector3f();
            this.rotateDirectionX = true;
            this.rotateDirectionY = true;
            this.rotateDirectionZ = true;
        }

        @Override
        public void cloneFields(@NotNull Cloner cloner, @NotNull Object original
        ) {
            super.cloneFields(cloner, original);

            speed = cloner.clone(speed);
            startSpeed = cloner.clone(startSpeed);
            endSpeed = cloner.clone(endSpeed);
        }
    }

    /**
     * The list of speeds.
     */
    @NotNull
    private SafeArrayList<Vector3f> speeds;

    /**
     * The speed factor.
     */
    @NotNull
    private final Vector3f speedFactor;

    /**
     * The store vector.
     */
    @NotNull
    private final Vector3f store;

    /**
     * The flag of using random direction.
     */
    private boolean randomDirection;

    /**
     * The flag of using random speed.
     */
    private boolean randomSpeed;

    /**
     * The direction.
     */
    private boolean direction;

    /**
     * The flag of using random start rotation for X.
     */
    private boolean randomStartRotationX;

    /**
     * The flag of using random start rotation for Y.
     */
    private boolean randomStartRotationY;

    /**
     * The flag of using random start rotation for Z.
     */
    private boolean randomStartRotationZ;

    public RotationInfluencer() {
        this.speeds = new SafeArrayList<>(Vector3f.class);
        this.speedFactor = Vector3f.ZERO.clone();
        this.store = new Vector3f();
        this.randomDirection = true;
        this.randomSpeed = true;
        this.direction = true;
    }

    @Override
    public @NotNull String getName() {
        return Messages.PARTICLE_INFLUENCER_ROTATION;
    }

    @Override
    public @NotNull RotationInfluencer.RotationInfluencerData newDataObject() {
        return new RotationInfluencerData();
    }

    @Override
    protected void updateImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull RotationInfluencer.RotationInfluencerData data,
            float tpf
    ) {

        Vector3f rotationSpeed = data.speed;

        if (speeds.size() > 1) {

            if (data.index >= speeds.size()) {
                data.index = 0;
            }

            data.interval += tpf;

            if (data.interval >= data.duration) {
                updateRotation(data);
            }

            Interpolation interpolation = data.interpolation;

            blend = interpolation.apply(data.interval / data.duration);

            Vector3f startSpeed = data.startSpeed;
            Vector3f endSpeed = data.endSpeed;

            rotationSpeed.interpolateLocal(startSpeed, endSpeed, blend);
        }

        particleData.angles.addLocal(rotationSpeed.mult(tpf, store));

        super.updateImpl(emitterNode, particleData, data, tpf);
    }

    /**
     * Update a rotation.
     *
     * @param data the influencer's data.
     */
    private void updateRotation(RotationInfluencerData data) {

        data.index++;

        if (!isCycle()) {
            if (data.index == speeds.size() - 1) {
                data.index = 0;
            }
        } else {
            if (data.index == speeds.size()) {
                data.index = 0;
            }
        }

        nextRotationSpeed(data, data.index, data.startSpeed);

        int index = data.index + 1;

        if (index == speeds.size()) {
            index = 0;
        }

        nextRotationSpeed(data, index, data.endSpeed);

        SafeArrayList<Interpolation> interpolations = getInterpolations();
        data.interpolation = interpolations.get(data.index);
        data.interval -= data.duration;
    }

    @Override
    protected void firstInitializeImpl(@NotNull ParticleData particleData) {

        if (speeds.isEmpty()) {
            addRotationSpeed(new Vector3f(0, 0, 10));
        }

        super.firstInitializeImpl(particleData);
    }

    @Override
    protected void initializeImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull RotationInfluencer.RotationInfluencerData data
    ) {

        data.index = 0;
        data.interval = 0f;
        data.duration = isCycle() ? getFixedDuration() : particleData.startLife / ((float) speeds.size() - 1);

        if (isRandomDirection()) {
            Random random = RandomUtils.getRandom();
            data.rotateDirectionX = random.nextBoolean();
            data.rotateDirectionY = random.nextBoolean();
            data.rotateDirectionZ = random.nextBoolean();
        }

        nextRotationSpeed(data, data.index, data.startSpeed);

        data.speed.set(data.startSpeed);

        if (speeds.size() > 1) {
            nextRotationSpeed(data, data.index + 1, data.endSpeed);
        }

        SafeArrayList<Interpolation> interpolations = getInterpolations();
        data.interpolation = interpolations.get(data.index);

        if (isRandomStartRotationX() || isRandomStartRotationY() || isRandomStartRotationZ()) {
            calculateRandomAngles(particleData);
        } else {
            particleData.angles.set(0, 0, 0);
        }

        super.initializeImpl(emitterNode, particleData, data);
    }

    /**
     * Calculates random angles.
     *
     * @param particleData the particle data.
     */
    private void calculateRandomAngles(@NotNull ParticleData particleData) {

        Random random = RandomUtils.getRandom();
        float x = randomStartRotationX ? random.nextFloat() * FastMath.TWO_PI : 0;
        float y = randomStartRotationY ? random.nextFloat() * FastMath.TWO_PI : 0;
        float z = randomStartRotationZ ? random.nextFloat() * FastMath.TWO_PI : 0;

        particleData.angles.set(x, y, z);
    }

    /**
     * Calculates a next rotation speed.
     *
     * @param data  the influencer's data.
     * @param index the index.
     * @param store the store vector.
     */
    private void nextRotationSpeed(
            @NotNull RotationInfluencerData data,
            int index,
            @NotNull Vector3f store
    ) {

        store.set(speeds.get(index));

        if (isRandomSpeed()) {
            Random random = RandomUtils.getRandom();
            store.set(random.nextFloat() * store.x,
                    random.nextFloat() * store.y,
                    random.nextFloat() * store.z);
        }

        if (isRandomDirection()) {
            store.x = data.rotateDirectionX ? store.x : -store.x;
            store.y = data.rotateDirectionY ? store.y : -store.y;
            store.z = data.rotateDirectionZ ? store.z : -store.z;
        }
    }

    @Override
    protected void resetImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull RotationInfluencer.RotationInfluencerData data
    ) {
        particleData.angles.set(0, 0, 0);
        super.resetImpl(emitterNode, particleData, data);
    }

    /**
     * Adds rotation speed.
     *
     * @param speed the new rotation speed.
     */
    public void addRotationSpeed(@NotNull Vector3f speed) {
        addRotationSpeed(speed, Interpolation.LINEAR);
    }

    /**
     * Adds rotation speed.
     *
     * @param speed         the new rotation speed.
     * @param interpolation the interpolation.
     */
    public void addRotationSpeed(@NotNull Vector3f speed, @NotNull Interpolation interpolation) {
        addInterpolation(interpolation);
        speeds.add(speed.clone());
    }

    /**
     * Removes rotation speed and interpolation for the index.
     *
     * @param index the index.
     */
    public void removeRotationSpeed(int index) {
        removeInterpolation(index);
        speeds.remove(index);
    }

    /**
     * Removes all rotation speeds.
     */
    public void removeAll() {
        clearInterpolations();
        this.speeds.clear();
    }

    /**
     * Gets rotation speeds.
     *
     * @return the list of rotations.
     */
    public @NotNull SafeArrayList<Vector3f> getRotationSpeeds() {
        return speeds;
    }

    /**
     * Gets rotation speed.
     *
     * @param index the index.
     * @return the rotation speed for the index.
     */
    public @NotNull Vector3f getRotationSpeed(int index) {
        return speeds.get(index);
    }

    /**
     * Changes a rotation speed for the index.
     *
     * @param rotationSpeed the new rotation speed.
     * @param index         the index.
     */
    public void updateRotationSpeed(@NotNull Vector3f rotationSpeed, int index) {
        speeds.set(index, rotationSpeed);
    }

    /**
     * Removes a last rotation speed and interpolation.
     */
    public void removeLast() {

        SafeArrayList<Vector3f> speeds = getRotationSpeeds();
        if (speeds.isEmpty()) {
            return;
        }

        int index = speeds.size() - 1;

        removeInterpolation(index);
        speeds.remove(index);
    }

    /**
     * Allows the influencer to choose a random rotation direction per axis as the particle is emitted.
     *
     * @param randomDirection the random direction
     */
    public void setRandomDirection(boolean randomDirection) {
        this.randomDirection = randomDirection;
    }

    /**
     * Returns true if the influencer currently selects a random rotation direction per axis as the particle is emitted.
     *
     * @return true if the influencer currently selects a random rotation direction per axis as the particle is emitted.
     */
    public boolean isRandomDirection() {
        return randomDirection;
    }

    /**
     * Allows the influencer to select a random rotation speed from 0 to the provided maximum speeds per axis
     *
     * @param randomSpeed true if need to select a random rotation speed.
     */
    public void setRandomSpeed(boolean randomSpeed) {
        this.randomSpeed = randomSpeed;
    }

    /**
     * Returns true if the influencer currently to selects random rotation speeds per axis.
     *
     * @return true if the influencer currently to selects random rotation speeds per axis.
     */
    public boolean isRandomSpeed() {
        return randomSpeed;
    }

    /**
     * Sets random start rotation X.
     *
     * @param randomStartRotationX true if the influencer uses random start rotation for X.
     */
    public void setRandomStartRotationX(boolean randomStartRotationX) {
        this.randomStartRotationX = randomStartRotationX;
    }

    /**
     * Sets random start rotation Y.
     *
     * @param randomStartRotationY true if the influencer uses random start rotation for Y.
     */
    public void setRandomStartRotationY(boolean randomStartRotationY) {
        this.randomStartRotationY = randomStartRotationY;
    }

    /**
     * Sets random start rotation Z.
     *
     * @param randomStartRotationZ true if the influencer uses random start rotation for Z.
     */
    public void setRandomStartRotationZ(boolean randomStartRotationZ) {
        this.randomStartRotationZ = randomStartRotationZ;
    }

    /**
     * Returns true if this influencer uses random start rotation X.
     *
     * @return true if this influencer uses random start rotation X.
     */
    public boolean isRandomStartRotationX() {
        return randomStartRotationX;
    }

    /**
     * Returns true if this influencer uses random start rotation Y.
     *
     * @return true if this influencer uses random start rotation Y.
     */
    public boolean isRandomStartRotationY() {
        return randomStartRotationY;
    }


    /**
     * Returns true if this influencer uses random start rotation Z.
     *
     * @return true if this influencer uses random start rotation Z.
     */
    public boolean isRandomStartRotationZ() {
        return randomStartRotationZ;
    }

    /**
     * Forces the rotation direction to always remain constant per particle.
     *
     * @param direction boolean
     */
    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    /**
     * Returns true if the rotation direction will always remain constant per particle.
     *
     * @return true if the rotation direction will always remain constant per particle.
     */
    public boolean isDirection() {
        return direction;
    }

    @Override
    public void write(@NotNull JmeExporter exporter) throws IOException {
        super.write(exporter);

        OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(speeds.toArray(new Vector3f[speeds.size()]), "speeds", null);
        capsule.write(speedFactor, "speedFactor", Vector3f.ZERO);
        capsule.write(randomDirection, "randomDirection", true);
        capsule.write(randomSpeed, "randomSpeed", true);
        capsule.write(direction, "direction", true);
        capsule.write(randomStartRotationX, "randomStartRotationX", false);
        capsule.write(randomStartRotationY, "randomStartRotationY", false);
        capsule.write(randomStartRotationZ, "randomStartRotationZ", false);
    }

    @Override
    public void read(@NotNull JmeImporter importer) throws IOException {
        super.read(importer);

        InputCapsule capsule = importer.getCapsule(this);
        Savable[] readSpeeds = capsule.readSavableArray("speeds", null);

        if (readSpeeds != null) {
            for (Savable speed : readSpeeds) {
                speeds.add((Vector3f) speed);
            }
        }

        speedFactor.set((Vector3f) capsule.readSavable("speedFactor", Vector3f.ZERO.clone()));
        randomDirection = capsule.readBoolean("randomDirection", true);
        randomSpeed = capsule.readBoolean("randomSpeed", true);
        direction = capsule.readBoolean("direction", true);
        randomStartRotationX = capsule.readBoolean("randomStartRotationX", false);
        randomStartRotationY = capsule.readBoolean("randomStartRotationY", false);
        randomStartRotationZ = capsule.readBoolean("randomStartRotationZ", false);
    }

    @Override
    public @NotNull ParticleInfluencer clone() {
        RotationInfluencer clone = (RotationInfluencer) super.clone();
        clone.speeds = new SafeArrayList<>(Vector3f.class);
        clone.speeds.addAll(speeds);
        clone.setDirection(direction);
        clone.setRandomDirection(randomDirection);
        clone.setRandomSpeed(randomSpeed);
        clone.randomStartRotationX = randomStartRotationX;
        clone.randomStartRotationY = randomStartRotationY;
        clone.randomStartRotationZ = randomStartRotationZ;
        return clone;
    }
}
