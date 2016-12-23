package tonegod.emitter.influencers;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import rlib.util.ArrayUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.array.UnsafeArray;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.interpolation.InterpolationManager;
import tonegod.emitter.particle.ParticleData;

/**
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class RotationInfluencer implements ParticleInfluencer {

    /**
     * The list of speeds.
     */
    private final UnsafeArray<Vector3f> speeds;

    /**
     * The list of interpolations.
     */
    private final UnsafeArray<Interpolation> interpolations;

    /**
     * The speed factor.
     */
    private final Vector3f speedFactor;

    private float blend;
    private float fixedDuration;

    private boolean randomDirection;
    private boolean randomSpeed;
    private boolean direction;
    private boolean initialized;
    private boolean enabled;
    private boolean cycle;

    private boolean randomStartRotationX;
    private boolean randomStartRotationY;
    private boolean randomStartRotationZ;

    public RotationInfluencer() {
        this.speeds = ArrayFactory.newUnsafeArray(Vector3f.class);
        this.interpolations = ArrayFactory.newUnsafeArray(Interpolation.class);
        this.speedFactor = Vector3f.ZERO.clone();
        this.fixedDuration = 0f;
        this.randomDirection = true;
        this.randomSpeed = true;
        this.direction = true;
        this.enabled = true;
    }

    @NotNull
    @Override
    public String getName() {
        return "Rotation influencer";
    }

    @Override
    public void update(@NotNull final ParticleData particleData, final float tpf) {
        if (!enabled) return;

        if (speeds.size() > 1) {

            particleData.rotationInterval += tpf;

            if (particleData.rotationInterval >= particleData.rotationDuration) {
                updateRotation(particleData);
            }

            blend = particleData.rotationInterpolation.apply(particleData.rotationInterval / particleData.rotationDuration);

            particleData.rotationSpeed.interpolateLocal(particleData.startRotationSpeed, particleData.endRotationSpeed, blend);
        }

        particleData.angles.addLocal(particleData.rotationSpeed.mult(tpf));
    }

    private void updateRotation(@NotNull final ParticleData particleData) {
        particleData.rotationIndex++;

        if (!cycle) {
            if (particleData.rotationIndex == speeds.size() - 1) {
                particleData.rotationIndex = 0;
            }
        } else {
            if (particleData.rotationIndex == speeds.size()) {
                particleData.rotationIndex = 0;
            }
        }

        getRotationSpeed(particleData, particleData.rotationIndex, particleData.startRotationSpeed);

        int index = particleData.rotationIndex + 1;

        if (index == speeds.size()) {
            index = 0;
        }

        getRotationSpeed(particleData, index, particleData.endRotationSpeed);

        particleData.rotationInterpolation = interpolations.get(particleData.rotationIndex);
        particleData.rotationInterval -= particleData.rotationDuration;
    }

    @Override
    public void initialize(@NotNull final ParticleData particleData) {

        if (!initialized) {
            if (speeds.isEmpty()) {
                addRotationSpeed(new Vector3f(0, 0, 10));
            }
            initialized = true;
        }

        particleData.rotationIndex = 0;
        particleData.rotationInterval = 0f;
        particleData.rotationDuration = (cycle) ? fixedDuration : particleData.startlife / ((float) speeds.size() - 1);

        if (randomDirection) {
            particleData.rotateDirectionX = FastMath.rand.nextBoolean();
            particleData.rotateDirectionY = FastMath.rand.nextBoolean();
            particleData.rotateDirectionZ = FastMath.rand.nextBoolean();
        }

        getRotationSpeed(particleData, particleData.rotationIndex, particleData.startRotationSpeed);

        particleData.rotationSpeed.set(particleData.startRotationSpeed);

        if (speeds.size() > 1) {
            getRotationSpeed(particleData, particleData.rotationIndex + 1, particleData.endRotationSpeed);
        }

        particleData.rotationInterpolation = interpolations.get(particleData.rotationIndex);

        if (randomStartRotationX || randomStartRotationY || randomStartRotationZ) {
            particleData.angles.set(
                    randomStartRotationX ? FastMath.nextRandomFloat() * FastMath.TWO_PI : 0,
                    randomStartRotationY ? FastMath.nextRandomFloat() * FastMath.TWO_PI : 0,
                    randomStartRotationZ ? FastMath.nextRandomFloat() * FastMath.TWO_PI : 0
            );
        } else {
            particleData.angles.set(0, 0, 0);
        }
    }

    private void getRotationSpeed(@NotNull final ParticleData particleData, final int index, final Vector3f store) {
        store.set(speeds.get(index));

        if (randomSpeed) {
            store.set(
                    FastMath.nextRandomFloat() * store.x,
                    FastMath.nextRandomFloat() * store.y,
                    FastMath.nextRandomFloat() * store.z
            );
        }
        if (randomDirection) {
            store.x = particleData.rotateDirectionX ? store.x : -store.x;
            store.y = particleData.rotateDirectionY ? store.y : -store.y;
            store.z = particleData.rotateDirectionZ ? store.z : -store.z;
        }
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
        particleData.angles.set(0, 0, 0);
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Add rotation speed.
     *
     * @param speed the new rotation speed.
     */
    public void addRotationSpeed(@NotNull final Vector3f speed) {
        addRotationSpeed(speed, Interpolation.LINEAR);
    }

    /**
     * Add rotation speed.
     *
     * @param speed         the new rotation speed.
     * @param interpolation the interpolation.
     */
    public void addRotationSpeed(@NotNull final Vector3f speed, @NotNull final Interpolation interpolation) {
        speeds.add(speed.clone());
        interpolations.add(interpolation);
    }

    /**
     * Remove rotation speed and interpolation for the index.
     *
     * @param index the index.
     */
    public void removeRotationSpeed(final int index) {
        speeds.slowRemove(index);
        interpolations.slowRemove(index);
    }

    /**
     * Remove all rotation speeds.
     */
    public void removeAll() {
        this.speeds.clear();
        this.interpolations.clear();
    }

    /**
     * @return the list of rotations.
     */
    public Array<Vector3f> getRotationSpeeds() {
        return speeds;
    }

    /**
     * @param index the index.
     * @return the rotation speed for the index.
     */
    @NotNull
    public Vector3f getRotationSpeed(final int index) {
        return speeds.get(index);
    }

    /**
     * @return the list of interpolations.
     */
    public Array<Interpolation> getInterpolations() {
        return interpolations;
    }

    /**
     * @param index the index.
     * @return the interpolation for the index.
     */
    @NotNull
    public Interpolation getInterpolation(final int index) {
        return interpolations.get(index);
    }

    /**
     * Change a rotation speed for the index.
     *
     * @param rotationSpeed the new rotation speed.
     * @param index         the index.
     */
    public void updateRotationSpeed(final @NotNull Vector3f rotationSpeed, final int index) {
        speeds.set(index, rotationSpeed);
    }

    /**
     * Change a interpolation for the index.
     *
     * @param interpolation the new interpolation.
     * @param index         the index.
     */
    public void updateInterpolation(final @NotNull Interpolation interpolation, final int index) {
        interpolations.set(index, interpolation);
    }

    /**
     * Remove last a rotation speed and interpolation.
     */
    public void removeLast() {
        if (speeds.isEmpty()) return;
        interpolations.fastRemove(interpolations.size() - 1);
        speeds.fastRemove(speeds.size() - 1);
    }

    /**
     * Allows the influencer to choose a random rotation direction per axis as the particle is
     * emitted.
     *
     * @param randomDirection boolean
     */
    public void setRandomDirection(final boolean randomDirection) {
        this.randomDirection = randomDirection;
    }

    /**
     * Returns if the influencer currently selects a random rotation direction per axis as the
     * particle is emitted.
     *
     * @return boolean
     */
    public boolean isRandomDirection() {
        return randomDirection;
    }

    /**
     * Allows the influencer to select a random rotation speed from 0 to the provided maximum speeds
     * per axis
     *
     * @param randomSpeed boolean
     */
    public void setRandomSpeed(final boolean randomSpeed) {
        this.randomSpeed = randomSpeed;
    }

    /**
     * Returns if the influencer currently to selects random rotation speeds per axis
     *
     * @return boolean
     */
    public boolean isRandomSpeed() {
        return randomSpeed;
    }

    public void setRandomStartRotation(final boolean xRotation, final boolean yRotation, final boolean zRotation) {
        randomStartRotationX = xRotation;
        randomStartRotationY = yRotation;
        randomStartRotationZ = zRotation;
    }

    public void setRandomStartRotationX(final boolean randomStartRotationX) {
        this.randomStartRotationX = randomStartRotationX;
    }

    public void setRandomStartRotationY(final boolean randomStartRotationY) {
        this.randomStartRotationY = randomStartRotationY;
    }

    public void setRandomStartRotationZ(final boolean randomStartRotationZ) {
        this.randomStartRotationZ = randomStartRotationZ;
    }

    public boolean isRandomStartRotationX() {
        return randomStartRotationX;
    }

    public boolean isRandomStartRotationY() {
        return randomStartRotationY;
    }

    public boolean isRandomStartRotationZ() {
        return randomStartRotationZ;
    }

    /**
     * Forces the rotation direction to always remain constant per particle
     *
     * @param direction boolean
     */
    public void setDirection(final boolean direction) {
        this.direction = direction;
    }

    /**
     * Returns if the rotation direction will always remain constant per particle
     */
    public boolean isDirection() {
        return direction;
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {

        final int[] interpolationIds = interpolations.stream()
                .mapToInt(InterpolationManager::getId).toArray();

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(speeds.toArray(new Vector3f[speeds.size()]), "speeds", null);
        capsule.write(interpolationIds, "interpolations", null);
        capsule.write(speedFactor, "speedFactor", Vector3f.ZERO);
        capsule.write(randomDirection, "randomDirection", true);
        capsule.write(randomSpeed, "randomSpeed", true);
        capsule.write(direction, "direction", true);
        capsule.write(randomStartRotationX, "randomStartRotationX", false);
        capsule.write(randomStartRotationY, "randomStartRotationY", false);
        capsule.write(randomStartRotationZ, "randomStartRotationZ", false);
        capsule.write(fixedDuration, "fixedDuration", 0f);
        capsule.write(enabled, "enabled", true);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {

        final InputCapsule capsule = importer.getCapsule(this);
        final Savable[] loadedSpeeds = capsule.readSavableArray("speeds", null);

        ArrayUtils.forEach(loadedSpeeds, speeds, (savable, toStore) -> toStore.add((Vector3f) savable));

        final int[] loadedInterpolations = capsule.readIntArray("interpolations", null);

        ArrayUtils.forEach(loadedInterpolations, interpolations,
                (id, toStore) -> toStore.add(InterpolationManager.getInterpolation(id)));

        speedFactor.set((Vector3f) capsule.readSavable("speedFactor", Vector3f.ZERO.clone()));
        randomDirection = capsule.readBoolean("randomDirection", true);
        randomSpeed = capsule.readBoolean("randomSpeed", true);
        direction = capsule.readBoolean("direction", true);
        randomStartRotationX = capsule.readBoolean("randomStartRotationX", false);
        randomStartRotationY = capsule.readBoolean("randomStartRotationY", false);
        randomStartRotationZ = capsule.readBoolean("randomStartRotationZ", false);
        fixedDuration = capsule.readFloat("fixedDuration", 0f);
        enabled = capsule.readBoolean("enabled", true);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        try {
            RotationInfluencer clone = (RotationInfluencer) super.clone();
            clone.setDirection(direction);
            clone.setRandomDirection(randomDirection);
            clone.setRandomSpeed(randomSpeed);
            clone.setRandomStartRotation(randomStartRotationX, randomStartRotationY, randomStartRotationZ);
            clone.setEnabled(enabled);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
