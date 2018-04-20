package tonegod.emitter.influencers.impl;

import static tonegod.emitter.util.RandomUtils.getRandom;
import static tonegod.emitter.util.RandomUtils.nextRandomInt;
import com.jme3.export.*;
import com.jme3.math.Vector3f;
import com.jme3.util.SafeArrayList;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.Messages;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.particle.ParticleData;

import java.io.IOException;

/**
 * The implementation of the {@link ParticleInfluencer} to change destinations of particles.
 *
 * @author t0neg0d, JavaSaBr
 */
public class DestinationInfluencer extends AbstractInterpolatedParticleInfluencer<BaseInterpolationData> {

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
    public @NotNull BaseInterpolationData newDataObject() {
        return new BaseInterpolationData();
    }

    @Override
    protected void updateImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull BaseInterpolationData data,
            float tpf
    ) {

        data.interval += tpf;

        if (data.index >= destinations.size()) {
            data.index = 0;
        }

        if (data.interval >= data.duration) {
            updateInterpolation(data, getDestinations());
        }

        Interpolation interpolation = data.interpolation;
        Vector3f position = particleData.getPosition();

        int destinationIndex = data.index;
        Vector3f destination = destinations.get(destinationIndex);

        float dist = position.distance(destination);

        blend = interpolation.apply(data.interval / data.duration);

        //TODO recheck
        // destinationDir.set(destination.subtract(particleData.position));
        destination.subtract(position, destinationDir);
        destinationDir.multLocal(dist);

        weight = weights.get(destinationIndex);

        particleData.velocity.interpolateLocal(destinationDir, blend * tpf * (weight * 10));

        super.updateImpl(emitterNode, particleData, data, tpf);
    }

    @Override
    protected void firstInitializeImpl(@NotNull ParticleData particleData) {

        if (destinations.isEmpty()) {
            addDestination(new Vector3f(0, 0, 0), 0.5f);
        }

        super.firstInitializeImpl(particleData);
    }

    @Override
    protected void initializeImpl(
            @NotNull ParticleEmitterNode emitterNode,
            @NotNull ParticleData particleData,
            @NotNull BaseInterpolationData data
    ) {

        if (isRandomStartDestination()) {
            data.index = nextRandomInt(getRandom(), 0, destinations.size() - 1);
        } else {
            data.index = 0;
        }

        SafeArrayList<Interpolation> interpolations = getInterpolations();
        data.interval = 0f;
        data.duration = isCycle() ? getFixedDuration() : particleData.startLife / ((float) destinations.size());
        data.interpolation = interpolations.get(data.index);

        super.initializeImpl(emitterNode, particleData, data);
    }

    /**
     * When enabled, the initial step the particle will start at will be randomly selected from the defined list of
     * directions
     *
     * @param randomStartDestination the random start destination
     */
    public void setRandomStartDestination(boolean randomStartDestination) {
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
    public void addDestination(@NotNull Vector3f destination, float weight) {
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
    public void addDestination(
            @NotNull Vector3f destination,
            float weight,
            @NotNull Interpolation interpolation
    ) {
        addInterpolation(interpolation);
        destinations.add(destination.clone());
        weights.add(weight);
    }

    /**
     * Removes the destination step value at the supplied index.
     *
     * @param index the index
     */
    public void removeDestination(int index) {
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
     * Gets the destination by the index.
     *
     * @param index the index.
     * @return the destination.
     */
    public @NotNull Vector3f getDestination(int index) {
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
     * Gets the weight by the index.
     *
     * @param index the index.
     * @return the weight.
     */
    public @NotNull Float getWeight(int index) {
        return weights.get(index);
    }

    /**
     * Changes a destination for the index.
     *
     * @param destination the new destination.
     * @param index       the index.
     */
    public void updateDestination(@NotNull Vector3f destination, int index) {
        destinations.set(index, destination);
    }

    /**
     * Changes a weight for the index.
     *
     * @param weight the new weight.
     * @param index  the index.
     */
    public void updateWeight(@NotNull Float weight, int index) {
        weights.set(index, weight);
    }

    /**
     * Removes last a destination, weight and interpolation.
     */
    public void removeLast() {

        SafeArrayList<Float> weights = getWeights();
        if (weights.isEmpty()) {
            return;
        }

        int index = weights.size() - 1;

        removeInterpolation(index);
        destinations.remove(index);
        weights.remove(index);
    }

    @Override
    public void write(@NotNull JmeExporter exporter) throws IOException {
        super.write(exporter);

        Float[] values = weights.getArray();
        double[] weightsToSave = new double[weights.size()];

        for (int i = 0; i < values.length; i++) {
            weightsToSave[i] = values[i];
        }

        OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(destinations.toArray(new Vector3f[destinations.size()]), "destinations", null);
        capsule.write(weightsToSave, "weights", null);
        capsule.write(randomStartDestination, "randomStartDestination", false);
    }

    @Override
    public void read(@NotNull JmeImporter importer) throws IOException {
        super.read(importer);

        InputCapsule capsule = importer.getCapsule(this);

        Savable[] readDestinations = capsule.readSavableArray("destinations", null);
        double[] readWeights = capsule.readDoubleArray("weights", null);

        if (readDestinations != null) {
            for (Savable destination : readDestinations) {
                destinations.add((Vector3f) destination);
            }
        }

        if (readWeights != null) {
            for (double value : readWeights) {
                weights.add((float) value);
            }
        }

        randomStartDestination = capsule.readBoolean("randomStartDestination", false);
    }

    @Override
    public @NotNull ParticleInfluencer clone() {
        DestinationInfluencer clone = (DestinationInfluencer) super.clone();
        clone.destinations = new SafeArrayList<>(Vector3f.class);
        clone.destinations.addAll(destinations);
        clone.weights = new SafeArrayList<>(Float.class);
        clone.weights.addAll(weights);
        clone.randomStartDestination = randomStartDestination;
        return clone;
    }
}
