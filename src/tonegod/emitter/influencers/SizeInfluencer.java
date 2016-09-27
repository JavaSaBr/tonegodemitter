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
 * The implementation of the {@link ParticleInfluencer} for influence to size of particles.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class SizeInfluencer implements ParticleInfluencer {

    private final SafeArrayList<Vector3f> sizes;
    private final SafeArrayList<Interpolation> interpolations;

    private final Vector3f tempV3a;
    private final Vector3f tempV3b;
    private final Vector3f startSize;
    private final Vector3f endSize;

    private float blend;
    private float randomSizeTolerance;
    private float fixedDuration;

    private boolean initialized;
    private boolean randomSize;
    private boolean cycle;
    private boolean enabled;

    public SizeInfluencer() {
        this.sizes = new SafeArrayList<>(Vector3f.class);
        this.interpolations = new SafeArrayList<>(Interpolation.class);
        this.enabled = true;
        this.tempV3a = new Vector3f();
        this.tempV3b = new Vector3f();
        this.startSize = new Vector3f(0.1f, 0.1f, 0.1f);
        this.endSize = new Vector3f(0, 0, 0);
        this.randomSizeTolerance = 0.5f;
    }

    @NotNull
    @Override
    public String getName() {
        return "Size influencer";
    }

    @Override
    public void update(@NotNull final ParticleData particleData, final float tpf) {
        if (!enabled) return;

        particleData.sizeInterval += tpf;

        if (particleData.sizeInterval >= particleData.sizeDuration) {
            updateSize(particleData);
        }

        blend = particleData.sizeInterpolation.apply(particleData.sizeInterval / particleData.sizeDuration);
        particleData.size.interpolateLocal(particleData.startSize, particleData.endSize, blend);
    }

    private void updateSize(@NotNull final ParticleData particleData) {
        particleData.sizeIndex++;

        if (particleData.sizeIndex == sizes.size() - 1) {
            particleData.sizeIndex = 0;
        }

        getNextSizeRange(particleData);

        particleData.sizeInterpolation = interpolations.getArray()[particleData.sizeIndex];
        particleData.sizeInterval -= particleData.sizeDuration;
    }

    @Override
    public void initialize(@NotNull final ParticleData particleData) {

        if (!initialized) {
            if (sizes.isEmpty()) {
                addSize(1f);
                addSize(0f);
            } else if (sizes.size() == 1) {
                setEnabled(false);
            }
            initialized = true;
        }

        particleData.sizeIndex = 0;
        particleData.sizeInterval = 0f;
        particleData.sizeDuration = (cycle) ? fixedDuration : particleData.startlife / ((float) sizes.size() - 1 - particleData.sizeIndex);

        getNextSizeRange(particleData);

        particleData.sizeInterpolation = interpolations.getArray()[particleData.sizeIndex];
    }

    private void getNextSizeRange(@NotNull final ParticleData particleData) {
        if (particleData.sizeIndex == 0) { //endSize.equals(Vector3f.ZERO)) {
            particleData.startSize.set(sizes.getArray()[particleData.sizeIndex]);
            if (randomSize) {
                tempV3a.set(particleData.startSize);
                tempV3b.set(tempV3a).multLocal(randomSizeTolerance);
                tempV3a.subtractLocal(tempV3b);
                tempV3b.multLocal(FastMath.nextRandomFloat());
                tempV3a.addLocal(tempV3b);
                particleData.startSize.set(tempV3a);
            }
        } else {
            particleData.startSize.set(particleData.endSize);
        }

        if (sizes.size() > 1) {
            if (particleData.sizeIndex == sizes.size() - 1) {
                particleData.endSize.set(sizes.getArray()[0]);
            } else {
                particleData.endSize.set(sizes.getArray()[particleData.sizeIndex + 1]);
            }
            if (randomSize) {
                tempV3a.set(particleData.endSize);
                tempV3b.set(tempV3a).multLocal(randomSizeTolerance);
                tempV3a.subtractLocal(tempV3b);
                tempV3b.multLocal(FastMath.nextRandomFloat());
                tempV3a.addLocal(tempV3b);
                particleData.endSize.set(tempV3a);
            }
        } else {
            particleData.endSize.set(particleData.startSize);
        }

        particleData.size.set(particleData.startSize);
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
        particleData.size.set(0, 0, 0);
        particleData.startSize.set(0f, 0f, 0f);
        particleData.endSize.set(0, 0, 0);
    }

    public void addSize(final float size) {
        addSize(size, Interpolation.linear);
    }

    public void addSize(@NotNull final Vector3f size) {
        addSize(size, Interpolation.linear);
    }

    public void addSize(final float size, @NotNull final Interpolation interpolation) {
        addSize(new Vector3f(size, size, size), interpolation);
    }

    public void addSize(@NotNull final Vector3f size, @NotNull final Interpolation interpolation) {
        sizes.add(size.clone());
        interpolations.add(interpolation);
    }

    @NotNull
    public Vector3f[] getSizes() {
        return sizes.getArray();
    }

    @NotNull
    public Interpolation[] getInterpolations() {
        return interpolations.getArray();
    }

    public void removeSize(final int index) {
        sizes.remove(index);
        interpolations.remove(index);
    }

    public void removeAll() {
        sizes.clear();
        interpolations.clear();
    }

    public void setRandomSize(final boolean randomSize) {
        this.randomSize = randomSize;
    }

    public boolean isRandomSize() {
        return randomSize;
    }

    public void setRandomSizeTolerance(final float randomSizeTolerance) {
        this.randomSizeTolerance = randomSizeTolerance;
    }

    public float getRandomSizeTolerance() {
        return randomSizeTolerance;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.writeSavableArrayList(new ArrayList<>(sizes), "sizes", null);
        Map<String, Vector2f> interps = new HashMap<String, Vector2f>();
        int index = 0;
        for (Interpolation in : interpolations.getArray()) {
            interps.put(Interpolation.getInterpolationName(in) + ":" + String.valueOf(index), null);
            index++;
        }
        oc.writeStringSavableMap(interps, "interpolations", null);
        oc.write(randomSize, "randomSize", false);
        oc.write(randomSizeTolerance, "randomSizeTolerance", 0.5f);
        oc.write(fixedDuration, "fixedDuration", 0f);
        oc.write(enabled, "enabled", true);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        sizes.addAll(new SafeArrayList<>(Vector3f.class, ic.readSavableArrayList("sizes", null)));
        Map<String, Vector2f> interps = (Map<String, Vector2f>) ic.readStringSavableMap("interpolations", null);
        for (String in : interps.keySet()) {
            String name = in.substring(0, in.indexOf(":"));
            interpolations.add(Interpolation.getInterpolationByName(name));
        }
        randomSize = ic.readBoolean("randomSize", false);
        randomSizeTolerance = ic.readFloat("randomSizeTolerance", 0.5f);
        fixedDuration = ic.readFloat("fixedDuration", 0f);
        enabled = ic.readBoolean("enabled", true);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        try {
            SizeInfluencer clone = (SizeInfluencer) super.clone();
            clone.sizes.addAll(sizes);
            clone.interpolations.addAll(interpolations);
            clone.setFixedDuration(fixedDuration);
            clone.setRandomSizeTolerance(randomSizeTolerance);
            clone.setRandomSize(randomSize);
            clone.setEnabled(enabled);
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
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
