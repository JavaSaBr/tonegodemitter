package tonegod.emitter.particle;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Format;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.util.BufferUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import tonegod.emitter.EmitterMesh;
import tonegod.emitter.ParticleEmitterNode;

/**
 * @author t0neg0d
 */
public final class ParticleDataTriMesh extends ParticleDataMesh {

    private Vector3f left;
    private Vector3f up;
    private Vector3f dir;
    private Vector3f tempV3;
    private Vector3f lock;

    private Quaternion rotStore;
    private Quaternion tempQ;

    private ColorRGBA tempC;

    private int imgX;
    private int imgY;

    private float startX;
    private float startY;
    private float endX;
    private float endY;

    public ParticleDataTriMesh() {
        this.left = new Vector3f();
        this.up = new Vector3f();
        this.dir = new Vector3f();
        this.tempV3 = new Vector3f();
        this.rotStore = new Quaternion();
        this.tempQ = new Quaternion();
        this.tempC = new ColorRGBA();
        this.lock = new Vector3f(0, 0.99f, 0.01f);
    }

    @Override
    public void initParticleData(@NotNull final ParticleEmitterNode emitterNode, int numParticles) {
        super.initParticleData(emitterNode, numParticles);

        setUniqueTexCoords(false);
        setMode(Mode.Triangles);

        // set positions
        FloatBuffer posBuffer = BufferUtils.createVector3Buffer(numParticles * 4);

        // if the buffer is already set only update the data
        VertexBuffer buf = getBuffer(VertexBuffer.Type.Position);

        if (buf != null) {
            buf.updateData(posBuffer);
        } else {
            final VertexBuffer pvb = new VertexBuffer(VertexBuffer.Type.Position);
            pvb.setupData(Usage.Stream, 3, Format.Float, posBuffer);
            setBuffer(pvb);
        }

        // set colors
        ByteBuffer cb = BufferUtils.createByteBuffer(numParticles * 4 * 4);

        buf = getBuffer(VertexBuffer.Type.Color);

        if (buf != null) {
            buf.updateData(cb);
        } else {
            final VertexBuffer cvb = new VertexBuffer(VertexBuffer.Type.Color);
            cvb.setupData(Usage.Stream, 4, Format.UnsignedByte, cb);
            cvb.setNormalized(true);
            setBuffer(cvb);
        }

        // set texcoords
        FloatBuffer tb = BufferUtils.createVector2Buffer(numParticles * 4);

        for (int i = 0; i < numParticles; i++) {
            tb.put(0f).put(1f);
            tb.put(1f).put(1f);
            tb.put(0f).put(0f);
            tb.put(1f).put(0f);
        }

        tb.flip();

        buf = getBuffer(VertexBuffer.Type.TexCoord);

        if (buf != null) {
            buf.updateData(tb);
        } else {
            final VertexBuffer tvb = new VertexBuffer(VertexBuffer.Type.TexCoord);
            tvb.setupData(Usage.Static, 2, Format.Float, tb);
            setBuffer(tvb);
        }

        // set indices
        ShortBuffer ib = BufferUtils.createShortBuffer(numParticles * 6);

        for (int i = 0; i < numParticles; i++) {

            final int startIdx = (i * 4);

            // triangle 1
            ib.put((short) (startIdx + 1))
                    .put((short) (startIdx + 0))
                    .put((short) (startIdx + 2));

            // triangle 2
            ib.put((short) (startIdx + 1))
                    .put((short) (startIdx + 2))
                    .put((short) (startIdx + 3));
        }

        ib.flip();

        buf = getBuffer(VertexBuffer.Type.Index);

        if (buf != null) {
            buf.updateData(ib);
        } else {
            final VertexBuffer ivb = new VertexBuffer(VertexBuffer.Type.Index);
            ivb.setupData(Usage.Static, 3, Format.UnsignedShort, ib);
            setBuffer(ivb);
        }

        updateCounts();
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
        final Vector3f worldTranslation = emitterNode.getWorldTranslation();

        VertexBuffer pvb = getBuffer(VertexBuffer.Type.Position);
        FloatBuffer positions = (FloatBuffer) pvb.getData();

        VertexBuffer cvb = getBuffer(VertexBuffer.Type.Color);
        ByteBuffer colors = (ByteBuffer) cvb.getData();

        VertexBuffer tvb = getBuffer(VertexBuffer.Type.TexCoord);
        FloatBuffer texcoords = (FloatBuffer) tvb.getData();

        // update data in vertex buffers
        positions.clear();
        colors.clear();
        texcoords.clear();

        for (final ParticleData particleData : particles) {
            if (particleData.life == 0 || !particleData.isActive()) {
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
            } else {

                final Vector3f velocity = particleData.getVelocity();

                switch (emitterNode.getBillboardMode()) {
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

                        rotStore = tempQ.fromAngleAxis(-90 * FastMath.DEG_TO_RAD, left);

                        left = rotStore.mult(left);
                        up = rotStore.mult(up);
                        break;
                    }
                    case VELOCITY_Z_UP_Y_LEFT: {
                        up.set(velocity).crossLocal(Vector3f.UNIT_Y).normalizeLocal();
                        left.set(velocity).crossLocal(up).normalizeLocal();
                        dir.set(velocity);
                        tempV3.set(left).crossLocal(up).normalizeLocal();
                        rotStore = tempQ.fromAngleAxis(90 * FastMath.DEG_TO_RAD, velocity);
                        left = rotStore.mult(left);
                        up = rotStore.mult(up);
                        rotStore = tempQ.fromAngleAxis(-90 * FastMath.DEG_TO_RAD, left);
                        up = rotStore.mult(up);
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
                        up.set(camera.getUp());
                        left.set(camera.getLeft());
                        dir.set(camera.getDirection());
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

                particleData.upVec.set(up);

                if (emitterNode.isVelocityStretching()) {
                    up.multLocal(velocity.length() * emitterNode.getVelocityStretchFactor());
                }

                up.multLocal(particleData.size.y);
                left.multLocal(particleData.size.x);

                rotStore = tempQ.fromAngleAxis(particleData.angles.y, left);
                left = rotStore.mult(left);
                up = rotStore.mult(up);

                rotStore = tempQ.fromAngleAxis(particleData.angles.x, up);
                left = rotStore.mult(left);
                up = rotStore.mult(up);

                rotStore = tempQ.fromAngleAxis(particleData.angles.z, dir);
                left = rotStore.mult(left);
                up = rotStore.mult(up);

                if (emitterNode.isParticlesFollowEmitter()) {
                    tempV3.set(particleData.position);
                } else {
                    tempV3.set(particleData.position)
                            .subtractLocal(worldTranslation.subtract(particleData.initialPosition));
                }

                positions.put(tempV3.x + left.x + up.x)
                        .put(tempV3.y + left.y + up.y)
                        .put(tempV3.z + left.z + up.z);

                positions.put(tempV3.x - left.x + up.x)
                        .put(tempV3.y - left.y + up.y)
                        .put(tempV3.z - left.z + up.z);

                positions.put(tempV3.x + left.x - up.x)
                        .put(tempV3.y + left.y - up.y)
                        .put(tempV3.z + left.z - up.z);

                positions.put(tempV3.x - left.x - up.x)
                        .put(tempV3.y - left.y - up.y)
                        .put(tempV3.z - left.z - up.z);
            }

            if (isUniqueTexCoords()) {

                startX = 1f / emitterNode.getSpriteColCount() * particleData.spriteCol;
                startY = 1f / emitterNode.getSpriteRowCount() * particleData.spriteRow;

                endX = startX + 1f / emitterNode.getSpriteColCount();
                endY = startY + 1f / emitterNode.getSpriteRowCount();

                texcoords.put(startX).put(endY);
                texcoords.put(endX).put(endY);
                texcoords.put(startX).put(startY);
                texcoords.put(endX).put(startY);
            }

            tempC.set(particleData.color);
            tempC.a *= particleData.alpha;

            int abgr = tempC.asIntABGR();
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
        }

        //	this.setBuffer(VertexBuffer.Type.Position, 3, positions);
        positions.clear();
        colors.clear();

        if (!isUniqueTexCoords()) {
            texcoords.clear();
        } else {
            texcoords.clear();
            tvb.updateData(texcoords);
        }

        // force renderer to re-send data to GPU
        pvb.updateData(positions);
        cvb.updateData(colors);

        updateBound();
    }
}