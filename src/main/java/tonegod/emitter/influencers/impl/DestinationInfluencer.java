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
import java.util.concurrent.Callable;

/**
 * The implementation of the {@link ParticleInfluencer} to change destinations of particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public class DestinationInfluencer extends AbstractInterpolatedParticleInfluencer {

    private static final int DATA_ID = ParticleData.reserveObjectDataId();

    @NotNull
    private static final Callable<DestinationInfluencerData> DATA_FACTORY = new Callable<DestinationInfluencerData>() {
        @Override
        public DestinationInfluencerData call() throws Exception {
            return new DestinationInfluencerData();
        }
    };

    private static class DestinationInfluencerData {

        /**
         * The interpolation.
         */
        @NotNull
        public Interpolation interpolation;

        /**
         * The index.
         */
        public int index;

        /**
         * The interval.
         */
        public float interval;

        /**
         * The duration.
         */
        public float duration;

        private DestinationInfluencerData() {
            this.interpolation = Interpolation.LINEAR;
            this.duration = 1;
        }
    }

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

    public DestinationInfluencer() {
        this.destinations = new SafeArrayList<>(Vector3f.class);
        this.weights = new SafeArrayList<>(Float.class);
        this.destinationDir = new Vector3f();
        this.weight = 1F;
    }

    @Override
    public @NotNull String getName() {
        return Messages.PARTICLE_INFLUENCER_DESTINATION;
    }

    @Override
    protected void updateImpl(@NotNull final ParticleData particleData, final float tpf) {

        final DestinationInfluencerData data = particleData.getObjectData(DATA_ID);
        data.interval += tpf;

        if (data.index >= destinations.size()) {
            data.index = 0;
        }

        if (data.interval >= data.duration) {
            updateDestination(data);
        }

        final Interpolation interpolation = data.interpolation;
        final Vector3f position = particleData.getPosition();

        final int destinationIndex = data.index;
        final Vector3f destination = destinations.get(destinationIndex);

        final float dist = position.distance(destination);

        blend = interpolation.apply(data.interval / data.duration);

        //TODO recheck
        // destinationDir.set(destination.subtract(particleData.position));
        destination.subtract(position, destinationDir);
        destinationDir.multLocal(dist);

        weight = weights.get(destinationIndex);

        particleData.velocity.interpolateLocal(destinationDir, blend * tpf * (weight * 10));
    }

    /**
     * Update the destination.
     *
     * @param data the infuelncer's data.
     */
    private void updateDestination(@NotNull final DestinationInfluencerData data) {
        data.index++;

        if (data.index >= destinations.size()) {
            data.index = 0;
        }

        final SafeArrayList<Interpolation> interpolations = getInterpolations();
        data.interpolation = interpolations.get(data.index);
        data.interval -= data.duration;
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
        particleData.initializeObjectData(DATA_ID, DATA_FACTORY);

        final DestinationInfluencerData data = particleData.getObjectData(DATA_ID);

        if (isRandomStartDestination()) {
            data.index = nextRandomInt(getRandom(), 0, destinations.size() - 1);
        } else {
            data.index = 0;
        }

        final SafeArrayList<Interpolation> interpolations = getInterpolations();
        data.interval = 0f;
        data.duration = isCycle() ? getFixedDuration() : particleData.startlife / ((float) destinations.size());
        data.interpolation = interpolations.get(data.index);
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
     * Removes the destination step value at the supplied index.
     *
     * @param index the index
     */
    public void removeDestination(final int index) {
        removeInterpolation(index);
        destinations.remove(index);
        weights.remove(index);
    }

    /**
     * Removes all destination step values.
     */
    public void removeAll() {
        clearInterpolations();
        destinations.clear();
        weights.clear();
    }

    /**
     * Returns an array containing all destination step values.
     *
     * @return the destination steps.
     */
    public @NotNull SafeArrayList<Vector3f> getDestinations() {
        return destinations;
    }

    /**
     * Get the destination by the index.
     *
     * @param index the index.
     * @return the destination.
     */
    public @NotNull Vector3f getDestination(final int index) {
        return destinations.get(index);
    }

    /**
     * Returns the array with all step value interpolations.
     *
     * @return the weights.
     */
    public @NotNull SafeArrayList<Float> getWeights() {
        return weights;
    }

    /**
     * Get the weight by the index.
     *
     * @param index the index.
     * @return the weight.
     */
    public @NotNull Float getWeight(final int index) {
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
        if (weights.isEmpty()) {
            return;
        }

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

    @Override
    public @NotNull ParticleInfluencer clone() {
        final DestinationInfluencer clone = (DestinationInfluencer) super.clone();
        clone.destinations = new SafeArrayList<>(Vector3f.class);
        clone.destinations.addAll(destinations);
        clone.weights = new SafeArrayList<>(Float.class);
        clone.weights.addAll(weights);
        clone.randomStartDestination = randomStartDestination;
        return clone;
    }
}
