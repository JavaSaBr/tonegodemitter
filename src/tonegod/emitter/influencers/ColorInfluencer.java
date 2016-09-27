package tonegod.emitter.influencers;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.util.SafeArrayList;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tonegod.emitter.Interpolation;
import tonegod.emitter.particle.ParticleData;

import static com.jme3.math.FastMath.nextRandomInt;

/**
 * The implementation of the {@link ParticleInfluencer} for influence to color of particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class ColorInfluencer implements ParticleInfluencer {

    private final SafeArrayList<Interpolation> interpolations;
    private final SafeArrayList<ColorRGBA> colors;

    private final transient ColorRGBA resetColor;

    private final ColorRGBA startColor;
    private final ColorRGBA endColor;

    private float blend;
    private float fixedDuration;

    private boolean initialized;
    private boolean enabled;
    private boolean randomStartColor;
    private boolean cycle;

    public ColorInfluencer() {
        this.colors = new SafeArrayList<>(ColorRGBA.class);
        this.interpolations = new SafeArrayList<>(Interpolation.class);
        this.resetColor = new ColorRGBA(0, 0, 0, 0);
        this.startColor = new ColorRGBA().set(ColorRGBA.Red);
        this.endColor = new ColorRGBA().set(ColorRGBA.Yellow);
        this.enabled = true;
    }

    @NotNull
    @Override
    public String getName() {
        return "Color influencer";
    }

    @Override
    public void update(@NotNull ParticleData particleData, float tpf) {
        if (!enabled) return;

        particleData.colorInterval += tpf;

        if (particleData.colorInterval >= particleData.colorDuration) {
            updateColor(particleData);
        }

        final ColorRGBA[] colorsArray = colors.getArray();

        blend = particleData.colorInterpolation.apply(particleData.colorInterval / particleData.colorDuration);
        startColor.set(colorsArray[particleData.colorIndex]);

        if (particleData.colorIndex == colors.size() - 1) {
            endColor.set(colorsArray[0]);
        } else {
            endColor.set(colorsArray[particleData.colorIndex + 1]);
        }

        particleData.color.interpolateLocal(startColor, endColor, blend);
    }

    private void updateColor(@NotNull final ParticleData particleData) {
        particleData.colorIndex++;

        if (particleData.colorIndex >= colors.size()) {
            particleData.colorIndex = 0;
        }

        final Interpolation[] array = interpolations.getArray();
        particleData.colorInterpolation = array[particleData.colorIndex];
        particleData.colorInterval -= particleData.colorDuration;
    }

    @Override
    public void initialize(@NotNull final ParticleData particleData) {

        if (!initialized) {
            if (colors.isEmpty()) {
                addColor(ColorRGBA.Red);
                addColor(ColorRGBA.Yellow);
            } else if (colors.size() == 1) {
                setEnabled(false);
            }
            initialized = true;
        }

        if (randomStartColor) {
            particleData.colorIndex = nextRandomInt(0, colors.size() - 1);
        } else {
            particleData.colorIndex = 0;
        }

        particleData.colorInterval = 0F;
        particleData.colorDuration = (cycle) ? fixedDuration : particleData.startlife / ((float) colors.size() - 1);
        particleData.color.set(colors.getArray()[particleData.colorIndex]);
        particleData.colorInterpolation = interpolations.getArray()[particleData.colorIndex];
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
        particleData.color.set(resetColor);
        particleData.colorIndex = 0;
        particleData.colorInterval = 0;
    }

    public void setRandomStartColor(final boolean randomStartColor) {
        this.randomStartColor = randomStartColor;
    }

    public boolean isRandomStartColor() {
        return randomStartColor;
    }

    public void addColor(@NotNull ColorRGBA color) {
        addColor(color, Interpolation.linear);
    }

    public void addColor(@NotNull ColorRGBA color, @NotNull Interpolation interpolation) {
        colors.add(color.clone());
        interpolations.add(interpolation);
    }

    public void removeColor(final int index) {
        colors.remove(index);
        interpolations.remove(index);
    }

    public void removeAll() {
        this.colors.clear();
        this.interpolations.clear();
    }

    public ColorRGBA[] getColors() {
        return colors.getArray();
    }

    public Interpolation[] getInterpolations() {
        return interpolations.getArray();
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.writeSavableArrayList(new ArrayList<>(colors), "colors", null);
        Map<String, Vector2f> interps = new HashMap<String, Vector2f>();
        int index = 0;
        for (Interpolation in : interpolations.getArray()) {
            interps.put(Interpolation.getInterpolationName(in) + ":" + String.valueOf(index), null);
            index++;
        }
        oc.writeStringSavableMap(interps, "interpolations", null);
        oc.write(enabled, "enabled", true);
        oc.write(randomStartColor, "randomStartColor", false);
        oc.write(cycle, "cycle", false);
        oc.write(fixedDuration, "fixedDuration", 0.125f);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        final SafeArrayList<ColorRGBA> readedColors = new SafeArrayList<>(ColorRGBA.class, ic.readSavableArrayList("colors", null));
        this.colors.addAll(readedColors);
        Map<String, Vector2f> interps = (Map<String, Vector2f>) ic.readStringSavableMap("interpolations", null);
        for (String in : interps.keySet()) {
            String name = in.substring(0, in.indexOf(":"));
            interpolations.add(Interpolation.getInterpolationByName(name));
        }
        enabled = ic.readBoolean("enabled", true);
        randomStartColor = ic.readBoolean("randomStartColor", false);
        cycle = ic.readBoolean("cycle", false);
        fixedDuration = ic.readFloat("fixedDuration", 0.125f);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        try {
            ColorInfluencer clone = (ColorInfluencer) super.clone();
            clone.colors.addAll(colors);
            clone.interpolations.addAll(interpolations);
            clone.enabled = enabled;
            clone.randomStartColor = randomStartColor;
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
     * @param fixedDuration duration between step updates
     */
    public void setFixedDuration(float fixedDuration) {
        if (fixedDuration != 0) {
            this.cycle = true;
            this.fixedDuration = fixedDuration;
        } else {
            this.cycle = false;
            this.fixedDuration = 0;
        }
    }

    /**
     * Returns the current duration used between steps for cycled animation
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
