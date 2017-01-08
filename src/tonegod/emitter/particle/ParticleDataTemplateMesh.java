package tonegod.emitter.particle;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.util.BufferUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Objects;

import tonegod.emitter.BillboardMode;
import tonegod.emitter.EmitterMesh;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of particle data mesh to use like some template.
 *
 * @author t0neg0d
 * @edit JavaSaBr
 */
public final class ParticleDataTemplateMesh extends ParticleDataMesh {

    @NotNull
    private final Vector3f left;

    @NotNull
    private final Vector3f up;

    @NotNull
    private final Vector3f dir;

    @NotNull
    private final Vector3f tempV3;

    @NotNull
    private final Vector3f tempV1;

    @NotNull
    private final Vector3f lock;

    @NotNull
    private final Quaternion rotStore;

    @NotNull
    private final Matrix3f mat3;

    private Mesh template;

    private IndexBuffer templateIndexes;
    private ShortBuffer finIndexes;

    private FloatBuffer finVerts;
    private FloatBuffer finCoords;
    private FloatBuffer finNormals;
    private FloatBuffer finColors;

    private FloatBuffer templateVerts;
    private FloatBuffer templateCoords;
    private FloatBuffer templateNormals;
    private FloatBuffer templateColors;

    public ParticleDataTemplateMesh() {
        this.left = new Vector3f();
        this.up = new Vector3f();
        this.dir = new Vector3f();
        this.tempV3 = new Vector3f();
        this.tempV1 = new Vector3f();
        this.rotStore = new Quaternion();
        this.lock = new Vector3f(0, 0.99f, 0.01f);
        this.mat3 = new Matrix3f();
    }

    @Override
    public void extractTemplateFromMesh(@NotNull final Mesh mesh) {
        template = mesh;
        templateVerts = MeshUtils.getPositionBuffer(mesh);
        templateCoords = MeshUtils.getTexCoordBuffer(mesh);
        templateIndexes = MeshUtils.getIndexBuffer(mesh);
        templateNormals = MeshUtils.getNormalsBuffer(mesh);
        templateColors = BufferUtils.createFloatBuffer(templateVerts.capacity() / 3 * 4);
    }

    /**
     * Get a template mesh.
     *
     * @return the template mesh.
     */
    @NotNull
    public Mesh getTemplateMesh() {
        return Objects.requireNonNull(template);
    }

    @Override
    public void initParticleData(@NotNull final ParticleEmitterNode emitterNode, final int numParticles) {
        super.initParticleData(emitterNode, numParticles);

        setMode(Mode.Triangles);
        setUniqueTexCoords(false);

        this.finVerts = BufferUtils.createFloatBuffer(templateVerts.capacity() * numParticles);
        try {
            this.finCoords = BufferUtils.createFloatBuffer(templateCoords.capacity() * numParticles);
        } catch (final Exception e) {
            LOGGER.warning(this, e);
        }

        this.finIndexes = BufferUtils.createShortBuffer(templateIndexes.size() * numParticles);
        this.finNormals = BufferUtils.createFloatBuffer(templateNormals.capacity() * numParticles);
        this.finColors = BufferUtils.createFloatBuffer(templateVerts.capacity() / 3 * 4 * numParticles);

        int index = 0, index2 = 0, index3 = 0, index4 = 0, index5 = 0;
        int indexOffset = 0;

        for (int i = 0; i < numParticles; i++) {
            templateVerts.rewind();

            for (int v = 0; v < templateVerts.capacity(); v += 3) {
                tempV3.set(templateVerts.get(v), templateVerts.get(v + 1), templateVerts.get(v + 2));
                finVerts.put(index, tempV3.getX());
                index++;
                finVerts.put(index, tempV3.getY());
                index++;
                finVerts.put(index, tempV3.getZ());
                index++;
            }
            try {

                templateCoords.rewind();

                for (int v = 0; v < templateCoords.capacity(); v++) {
                    finCoords.put(index2, templateCoords.get(v));
                    index2++;
                }

            } catch (final Exception e) {
                LOGGER.warning(this, e);
            }

            for (int v = 0; v < templateIndexes.size(); v++) {
                finIndexes.put(index3, (short) (templateIndexes.get(v) + indexOffset));
                index3++;
            }

            indexOffset += templateVerts.capacity() / 3;

            templateNormals.rewind();

            for (int v = 0; v < templateNormals.capacity(); v++) {
                finNormals.put(index4, templateNormals.get(v));
                index4++;
            }

            for (int v = 0; v < finColors.capacity(); v++) {
                finColors.put(v, 1.0f);
            }
        }

        // Clear & ssign buffers
        clearBuffer(VertexBuffer.Type.Position);
        setBuffer(VertexBuffer.Type.Position, 3, finVerts);
        clearBuffer(VertexBuffer.Type.TexCoord);

        try {
            setBuffer(VertexBuffer.Type.TexCoord, 2, finCoords);
        } catch (final Exception e) {
            LOGGER.warning(this, e);
        }

        clearBuffer(VertexBuffer.Type.Index);
        setBuffer(VertexBuffer.Type.Index, 3, finIndexes);
        clearBuffer(VertexBuffer.Type.Normal);
        setBuffer(VertexBuffer.Type.Normal, 3, finNormals);
        clearBuffer(VertexBuffer.Type.Color);
        setBuffer(VertexBuffer.Type.Color, 4, finColors);
        updateBound();
    }

    @Override
    public void setImagesXY(final int imagesX, final int imagesY) {
        super.setImagesXY(imagesX, imagesY);
        if (imagesX != 1 || imagesY != 1) {
            final VertexBuffer buffer = getBuffer(VertexBuffer.Type.TexCoord);
            buffer.setUsage(Usage.Stream);
        }
    }

    @Override
    public void updateParticleData(@NotNull final ParticleData[] particles, @NotNull final Camera camera,
                                   @NotNull final Matrix3f inverseRotation) {

        final ParticleEmitterNode emitterNode = getEmitterNode();
        final EmitterMesh emitterShape = emitterNode.getEmitterShape();
        final BillboardMode billboardMode = emitterNode.getBillboardMode();
        final Vector3f worldTranslation = emitterNode.getWorldTranslation();

        for (int i = 0; i < particles.length; i++) {

            final ParticleData particleData = particles[i];
            final Vector3f velocity = particleData.velocity;

            int offset = templateVerts.capacity() * i;
            int colorOffset = templateColors.capacity() * i;

            if (particleData.life == 0 || !particleData.active) {
                for (int x = 0; x < templateVerts.capacity(); x += 3) {
                    finVerts.put(offset + x, 0);
                    finVerts.put(offset + x + 1, 0);
                    finVerts.put(offset + x + 2, 0);
                }
            } else {
                for (int x = 0; x < templateVerts.capacity(); x += 3) {
                    switch (billboardMode) {
                        case VELOCITY: {
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
                            if (isNotUnitY(velocity)) {
                                up.set(velocity).crossLocal(Vector3f.UNIT_Y).normalizeLocal();
                            } else {
                                up.set(velocity).crossLocal(lock).normalizeLocal();
                            }
                            left.set(velocity).crossLocal(up).normalizeLocal();
                            dir.set(velocity);
                            rotStore.fromAngleAxis(-90 * FastMath.DEG_TO_RAD, left);
                            left.set(rotStore.mult(left, tempV1));
                            up.set(rotStore.mult(up, tempV1));
                            break;
                        }
                        case VELOCITY_Z_UP_Y_LEFT: {
                            up.set(velocity).crossLocal(Vector3f.UNIT_Y).normalizeLocal();
                            left.set(velocity).crossLocal(up).normalizeLocal();
                            dir.set(velocity);
                            tempV3.set(left).crossLocal(up).normalizeLocal();
                            rotStore.fromAngleAxis(90 * FastMath.DEG_TO_RAD, velocity);
                            left.set(rotStore.mult(left, tempV1));
                            up.set(rotStore.mult(up, tempV1));
                            rotStore.fromAngleAxis(-90 * FastMath.DEG_TO_RAD, left);
                            up.set(rotStore.mult(up, tempV1));
                            break;
                        }
                        case NORMAL: {

                            emitterShape.setNext(particleData.triangleIndex);
                            tempV3.set(emitterShape.getNormal());

                            if (tempV3 == Vector3f.UNIT_Y) {
                                tempV3.set(velocity);
                            }

                            up.set(tempV3).crossLocal(Vector3f.UNIT_Y).normalizeLocal();
                            left.set(tempV3).crossLocal(up).normalizeLocal();
                            dir.set(tempV3);
                            break;
                        }
                        case NORMAL_Y_UP: {

                            emitterShape.setNext(particleData.triangleIndex);
                            tempV3.set(velocity);

                            if (tempV3 == Vector3f.UNIT_Y) {
                                tempV3.set(Vector3f.UNIT_X);
                            }

                            up.set(Vector3f.UNIT_Y);
                            left.set(tempV3).crossLocal(up).normalizeLocal();
                            dir.set(tempV3);
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

                    tempV3.set(templateVerts.get(x), templateVerts.get(x + 1), templateVerts.get(x + 2));
                    tempV3.set(rotStore.mult(tempV3, tempV1));
                    tempV3.multLocal(particleData.size);

                    rotStore.fromAngles(particleData.angles.x, particleData.angles.y, particleData.angles.z);
                    tempV3.set(rotStore.mult(tempV3, tempV1));

                    tempV3.addLocal(particleData.position);

                    if (!emitterNode.isParticlesFollowEmitter()) {
                        tempV3.subtractLocal(worldTranslation.subtract(particleData.initialPosition, tempV1));//.divide(8f));
                    }

                    finVerts.put(offset + x, tempV3.getX());
                    finVerts.put(offset + x + 1, tempV3.getY());
                    finVerts.put(offset + x + 2, tempV3.getZ());
                }
            }

            if (emitterNode.isApplyLightingTransform()) {
                for (int v = 0; v < templateNormals.capacity(); v += 3) {

                    tempV3.set(templateNormals.get(v), templateNormals.get(v + 1), templateNormals.get(v + 2));
                    rotStore.fromAngles(particleData.angles.x, particleData.angles.y, particleData.angles.z);
                    rotStore.toRotationMatrix(mat3);

                    float vx = tempV3.x, vy = tempV3.y, vz = tempV3.z;

                    tempV3.x = mat3.get(0, 0) * vx + mat3.get(0, 1) * vy + mat3.get(0, 2) * vz;
                    tempV3.y = mat3.get(1, 0) * vx + mat3.get(1, 1) * vy + mat3.get(1, 2) * vz;
                    tempV3.z = mat3.get(2, 0) * vx + mat3.get(2, 1) * vy + mat3.get(2, 2) * vz;

                    finNormals.put(offset + v, tempV3.getX());
                    finNormals.put(offset + v + 1, tempV3.getY());
                    finNormals.put(offset + v + 2, tempV3.getZ());
                }
            }

            for (int v = 0; v < templateColors.capacity(); v += 4) {
                finColors.put(colorOffset + v, particleData.color.r)
                        .put(colorOffset + v + 1, particleData.color.g)
                        .put(colorOffset + v + 2, particleData.color.b)
                        .put(colorOffset + v + 3, particleData.color.a * particleData.alpha);
            }
        }

        setBuffer(VertexBuffer.Type.Position, 3, finVerts);

        if (emitterNode.isApplyLightingTransform()) {
            setBuffer(VertexBuffer.Type.Normal, 3, finNormals);
        }

        setBuffer(VertexBuffer.Type.Color, 4, finColors);
        updateBound();
    }
}