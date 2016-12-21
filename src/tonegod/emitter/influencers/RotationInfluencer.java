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

import tonegod.emitter.interpolation.Interpolation;
import tonegod.emitter.interpolation.InterpolationManager;
import tonegod.emitter.particle.ParticleData;

/**
 * @author t0neg0d
 */
public class RotationInfluencer implements ParticleInfluencer {
    private SafeArrayList<Vector3f> speeds = new SafeArrayList(Vector3f.class);
    private SafeArrayList<Interpolation> interpolations = new SafeArrayList(Interpolation.class);
    private boolean initialized = false;
    private boolean enabled = true;
    private boolean cycle = false;

    private Vector3f speedFactor = Vector3f.ZERO.clone();
    private boolean useRandomDirection = true;
    private boolean useRandomSpeed = true;
    private boolean direction = true;

    private boolean useRandomStartRotationX = false;
    private boolean useRandomStartRotationY = false;
    private boolean useRandomStartRotationZ = false;
    private float blend;
    private float fixedDuration = 0f;

    private Vector3f startRotation = new Vector3f();
    private Vector3f endRotation = new Vector3f();

    @NotNull
    @Override
    public String getName() {
        return "Rotation influencer";
    }

    @Override
    public void update(@NotNull ParticleData particleData, float tpf) {
        if (enabled) {
            if (speeds.size() > 1) {
                particleData.rotationInterval += tpf;
                if (particleData.rotationInterval >= particleData.rotationDuration)
                    updateRotation(particleData);

                blend = particleData.rotationInterpolation.apply(particleData.rotationInterval / particleData.rotationDuration);

                particleData.rotationSpeed.interpolateLocal(particleData.startRotationSpeed, particleData.endRotationSpeed, blend);
            }
            particleData.angles.addLocal(particleData.rotationSpeed.mult(tpf));
        }
    }

    private void updateRotation(ParticleData p) {
        p.rotationIndex++;
        if (!cycle) {
            if (p.rotationIndex == speeds.size() - 1)
                p.rotationIndex = 0;
        } else {
            if (p.rotationIndex == speeds.size())
                p.rotationIndex = 0;
        }

        getRotationSpeed(p, p.rotationIndex, p.startRotationSpeed);

        int index = p.rotationIndex + 1;
        if (index == speeds.size())
            index = 0;

        getRotationSpeed(p, index, p.endRotationSpeed);

        p.rotationInterpolation = interpolations.getArray()[p.rotationIndex];
        p.rotationInterval -= p.rotationDuration;
    }

    @Override
    public void initialize(@NotNull ParticleData particleData) {
        if (!initialized) {
            if (speeds.isEmpty()) {
                addRotationSpeed(new Vector3f(0, 0, 10));
            }
            initialized = true;
        }
        particleData.rotationIndex = 0;
        particleData.rotationInterval = 0f;
        particleData.rotationDuration = (cycle) ? fixedDuration : particleData.startlife / ((float) speeds.size() - 1);

        if (useRandomDirection) {
            particleData.rotateDirectionX = FastMath.rand.nextBoolean();
            particleData.rotateDirectionY = FastMath.rand.nextBoolean();
            particleData.rotateDirectionZ = FastMath.rand.nextBoolean();
        }

        getRotationSpeed(particleData, particleData.rotationIndex, particleData.startRotationSpeed);
        particleData.rotationSpeed.set(particleData.startRotationSpeed);
        if (speeds.size() > 1) {
            getRotationSpeed(particleData, particleData.rotationIndex + 1, particleData.endRotationSpeed);
        }

        particleData.rotationInterpolation = interpolations.getArray()[particleData.rotationIndex];

        if (useRandomStartRotationX || useRandomStartRotationY || useRandomStartRotationZ) {
            particleData.angles.set(
                    useRandomStartRotationX ? FastMath.nextRandomFloat() * FastMath.TWO_PI : 0,
                    useRandomStartRotationY ? FastMath.nextRandomFloat() * FastMath.TWO_PI : 0,
                    useRandomStartRotationZ ? FastMath.nextRandomFloat() * FastMath.TWO_PI : 0
            );
        } else {
            particleData.angles.set(0, 0, 0);
        }
    }

    private void getRotationSpeed(ParticleData p, int index, Vector3f store) {
        store.set(speeds.getArray()[index]);
        if (useRandomSpeed) {
            store.set(
                    FastMath.nextRandomFloat() * store.x,
                    FastMath.nextRandomFloat() * store.y,
                    FastMath.nextRandomFloat() * store.z
            );
        }
        if (useRandomDirection) {
            store.x = p.rotateDirectionX ? store.x : -store.x;
            store.y = p.rotateDirectionY ? store.y : -store.y;
            store.z = p.rotateDirectionZ ? store.z : -store.z;
        }
    }

    @Override
    public void reset(@NotNull ParticleData particleData) {
        particleData.angles.set(0, 0, 0);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void addRotationSpeed(Vector3f speeds) {
        addRotationSpeed(speeds, Interpolation.LINEAR);
    }

    public void addRotationSpeed(Vector3f speed, Interpolation interpolation) {
        this.speeds.add(speed.clone());
        this.interpolations.add(interpolation);
    }

    public void removeRotation(int index) {
        this.speeds.remove(index);
        this.interpolations.remove(index);
    }

    public void removeAll() {
        this.speeds.clear();
        this.interpolations.clear();
    }

    public Vector3f[] getRotations() {
        return this.speeds.getArray();
    }

    public Interpolation[] getInterpolations() {
        return this.interpolations.getArray();
    }

    /**
     * Allows the influencer to choose a random rotation direction per axis as the particle is
     * emitted.
     *
     * @param useRandomDirection boolean
     */
    public void setUseRandomDirection(boolean useRandomDirection) {
        this.useRandomDirection = useRandomDirection;
    }

    /**
     * Returns if the influencer currently selects a random rotation direction per axis as the
     * particle is emitted.
     *
     * @return boolean
     */
    public boolean getUseRandomDirection() {
        return this.useRandomDirection;
    }

    /**
     * Allows the influencer to select a random rotation speed from 0 to the provided maximum speeds
     * per axis
     *
     * @param useRandomSpeed boolean
     */
    public void setUseRandomSpeed(boolean useRandomSpeed) {
        this.useRandomSpeed = useRandomSpeed;
    }

    /**
     * Returns if the influencer currently to selects random rotation speeds per axis
     *
     * @return boolean
     */
    public boolean getUseRandomSpeed() {
        return this.useRandomSpeed;
    }

    public void setUseRandomStartRotation(boolean xRotation, boolean yRotation, boolean zRotation) {
        useRandomStartRotationX = xRotation;
        useRandomStartRotationY = yRotation;
        useRandomStartRotationZ = zRotation;
    }

    public boolean getUseRandomStartRotationX() {
        return this.useRandomStartRotationX;
    }

    public boolean getUseRandomStartRotationY() {
        return this.useRandomStartRotationY;
    }

    public boolean getUseRandomStartRotationZ() {
        return this.useRandomStartRotationZ;
    }

    /**
     * Forces the rotation direction to always remain constant per particle
     *
     * @param direction boolean
     */
    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    /**
     * Returns if the rotation direction will always remain constant per particle
     */
    public boolean getDirection() {
        return this.direction;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.writeSavableArrayList(new ArrayList(speeds), "speeds", null);
        Map<String, Vector2f> interps = new HashMap<String, Vector2f>();
        int index = 0;
        for (Interpolation in : interpolations.getArray()) {
            interps.put(in.getName() + ":" + String.valueOf(index), null);
            index++;
        }
        oc.writeStringSavableMap(interps, "interpolations", null);
        oc.write(speedFactor, "speedFactor", Vector3f.ZERO);
        oc.write(useRandomDirection, "useRandomDirection", true);
        oc.write(useRandomSpeed, "useRandomSpeed", true);
        oc.write(direction, "direction", true);
        oc.write(useRandomStartRotationX, "useRandomStartRotationX", false);
        oc.write(useRandomStartRotationY, "useRandomStartRotationY", false);
        oc.write(useRandomStartRotationZ, "useRandomStartRotationZ", false);
        oc.write(fixedDuration, "fixedDuration", 0f);
        oc.write(enabled, "enabled", true);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        speeds = new SafeArrayList<Vector3f>(Vector3f.class, ic.readSavableArrayList("speeds", null));
        Map<String, Vector2f> interps = (Map<String, Vector2f>) ic.readStringSavableMap("interpolations", null);
        for (String in : interps.keySet()) {
            String name = in.substring(0, in.indexOf(":"));
            interpolations.add(InterpolationManager.getInterpolation(name));
        }
        speedFactor = (Vector3f) ic.readSavable("speedFactor", Vector3f.ZERO.clone());
        useRandomDirection = ic.readBoolean("useRandomDirection", true);
        useRandomSpeed = ic.readBoolean("useRandomSpeed", true);
        direction = ic.readBoolean("direction", true);
        useRandomStartRotationX = ic.readBoolean("useRandomStartRotationX", false);
        useRandomStartRotationY = ic.readBoolean("useRandomStartRotationY", false);
        useRandomStartRotationZ = ic.readBoolean("useRandomStartRotationZ", false);
        fixedDuration = ic.readFloat("fixedDuration", 0f);
        enabled = ic.readBoolean("enabled", true);
    }

    @NotNull
    @Override
    public ParticleInfluencer clone() {
        try {
            RotationInfluencer clone = (RotationInfluencer) super.clone();
            clone.setDirection(direction);
            clone.setUseRandomDirection(useRandomDirection);
            clone.setUseRandomSpeed(useRandomSpeed);
            clone.setUseRandomStartRotation(useRandomStartRotationX, useRandomStartRotationY, useRandomStartRotationZ);
            clone.setEnabled(enabled);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
