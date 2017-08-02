package tonegod.emitter.influencers.impl;

import static tonegod.emitter.util.RandomUtils.getRandom;
import static tonegod.emitter.util.RandomUtils.nextRandomInt;
import com.jme3.export.*;
import com.jme3.math.Vector3f;
import com.jme3.util.SafeArrayList;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.Messages;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.particle.ParticleData;

import java.io.IOException;

/**
 * The implementation of the {@link ParticleInfluencer} to change destinations of particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public class DestinationInfluencer extends AbstractInterpolatedParticleInfluencer {

    /**
     * The list of destinations.
     */
    @NotNull
    private SafeArrayList<Vector3f> destinations;

    /**
     * The list of weights.
     */
    @NotNull
    private SafeArrayList<Float> weights;

    /**
     * The destination direction.
     */
    @NotNull
    private final Vector3f destinationDir;

    /**
     * The weight value.
     */
    private float weight;

    /**
     * The flag of using random start destination.
     */
    private boolean randomStartDestination;

    /**
     * Instantiates a new Destination influencer.
     */
    public DestinationInfluencer() {
        this.destinations = new SafeArrayList<>(Vector3f.class);
        this.weights = new SafeArrayList<>(Float.class);
        this.destinationDir = new Vector3f();
        this.weight = 1F;
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.PARTICLE_INFLUENCER_DESTINATION;
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {
        particleData.destinationInterval += tpf;

        if (particleData.destinationIndex >= destinations.size()) {
            particleData.destinationIndex = 0;
        }

        if (particleData.destinationInterval >= particleData.destinationDuration) {
            updateDestination(particleData);
        }

        final Interpolation interpolation = particleData.destinationInterpolation;
        final Vector3f position = particleData.getPosition();

        final int destinationIndex = particleData.destinationIndex;
        final Vector3f destination = destinations.get(destinationIndex);

        final float dist = position.distance(destination);

        blend = interpolation.apply(particleData.destinationInterval / particleData.destinationDuration);

        //TODO recheck
        // destinationDir.set(destination.subtract(particleData.position));
        destination.subtract(position, destinationDir);
        destinationDir.multLocal(dist);

        weight = weights.get(destinationIndex);

        particleData.velocity.interpolateLocal(destinationDir, blend * tpf * (weight * 10));
    }

    /**
     * Update a destination.
     *
     * @param particleData the particle data.
     */
    private void updateDestination(@NotNull final ParticleData particleData) {
        particleData.destinationIndex++;

        if (particleData.destinationIndex >= destinations.size()) {
            particleData.destinationIndex = 0;
        }

        final SafeArrayList<Interpolation> interpolations = getInterpolations();
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

        if (isRandomStartDestination()) {
            particleData.destinationIndex = nextRandomInt(getRandom(), 0, destinations.size() - 1);
        } else {
            particleData.destinationIndex = 0;
        }

        final SafeArrayList<Interpolation> interpolations = getInterpolations();
        particleData.destinationInterval = 0f;
        particleData.destinationDuration = isCycle() ? getFixedDuration() : particleData.startlife / ((float) destinations.size());
        particleData.destinationInterpolation = interpolations.get(particleData.destinationIndex);
    }

    /**
     * When enabled, the initial step the particle will start at will be randomly selected from the defined list of
     * directions
     *
     * @param randomStartDestination the random start destination
     */
    public void setRandomStartDestination(final boolean randomStartDestination) {
        this.randomStartDestination = randomStartDestination;
    }

    /**
     * Returns if the influencer will start a newly emitted particle at a random step in the provided list of
     * directions
     *
     * @return the boolean
     */
    public boolean isRandomStartDestination() {
        return randomStartDestination;
    }

    /**
     * Adds a destination using linear interpolation to the list of destinations used during the life cycle of the
     * particle
     *
     * @param destination The destination the particle will move towards
     * @param weight      How strong the pull towards the destination should be
     */
    public void addDestination(@NotNull final Vector3f destination, final float weight) {
        addDestination(destination, weight, Interpolation.LINEAR);
    }

    /**
     * Adds a destination using the defined interpolation to the list of destinations used during the life cycle of the
     * particle
     *
     * @param destination   The destination the particle will move towards
     * @param weight        How strong the pull towards the destination should be
     * @param interpolation The interpolation method used to blend from the this step value to the next
     */
    public void addDestination(@NotNull final Vector3f destination, final float weight,
                               @NotNull final Interpolation interpolation) {
        addInterpolation(interpolation);
        destinations.add(destination.clone());
        weights.add(weight);
    }

    /**
     * Removes the destination step value at the supplied index
     *
     * @param index the index
     */
    public void removeDestination(final int index) {
        removeInterpolation(index);
        destinations.remove(index);
        weights.remove(index);
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
     *
     * @return the destinations
     */
    @NotNull
    public SafeArrayList<Vector3f> getDestinations() {
        return destinations;
    }

    /**
     * Gets destination.
     *
     * @param index the index.
     * @return the destination for the index.
     */
    @NotNull
    public Vector3f getDestination(final int index) {
        return destinations.get(index);
    }

    /**
     * Returns an array containing all step value interpolations
     *
     * @return the weights
     */
    @NotNull
    public SafeArrayList<Float> getWeights() {
        return weights;
    }

    /**
     * Gets weight.
     *
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

        final SafeArrayList<Float> weights = getWeights();
        if (weights.isEmpty()) return;

        final int index = weights.size() - 1;

        removeInterpolation(index);
        destinations.remove(index);
        weights.remove(index);
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        super.write(exporter);

        final Float[] values = weights.getArray();
        final double[] weightsToSave = new double[weights.size()];

        for (int i = 0; i < values.length; i++) {
            weightsToSave[i] = values[i];
        }

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(destinations.toArray(new Vector3f[destinations.size()]), "destinations", null);
        capsule.write(weightsToSave, "weights", null);
        capsule.write(randomStartDestination, "randomStartDestination", false);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        super.read(importer);

        final InputCapsule capsule = importer.getCapsule(this);

        final Savable[] readDestinations = capsule.readSavableArray("destinations", null);
        final double[] readWeights = capsule.readDoubleArray("weights", null);

        if (readDestinations != null) {
            for (final Savable destination : readDestinations) {
                destinations.add((Vector3f) destination);
            }
        }

        if (readWeights != null) {
            for (final double value : readWeights) {
                weights.add((float) value);
            }
        }

        randomStartDestination = capsule.readBoolean("randomStartDestination", false);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        final DestinationInfluencer clone = (DestinationInfluencer) super.clone();
        clone.destinations = new SafeArrayList<>(Vector3f.class);
        clone.destinations.addAll(destinations);
        clone.weights = new SafeArrayList<>(Float.class);
        clone.weights.addAll(weights);
        clone.randomStartDestination = randomStartDestination;
        return clone;
    }
}
