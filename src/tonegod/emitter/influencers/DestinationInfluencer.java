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
 * The implementation of the {@link ParticleInfluencer} for influence to destination of particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class DestinationInfluencer implements ParticleInfluencer {

    /**
     * The list of destinations.
     */
    private final UnsafeArray<Vector3f> destinations;

    /**
     * The list of weights.
     */
    private final UnsafeArray<Float> weights;

    /**
     * The list of interpolations.
     */
    private final UnsafeArray<Interpolation> interpolations;

    /**
     * The destination direction.
     */
    private final Vector3f destinationDir;

    private float blend;
    private float weight;
    private float fixedDuration;
    private float dist;

    private boolean randomStartDestination;
    private boolean cycle;
    private boolean enabled;
    private boolean initialized;

    public DestinationInfluencer() {
        this.destinations = ArrayFactory.newUnsafeArray(Vector3f.class);
        this.weights = ArrayFactory.newUnsafeArray(Float.class);
        this.interpolations = ArrayFactory.newUnsafeArray(Interpolation.class);
        this.destinationDir = new Vector3f();
        this.enabled = true;
        this.weight = 1F;
    }

    @NotNull
    @Override
    public String getName() {
        return "Destination influencer";
    }

    @Override
    public void update(@NotNull final ParticleData particleData, final float tpf) {
        if (!enabled) return;

        particleData.destinationInterval += tpf;

        if (particleData.destinationInterval >= particleData.destinationDuration) {
            updateDestination(particleData);
        }

        blend = particleData.destinationInterpolation.apply(particleData.destinationInterval / particleData.destinationDuration);

        destinationDir.set(destinations.get(particleData.destinationIndex).subtract(particleData.position));
        dist = particleData.position.distance(destinations.get(particleData.destinationIndex));
        destinationDir.multLocal(dist);

        weight = weights.get(particleData.destinationIndex);

        particleData.velocity.interpolateLocal(destinationDir, blend * tpf * (weight * 10));
    }

    private void updateDestination(@NotNull final ParticleData particleData) {
        particleData.destinationIndex++;

        if (particleData.destinationIndex == destinations.size()) {
            particleData.destinationIndex = 0;
        }

        particleData.destinationInterpolation = interpolations.get(particleData.destinationIndex);
        particleData.destinationInterval -= particleData.destinationDuration;
    }

    @Override
    public void initialize(@NotNull final ParticleData particleData) {

        if (!initialized) {
            if (destinations.isEmpty()) {
                addDestination(new Vector3f(0, 0, 0), 0.5f);
            }
            initialized = true;
        }

        if (randomStartDestination) {
            particleData.destinationIndex = FastMath.nextRandomInt(0, destinations.size() - 1);
        } else {
            particleData.destinationIndex = 0;
        }

        particleData.destinationInterval = 0f;
        particleData.destinationDuration = (cycle) ? fixedDuration : particleData.startlife / ((float) destinations.size());
        particleData.destinationInterpolation = interpolations.get(particleData.destinationIndex);
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
    }

    /**
     * When enabled, the initial step the particle will start at will be randomly selected from the
     * defined list of directions
     */
    public void setRandomStartDestination(final boolean randomStartDestination) {
        this.randomStartDestination = randomStartDestination;
    }

    /**
     * Returns if the influencer will start a newly emitted particle at a random step in the
     * provided list of directions
     */
    public boolean isRandomStartDestination() {
        return randomStartDestination;
    }

    /**
     * Adds a destination using linear interpolation to the list of destinations used during the
     * life cycle of the particle
     *
     * @param destination The destination the particle will move towards
     * @param weight      How strong the pull towards the destination should be
     */
    public void addDestination(@NotNull final Vector3f destination, final float weight) {
        addDestination(destination, weight, Interpolation.LINEAR);
    }

    /**
     * Adds a destination using the defined interpolation to the list of destinations used during
     * the life cycle of the particle
     *
     * @param destination The destination the particle will move towards
     * @param weight      How strong the pull towards the destination should be
     * @interpolation The interpolation method used to blend from the this step value to the next
     */
    public void addDestination(@NotNull final Vector3f destination, final float weight, @NotNull final Interpolation interpolation) {
        destinations.add(destination.clone());
        weights.add(weight);
        interpolations.add(interpolation);
    }

    /**
     * Removes the destination step value at the supplied index
     */
    public void removeDestination(final int index) {
        destinations.slowRemove(index);
        weights.slowRemove(index);
        interpolations.slowRemove(index);
    }

    /**
     * Removes all destination step values
     */
    public void removeAll() {
        destinations.clear();
        weights.clear();
        interpolations.clear();
    }

    /**
     * Returns an array containing all destination step values
     */
    @NotNull
    public Array<Vector3f> getDestinations() {
        return destinations;
    }

    /**
     * @param index the index.
     * @return the destination for the index.
     */
    @NotNull
    public Vector3f getDestination(final int index) {
        return destinations.get(index);
    }

    /**
     * Returns an array containing all step value weights
     */
    @NotNull
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
     * Returns an array containing all step value interpolations
     */
    @NotNull
    public Array<Float> getWeights() {
        return weights;
    }

    /**
     * @param index the index.
     * @return the weight for the index.
     */
    @NotNull
    public Float getWeight(final int index) {
        return weights.get(index);
    }

    /**
     * Change a destination for the index.
     *
     * @param destination the new destination.
     * @param index       the index.
     */
    public void updateDestination(final @NotNull Vector3f destination, final int index) {
        destinations.set(index, destination);
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
     * Change a weight for the index.
     *
     * @param weight the new weight.
     * @param index  the index.
     */
    public void updateWeight(final @NotNull Float weight, final int index) {
        weights.set(index, weight);
    }

    /**
     * Remove last a destination, weight and interpolation.
     */
    public void removeLast() {
        if (weights.isEmpty()) return;
        destinations.fastRemove(destinations.size() - 1);
        interpolations.fastRemove(interpolations.size() - 1);
        weights.fastRemove(weights.size() - 1);
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {

        final int[] interpolationIds = interpolations.stream()
                .mapToInt(InterpolationManager::getId).toArray();

        final double[] weightsToSave = weights.stream().
                mapToDouble(value -> value).toArray();

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(destinations.toArray(new Vector3f[destinations.size()]), "destinations", null);
        capsule.write(weightsToSave, "weights", null);
        capsule.write(interpolationIds, "interpolations", null);
        capsule.write(enabled, "enabled", true);
        capsule.write(randomStartDestination, "randomStartDestination", false);
        capsule.write(cycle, "cycle", false);
        capsule.write(fixedDuration, "fixedDuration", 0.125f);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {

        final InputCapsule capsule = importer.getCapsule(this);
        final Savable[] loadedDestinations = capsule.readSavableArray("destinations", null);

        ArrayUtils.forEach(loadedDestinations, destinations, (savable, toStore) -> toStore.add((Vector3f) savable));

        final double[] loadedWeights = capsule.readDoubleArray("weights", null);

        ArrayUtils.forEach(loadedWeights, weights, (element, toStore) -> toStore.add((float) element));

        final int[] loadedInterpolations = capsule.readIntArray("interpolations", null);

        ArrayUtils.forEach(loadedInterpolations, interpolations,
                (id, toStore) -> toStore.add(InterpolationManager.getInterpolation(id)));

        enabled = capsule.readBoolean("enabled", true);
        randomStartDestination = capsule.readBoolean("randomStartDestination", false);
        cycle = capsule.readBoolean("cycle", false);
        fixedDuration = capsule.readFloat("fixedDuration", 0.125f);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        try {
            DestinationInfluencer clone = (DestinationInfluencer) super.clone();
            clone.destinations.addAll(destinations);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Each step value should last the specified duration, cycling once reaching the end of the
     * defined list (A value of 0 disables cycling)
     *
     * @param fixedDuration duration between step value updates
     */
    public void setFixedDuration(final float fixedDuration) {
        if (fixedDuration != 0) {
            this.cycle = true;
            this.fixedDuration = fixedDuration;
        } else {
            this.cycle = false;
            this.fixedDuration = 0;
        }
    }

    /**
     * Returns the current duration used between steps for cycling
     */
    public float getFixedDuration() {
        return fixedDuration;
    }

    @Override
    public void setEnabled(boolean enable) {
        this.enabled = enable;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
