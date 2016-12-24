package tonegod.emitter.influencers;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import tonegod.emitter.particle.ParticleData;

/**
 * @author t0neg0d
 */
public class SpriteInfluencer implements ParticleInfluencer {

    private transient float targetInterval;

    private int[] frameSequence;

    private float fixedDuration;

    private int totalFrames;

    private boolean enabled;
    private boolean randomImage;
    private boolean animate;
    private boolean cycle;

    public SpriteInfluencer() {
        this.fixedDuration = 0f;
        this.totalFrames = -1;
        this.animate = true;
        this.enabled = true;
    }

    @NotNull
    @Override
    public String getName() {
        return "Sprite influencer";
    }

    @Override
    public void update(@NotNull final ParticleData particleData, final float tpf) {
        if (!enabled || !animate) return;

        particleData.spriteInterval += tpf;

        targetInterval = (cycle) ? fixedDuration : particleData.spriteDuration;

        if (particleData.spriteInterval >= targetInterval) {
            updateFrame(particleData);
        }
    }

    private void updateFrame(@NotNull final ParticleData particleData) {
        if (frameSequence == null) {

            particleData.spriteCol++;

            if (particleData.spriteCol == particleData.emitterNode.getSpriteColCount()) {
                particleData.spriteCol = 0;
                particleData.spriteRow++;

                if (particleData.spriteRow == particleData.emitterNode.getSpriteRowCount()) {
                    particleData.spriteRow = 0;
                }
            }

        } else {

            particleData.spriteIndex++;

            if (particleData.spriteIndex == frameSequence.length) {
                particleData.spriteIndex = 0;
            }

            particleData.spriteRow = (int) FastMath.floor(frameSequence[particleData.spriteIndex] / particleData.emitterNode.getSpriteRowCount()) - 2;
            particleData.spriteCol = (int) frameSequence[particleData.spriteIndex] % particleData.emitterNode.getSpriteColCount();
        }

        particleData.spriteInterval -= targetInterval;
    }

    @Override
    public void initialize(@NotNull final ParticleData particleData) {

        if (totalFrames == -1) {
            totalFrames = particleData.emitterNode.getSpriteColCount() * particleData.emitterNode.getSpriteRowCount();
            if (totalFrames == 1) setAnimate(false);
        }

        if (randomImage) {
            if (frameSequence == null) {
                particleData.spriteIndex = FastMath.nextRandomInt(0, totalFrames - 1);
                particleData.spriteRow = (int) FastMath.floor(particleData.spriteIndex / particleData.emitterNode.getSpriteRowCount()) - 1;
                particleData.spriteCol = (int) particleData.spriteIndex % particleData.emitterNode.getSpriteColCount();
            } else {
                particleData.spriteIndex = FastMath.nextRandomInt(0, frameSequence.length - 1);
                particleData.spriteRow = (int) FastMath.floor(frameSequence[particleData.spriteIndex] / particleData.emitterNode.getSpriteRowCount()) - 1;
                particleData.spriteCol = (int) frameSequence[particleData.spriteIndex] % particleData.emitterNode.getSpriteColCount();
            }
        } else {
            if (frameSequence != null) {
                particleData.spriteIndex = frameSequence[0];
                particleData.spriteRow = (int) FastMath.floor(frameSequence[particleData.spriteIndex] / particleData.emitterNode.getSpriteRowCount()) - 2;
                particleData.spriteCol = (int) frameSequence[particleData.spriteIndex] % particleData.emitterNode.getSpriteColCount();
            } else {
                particleData.spriteIndex = 0;
                particleData.spriteRow = 0;
                particleData.spriteCol = 0;
            }
        }

        if (!animate) return;

        particleData.spriteInterval = 0;

        if (cycle) return;

        if (frameSequence == null) {
            particleData.spriteDuration = particleData.startlife / (float) totalFrames;
        } else {
            particleData.spriteDuration = particleData.startlife / (float) frameSequence.length;
        }
    }

    @Override
    public void reset(@NotNull final ParticleData particleData) {
        particleData.spriteIndex = 0;
        particleData.spriteCol = 0;
        particleData.spriteRow = 0;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Particles will/will not use sprite animations
     *
     * @param animate boolean
     */
    public void setAnimate(final boolean animate) {
        this.animate = animate;
    }

    /**
     * Current animation state of particle
     *
     * @return Returns if particles use sprite animation
     */
    public boolean isAnimate() {
        return animate;
    }

    /**
     * Sets if particles should select a random start image from the provided sprite texture
     *
     * @param randomImage boolean
     */
    public void setRandomStartImage(final boolean randomImage) {
        this.randomImage = randomImage;
    }

    /**
     * Returns if particles currently select a random start image from the provided sprite texture
     */
    public boolean isRandomStartImage() {
        return randomImage;
    }

    public void setFrameSequence(final int... frame) {
        frameSequence = frame;
    }

    public int[] getFrameSequence() {
        return frameSequence;
    }

    public void clearFrameSequence() {
        frameSequence = null;
    }

    /**
     * Animated texture should cycle and use the provided duration between frames (0 diables
     * cycling)
     *
     * @param fixedDuration duration between frame updates
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
     * Returns the current duration used between frames for cycled animation
     */
    public float getFixedDuration() {
        return fixedDuration;
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(randomImage, "randomImage", false);
        capsule.write(animate, "animate", true);
        capsule.write(fixedDuration, "fixedDuration", 0f);
        capsule.write(enabled, "enabled", true);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        final InputCapsule capsule = importer.getCapsule(this);
        randomImage = capsule.readBoolean("randomImage", false);
        animate = capsule.readBoolean("animate", true);
        fixedDuration = capsule.readFloat("fixedDuration", 0f);
        enabled = capsule.readBoolean("enabled", true);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        try {
            final SpriteInfluencer clone = (SpriteInfluencer) super.clone();
            clone.setAnimate(animate);
            clone.setFixedDuration(fixedDuration);
            clone.setRandomStartImage(randomImage);
            clone.setFrameSequence(frameSequence);
            clone.setEnabled(enabled);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
