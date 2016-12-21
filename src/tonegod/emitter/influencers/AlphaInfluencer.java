package tonegod.emitter.influencers;

import static com.jme3.math.FastMath.interpolateLinear;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.util.SafeArrayList;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.interpolation.InterpolationManager;
import tonegod.emitter.particle.ParticleData;

/**
 * The implementation of the {@link ParticleInfluencer} for influence to alpha of particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class AlphaInfluencer implements ParticleInfluencer {

    private SafeArrayList<Interpolation> interpolations;
    private SafeArrayList<Float> alphas;

    private boolean randomStartAlpha;
    private boolean initialized;
    private boolean enabled;
    private boolean cycle;

    private float startAlpha;
    private float endAlpha;
    private float blend;
    private float fixedDuration;

    public AlphaInfluencer() {
        this.alphas = new SafeArrayList<>(Float.class);
        this.interpolations = new SafeArrayList<>(Interpolation.class);
        this.enabled = true;
        this.startAlpha = 1;
    }

    @NotNull
    @Override
    public String getName() {
        return "Alpha influencer";
    }

    @Override
    public void update(@NotNull final ParticleData particleData, final float tpf) {
        if (!enabled) return;

        particleData.alphaInterval += tpf;

        if (particleData.alphaInterval >= particleData.alphaDuration) {
            updateAlpha(particleData);
        }

        blend = particleData.alphaInterpolation.apply(particleData.alphaInterval / particleData.alphaDuration);

        final Float[] alphasArray = alphas.getArray();

        startAlpha = alphasArray[particleData.alphaIndex];

        if (particleData.alphaIndex == alphas.size() - 1) {
            endAlpha = alphasArray[0];
        } else {
            endAlpha = alphasArray[particleData.alphaIndex + 1];
        }

        particleData.alpha = interpolateLinear(blend, startAlpha, endAlpha);
    }

    private void updateAlpha(final ParticleData particleData) {
        particleData.alphaIndex++;

        if (particleData.alphaIndex >= alphas.size()) {
            particleData.alphaIndex = 0;
        }

        particleData.alphaInterpolation = interpolations.getArray()[particleData.alphaIndex];
        particleData.alphaInterval -= particleData.alphaDuration;
    }

    @Override
    public void initialize(@NotNull final ParticleData particleData) {

        if (!initialized) {
            if (alphas.isEmpty()) {
                addAlpha(1f);
                addAlpha(0f);
            } else if (alphas.size() == 1) {
                setEnabled(false);
            }
            initialized = true;
        }

        if (randomStartAlpha) {
            particleData.alphaIndex = FastMath.nextRandomInt(0, alphas.size() - 1);
        } else {
            particleData.alphaIndex = 0;
        }

        particleData.alphaInterval = 0f;
        particleData.alphaDuration = (cycle) ? fixedDuration : particleData.startlife / ((float) alphas.size() - 1);
        particleData.alpha = alphas.getArray()[particleData.alphaIndex];
        particleData.alphaInterpolation = interpolations.getArray()[particleData.alphaIndex];
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
        particleData.alpha = 0;
    }

    /**
     * Adds a alpha step value using linear interpolation to the chain of values used throughout the
     * particles life span
     */
    public void addAlpha(final float alpha) {
        addAlpha(alpha, Interpolation.LINEAR);
    }

    /**
     * Adds a alpha step value to the chain of values used throughout the particles life span
     */
    public void addAlpha(final float alpha, @NotNull final Interpolation interpolation) {
        this.alphas.add(alpha);
        this.interpolations.add(interpolation);
    }

    /**
     * Returns an array containing all alpha step values
     */
    @NotNull
    public Float[] getAlphas() {
        return alphas.getArray();
    }

    /**
     * Returns an array containing all interpolation step values
     */
    @NotNull
    public Interpolation[] getInterpolations() {
        return interpolations.getArray();
    }

    /**
     * Removes the alpha step value at the given index
     */
    public void removeAlpha(final int index) {
        alphas.remove(index);
        interpolations.remove(index);
    }

    /**
     * Removes all added alpha step values
     */
    public void removeAll() {
        alphas.clear();
        interpolations.clear();
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        Map<String, Vector2f> as = new HashMap<String, Vector2f>();
        int index = 0;
        for (Float alpha : alphas.getArray()) {
            as.put(String.valueOf(alpha) + ":" + String.valueOf(index), null);
            index++;
        }
        oc.writeStringSavableMap(as, "alphas", null);
        Map<String, Vector2f> interps = new HashMap<String, Vector2f>();
        index = 0;
        for (Interpolation in : interpolations.getArray()) {
            interps.put(in.getName() + ":" + String.valueOf(index), null);
            index++;
        }
        oc.writeStringSavableMap(interps, "interpolations", null);
        oc.write(enabled, "enabled", true);
        oc.write(randomStartAlpha, "randomStartAlpha", false);
        oc.write(cycle, "cycle", false);
        oc.write(fixedDuration, "fixedDuration", 0.125f);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        Map<String, Vector2f> as = (Map<String, Vector2f>) ic.readStringSavableMap("alphas", null);
        for (String in : as.keySet()) {
            String val = in.substring(0, in.indexOf(":"));
            alphas.add(Float.valueOf(val));
        }
        Map<String, Vector2f> interps = (Map<String, Vector2f>) ic.readStringSavableMap("interpolations", null);
        for (String in : interps.keySet()) {
            String name = in.substring(0, in.indexOf(":"));
            interpolations.add(InterpolationManager.getInterpolation(name));
        }
        enabled = ic.readBoolean("enabled", true);
        randomStartAlpha = ic.readBoolean("randomStartAlpha", false);
        cycle = ic.readBoolean("cycle", false);
        fixedDuration = ic.readFloat("fixedDuration", 0.125f);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        try {
            AlphaInfluencer clone = (AlphaInfluencer) super.clone();
            clone.alphas.addAll(alphas);
            clone.interpolations.addAll(interpolations);
            clone.enabled = enabled;
            clone.randomStartAlpha = randomStartAlpha;
            clone.cycle = cycle;
            clone.fixedDuration = fixedDuration;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Animated texture should cycle and use the provided duration between frames (0 diables
     * cycling)
     *
     * @param fixedDuration duration between frame updates
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
     * Returns the current duration used between frames for cycled animation
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
