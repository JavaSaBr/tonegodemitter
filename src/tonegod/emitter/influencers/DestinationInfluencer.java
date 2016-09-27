package tonegod.emitter.influencers;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.util.SafeArrayList;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tonegod.emitter.Interpolation;
import tonegod.emitter.particle.ParticleData;

/**
 * The implementation of the {@link ParticleInfluencer} for influence to destination of particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class DestinationInfluencer implements ParticleInfluencer {

    private final SafeArrayList<Vector3f> destinations;
    private final SafeArrayList<Float> weights;
    private final SafeArrayList<Interpolation> interpolations;

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
        this.destinations = new SafeArrayList<>(Vector3f.class);
        this.weights = new SafeArrayList<>(Float.class);
        this.interpolations = new SafeArrayList<>(Interpolation.class);
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

        destinationDir.set(destinations.getArray()[particleData.destinationIndex].subtract(particleData.position));
        dist = particleData.position.distance(destinations.getArray()[particleData.destinationIndex]);
        destinationDir.multLocal(dist);

        weight = weights.getArray()[particleData.destinationIndex];

        particleData.velocity.interpolateLocal(destinationDir, blend * tpf * (weight * 10));
    }

    private void updateDestination(@NotNull final ParticleData particleData) {
        particleData.destinationIndex++;

        if (particleData.destinationIndex == destinations.size()) {
            particleData.destinationIndex = 0;
        }

        particleData.destinationInterpolation = interpolations.getArray()[particleData.destinationIndex];
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
        particleData.destinationInterpolation = interpolations.getArray()[particleData.destinationIndex];
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
    }

    /**
     * When enabled, the initial step the particle will start at will be randomly selected from the
     * defined list of directions
     */
    public void setRandomStartDestination(boolean randomStartDestination) {
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
    public void addDestination(Vector3f destination, float weight) {
        addDestination(destination, weight, Interpolation.linear);
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
        destinations.remove(index);
        weights.remove(index);
        interpolations.remove(index);
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
    public Vector3f[] getDestinations() {
        return destinations.getArray();
    }

    /**
     * Returns an array containing all step value weights
     */
    @NotNull
    public Interpolation[] getInterpolations() {
        return interpolations.getArray();
    }

    /**
     * Returns an array containing all step value interpolations
     */
    @NotNull
    public Float[] getWeights() {
        return weights.getArray();
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.writeSavableArrayList(new ArrayList(destinations), "destinations", null);
        oc.writeSavableArrayList(new ArrayList(weights), "weightss", null);
        Map<String, Vector2f> interps = new HashMap<String, Vector2f>();
        int index = 0;
        for (Interpolation in : interpolations.getArray()) {
            interps.put(Interpolation.getInterpolationName(in) + ":" + String.valueOf(index), null);
            index++;
        }
        oc.writeStringSavableMap(interps, "interpolations", null);
        oc.write(enabled, "enabled", true);
        oc.write(randomStartDestination, "randomStartDestination", false);
        oc.write(cycle, "cycle", false);
        oc.write(fixedDuration, "fixedDuration", 0.125f);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        destinations.addAll(new SafeArrayList<>(Vector3f.class, ic.readSavableArrayList("destinations", null)));
        weights.addAll(new SafeArrayList<>(Float.class, ic.readSavableArrayList("weights", null)));
        Map<String, Vector2f> interps = (Map<String, Vector2f>) ic.readStringSavableMap("interpolations", null);
        for (String in : interps.keySet()) {
            String name = in.substring(0, in.indexOf(":"));
            interpolations.add(Interpolation.getInterpolationByName(name));
        }
        enabled = ic.readBoolean("enabled", true);
        randomStartDestination = ic.readBoolean("randomStartDestination", false);
        cycle = ic.readBoolean("cycle", false);
        fixedDuration = ic.readFloat("fixedDuration", 0.125f);
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
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
