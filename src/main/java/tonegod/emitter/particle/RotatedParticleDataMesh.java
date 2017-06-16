package tonegod.emitter.particle;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.util.clone.Cloner;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.BillboardMode;
import tonegod.emitter.EmitterMesh;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The rotated particle data mesh.
 *
 * @author JavaSaBr
 */
public abstract class RotatedParticleDataMesh extends ParticleDataMesh {

    /**
     * The left vector.
     */
    @NotNull
    protected Vector3f left;

    /**
     * The up vector.
     */
    @NotNull
    protected Vector3f up;

    /**
     * The direction.
     */
    @NotNull
    protected Vector3f dir;

    /**
     * The Lock.
     */
    @NotNull
    protected Vector3f lock;

    /**
     * The Temp v 1.
     */
    @NotNull
    protected Vector3f tempV1;

    /**
     * The Temp v 2.
     */
    @NotNull
    protected Vector3f tempV2;

    /**
     * The Rot store.
     */
    @NotNull
    protected Quaternion rotStore;

    /**
     * Instantiates a new Rotated particle data mesh.
     */
    public RotatedParticleDataMesh() {
        this.left = new Vector3f();
        this.up = new Vector3f();
        this.dir = new Vector3f();
        this.tempV1 = new Vector3f();
        this.tempV2 = new Vector3f();
        this.rotStore = new Quaternion();
        this.lock = new Vector3f(0, 0.99f, 0.01f);
    }

    /**
     * Update rotation of a particle.
     *
     * @param particleData  the particle data.
     * @param billboardMode the billboard mode.
     * @param camera        the camera.
     */
    protected void updateRotation(@NotNull final ParticleData particleData, @NotNull final BillboardMode billboardMode,
                                  @NotNull final Camera camera) {

        switch (billboardMode) {
            case VELOCITY: {

                final Vector3f velocity = particleData.getVelocity();

                if (isNotUnitY(velocity)) {
                    up.set(velocity).crossLocal(Vector3f.UNIT_Y).normalizeLocal();
                } else {
                    up.set(velocity).crossLocal(lock).normalizeLocal();
                }

                left.set(velocity).crossLocal(up).normalizeLocal();
                dir.set(velocity);
                break;
            }
            case VELOCITY_Z_UP: {

                final Vector3f velocity = particleData.getVelocity();

                if (isNotUnitY(velocity)) {
                    up.set(velocity).crossLocal(Vector3f.UNIT_Y).normalizeLocal();
                } else {
                    up.set(velocity).crossLocal(lock).normalizeLocal();
                }

                left.set(velocity).crossLocal(up).normalizeLocal();
                dir.set(velocity);

                rotStore.fromAngleAxis(-90 * FastMath.DEG_TO_RAD, left);

                left.set(rotStore.mult(left, tempV2));
                up.set(rotStore.mult(up, tempV2));
                break;
            }
            case VELOCITY_Z_UP_Y_LEFT: {

                final Vector3f velocity = particleData.getVelocity();

                up.set(velocity).crossLocal(Vector3f.UNIT_Y).normalizeLocal();
                left.set(velocity).crossLocal(up).normalizeLocal();
                dir.set(velocity);

                tempV1.set(left).crossLocal(up).normalizeLocal();
                rotStore.fromAngleAxis(90 * FastMath.DEG_TO_RAD, velocity);
                left.set(rotStore.mult(left, tempV2));
                up.set(rotStore.mult(up, tempV2));
                rotStore.fromAngleAxis(-90 * FastMath.DEG_TO_RAD, left);
                up.set(rotStore.mult(up, tempV2));
                break;
            }
            case NORMAL: {

                final ParticleEmitterNode emitterNode = getEmitterNode();
                final EmitterMesh emitterShape = emitterNode.getEmitterShape();
                emitterShape.setNext(particleData.triangleIndex);

                tempV1.set(emitterShape.getNormal());

                if (Vector3f.UNIT_Y.equals(tempV1)) {
                    tempV1.set(particleData.getVelocity());
                }

                up.set(tempV1).crossLocal(Vector3f.UNIT_Y).normalizeLocal();
                left.set(tempV1).crossLocal(up).normalizeLocal();
                dir.set(tempV1);
                break;
            }
            case NORMAL_Y_UP: {

                final ParticleEmitterNode emitterNode = getEmitterNode();
                final EmitterMesh emitterShape = emitterNode.getEmitterShape();
                emitterShape.setNext(particleData.triangleIndex);

                tempV1.set(particleData.getVelocity());

                if (Vector3f.UNIT_Y.equals(tempV1)) {
                    tempV1.set(Vector3f.UNIT_X);
                }

                up.set(Vector3f.UNIT_Y);
                left.set(tempV1).crossLocal(up).normalizeLocal();
                dir.set(tempV1);
                break;
            }
            case CAMERA: {
                camera.getUp(up);
                camera.getLeft(left);
                camera.getDirection(dir);
                break;
            }
            case UNIT_X: {
                up.set(Vector3f.UNIT_Y);
                left.set(Vector3f.UNIT_Z);
                dir.set(Vector3f.UNIT_X);
                break;
            }
            case UNIT_Y: {
                up.set(Vector3f.UNIT_Z);
                left.set(Vector3f.UNIT_X);
                dir.set(Vector3f.UNIT_Y);
                break;
            }
            case UNIT_Z: {
                up.set(Vector3f.UNIT_X);
                left.set(Vector3f.UNIT_Y);
                dir.set(Vector3f.UNIT_Z);
                break;
            }
        }
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
        super.cloneFields(cloner, original);

        left = cloner.clone(left);
        up = cloner.clone(up);
        dir = cloner.clone(dir);
        lock = cloner.clone(lock);
        tempV1 = cloner.clone(tempV1);
        tempV2 = cloner.clone(tempV2);
        rotStore = cloner.clone(rotStore);
    }
}
