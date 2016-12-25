package tonegod.emitter.influencers.impl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import rlib.util.ArrayUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.array.UnsafeArray;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.particle.ParticleData;

/**
 * The implementation of the {@link ParticleInfluencer} for influence to destination of particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class DestinationInfluencer extends AbstractInterpolatedParticleInfluencer {

    /**
     * The list of destinations.
     */
    private final UnsafeArray<Vector3f> destinations;

    /**
     * The list of weights.
     */
    private final UnsafeArray<Float> weights;

    /**
     * The destination direction.
     */
    private final Vector3f destinationDir;

    /**
     * The weight value.
     */
    private float weight;

    /**
     * The distance,
     */
    private float dist;

    /**
     * The flag of using random start destination.
     */
    private boolean randomStartDestination;

    public DestinationInfluencer() {
        this.destinations = ArrayFactory.newUnsafeArray(Vector3f.class);
        this.weights = ArrayFactory.newUnsafeArray(Float.class);
        this.destinationDir = new Vector3f();
        this.weight = 1F;
    }

    @NotNull
    @Override
    public String getName() {
        return "Destination influencer";
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {
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

    /**
     * Update a destination.
     *
     * @param particleData the particle data.
     */
    private void updateDestination(@NotNull final ParticleData particleData) {
        particleData.destinationIndex++;

        if (particleData.destinationIndex == destinations.size()) {
            particleData.destinationIndex = 0;
        }

        final Array<Interpolation> interpolations = getInterpolations();
        particleData.destinationInterpolation = interpolations.get(particleData.destinationIndex);
        particleData.destinationInterval -= particleData.destinationDuration;
    }

    @Override
    protected void firstInitializeImpl(@NotNull final ParticleData particleData) {

        if (destinations.isEmpty()) {
            addDestination(new Vector3f(0, 0, 0), 0.5f);
        }

        super.firstInitializeImpl(particleData);
    }

    @Override
    protected void initializeImpl(@NotNull final ParticleData particleData) {

        if (randomStartDestination) {
            particleData.destinationIndex = FastMath.nextRandomInt(0, destinations.size() - 1);
        } else {
            particleData.destinationIndex = 0;
        }

        final Array<Interpolation> interpolations = getInterpolations();
        particleData.destinationInterval = 0f;
        particleData.destinationDuration = isCycle() ? getFixedDuration() : particleData.startlife / ((float) destinations.size());
        particleData.destinationInterpolation = interpolations.get(particleData.destinationIndex);
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
     * @param destination   The destination the particle will move towards
     * @param weight        How strong the pull towards the destination should be
     * @param interpolation The interpolation method used to blend from the this step value to the
     *                      next
     */
    public void addDestination(@NotNull final Vector3f destination, final float weight, @NotNull final Interpolation interpolation) {
        addInterpolation(interpolation);
        destinations.add(destination.clone());
        weights.add(weight);
    }

    /**
     * Removes the destination step value at the supplied index
     */
    public void removeDestination(final int index) {
        removeInterpolation(index);
        destinations.slowRemove(index);
        weights.slowRemove(index);
    }

    /**
     * Removes all destination step values
     */
    public void removeAll() {
        clearInterpolations();
        destinations.clear();
        weights.clear();
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

        final Array<Float> weights = getWeights();
        if (weights.isEmpty()) return;

        final int index = weights.size() - 1;

        removeInterpolation(index);
        destinations.fastRemove(index);
        weights.fastRemove(index);
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        super.write(exporter);

        final double[] weightsToSave = weights.stream().
                mapToDouble(value -> value).toArray();

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(destinations.toArray(new Vector3f[destinations.size()]), "destinations", null);
        capsule.write(weightsToSave, "weights", null);
        capsule.write(randomStartDestination, "randomStartDestination", false);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        super.read(importer);

        final InputCapsule capsule = importer.getCapsule(this);

        ArrayUtils.forEach(capsule.readSavableArray("destinations", null), destinations,
                (savable, toStore) -> toStore.add((Vector3f) savable));

        ArrayUtils.forEach(capsule.readDoubleArray("weights", null), weights,
                (element, toStore) -> toStore.add((float) element));

        randomStartDestination = capsule.readBoolean("randomStartDestination", false);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        final DestinationInfluencer clone = (DestinationInfluencer) super.clone();
        clone.destinations.addAll(destinations);
        clone.weights.addAll(weights);
        clone.randomStartDestination = randomStartDestination;
        return clone;
    }
}
