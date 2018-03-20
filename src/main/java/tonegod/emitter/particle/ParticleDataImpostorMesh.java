package tonegod.emitter.particle;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Format;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.util.BufferUtils;
import com.jme3.util.clone.Cloner;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.BillboardMode;
import tonegod.emitter.ParticleEmitterNode;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * The type Particle data impostor mesh.
 *
 * @author t0neg0d, JavaSaBr
 */
public class ParticleDataImpostorMesh extends RotatedParticleDataMesh {

    private Vector3f left33;
    private Vector3f left66;
    private Vector3f temp1V3;
    private Vector3f temp2V3;
    private Vector3f temp3V3;
    private Vector3f temp4V3;
    private Vector3f temp1aV3;
    private Vector3f temp2aV3;
    private Vector3f temp3aV3;
    private Vector3f temp4aV3;
    private Vector3f temp1bV3;
    private Vector3f temp2bV3;
    private Vector3f temp3bV3;
    private Vector3f temp4bV3;

    private Quaternion q33;

    public ParticleDataImpostorMesh() {
        left33 = new Vector3f();
        left66 = new Vector3f();
        tempV1 = new Vector3f();
        temp1V3 = new Vector3f();
        temp2V3 = new Vector3f();
        temp3V3 = new Vector3f();
        temp4V3 = new Vector3f();
        temp1aV3 = new Vector3f();
        temp2aV3 = new Vector3f();
        temp3aV3 = new Vector3f();
        temp4aV3 = new Vector3f();
        temp1bV3 = new Vector3f();
        temp2bV3 = new Vector3f();
        temp3bV3 = new Vector3f();
        temp4bV3 = new Vector3f();
        q33 = new Quaternion();
        lock = new Vector3f(0, 0.99f, 0.01f);
    }

    @Override
    public void initialize(@NotNull final ParticleEmitterNode emitterNode, final int numParticles) {
        super.initialize(emitterNode, numParticles);

        setMode(Mode.Triangles);
        setUniqueTexCoords(false);
        preparePositionBuffer(numParticles * 12);
        prepareColorBuffer(numParticles * 12 * 4);

        // set texcoords
        FloatBuffer tb = BufferUtils.createVector2Buffer(numParticles * 12);
        for (int i = 0; i < numParticles; i++) {
            tb.put(0f).put(1f);
            tb.put(1f).put(1f);
            tb.put(0f).put(0f);
            tb.put(1f).put(0f);
            tb.put(0f).put(1f);
            tb.put(1f).put(1f);
            tb.put(0f).put(0f);
            tb.put(1f).put(0f);
            tb.put(0f).put(1f);
            tb.put(1f).put(1f);
            tb.put(0f).put(0f);
            tb.put(1f).put(0f);
        }
        tb.flip();

        VertexBuffer buf = getBuffer(VertexBuffer.Type.TexCoord);

        if (buf != null) {
            buf.updateData(tb);
        } else {
            VertexBuffer tvb = new VertexBuffer(VertexBuffer.Type.TexCoord);
            tvb.setupData(Usage.Static, 2, Format.Float, tb);
            setBuffer(tvb);
        }

        // set indices
        ShortBuffer ib = BufferUtils.createShortBuffer(numParticles * 18);

        for (int i = 0; i < numParticles; i++) {
            int startIdx = (i * 12);

            // triangle 1
            ib.put((short) (startIdx + 1))
                    .put((short) (startIdx + 0))
                    .put((short) (startIdx + 2));

            // triangle 2
            ib.put((short) (startIdx + 1))
                    .put((short) (startIdx + 2))
                    .put((short) (startIdx + 3));

            // triangle 3
            ib.put((short) (startIdx + 5))
                    .put((short) (startIdx + 4))
                    .put((short) (startIdx + 6));

            // triangle 4
            ib.put((short) (startIdx + 5))
                    .put((short) (startIdx + 6))
                    .put((short) (startIdx + 7));

            // triangle 5
            ib.put((short) (startIdx + 9))
                    .put((short) (startIdx + 8))
                    .put((short) (startIdx + 10));

            // triangle 6
            ib.put((short) (startIdx + 9))
                    .put((short) (startIdx + 10))
                    .put((short) (startIdx + 11));
        }
        ib.flip();

        buf = getBuffer(VertexBuffer.Type.Index);

        if (buf != null) {
            buf.updateData(ib);
        } else {
            VertexBuffer ivb = new VertexBuffer(VertexBuffer.Type.Index);
            ivb.setupData(Usage.Static, 3, Format.UnsignedShort, ib);
            setBuffer(ivb);
        }

        updateCounts();

        q33.fromAngleAxis(33f * 2f * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
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
        final Vector3f worldTranslation = emitterNode.getWorldTranslation();
        final BillboardMode billboardMode = emitterNode.getBillboardMode();

        final VertexBuffer pvb = getBuffer(VertexBuffer.Type.Position);
        final FloatBuffer positions = (FloatBuffer) pvb.getData();

        final VertexBuffer cvb = getBuffer(VertexBuffer.Type.Color);
        final ByteBuffer colors = (ByteBuffer) cvb.getData();

        final VertexBuffer tvb = getBuffer(VertexBuffer.Type.TexCoord);
        final FloatBuffer texcoords = (FloatBuffer) tvb.getData();

        // update data in vertex buffers
        positions.clear();
        colors.clear();
        texcoords.clear();

        for (final ParticleData particleData : particles) {

            if (particleData.life == 0 || !particleData.active) {
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
            } else {

                updateRotation(particleData, billboardMode, camera);

                particleData.upVec.set(up);

                if (emitterNode.isVelocityStretching()) {
                    final Vector3f velocity = particleData.getVelocity();
                    up.multLocal(velocity.length() * emitterNode.getVelocityStretchFactor());
                }

                up.multLocal(particleData.size.y);
                left.multLocal(particleData.size.x);

                rotStore.fromAngleAxis(particleData.angles.y, left);
                left.set(rotStore.mult(left, tempV1));
                up.set(rotStore.mult(up, tempV1));

                rotStore.fromAngleAxis(particleData.angles.x, up);
                left.set(rotStore.mult(left, tempV1));
                up.set(rotStore.mult(up, tempV1));

                rotStore.fromAngleAxis(particleData.angles.z, dir);
                left.set(rotStore.mult(left, tempV1));
                up.set(rotStore.mult(up, tempV1));

                if (emitterNode.isParticlesFollowEmitter()) {
                    tempV2.set(particleData.position);
                } else {

                    final Vector3f subtract = worldTranslation
                            .subtract(particleData.initialPosition, tempV1);

                    tempV2.set(particleData.position)
                            .subtractLocal(subtract);//.divide(8f));
                }

                q33.fromAngleAxis(33f * 2f * FastMath.DEG_TO_RAD, up);
                left33.set(q33.mult(left, tempV1));
                left66.set(q33.mult(left33, tempV1));

                temp1V3.set(tempV2.x + left.x + up.x, tempV2.y + left.y + up.y, tempV2.z + left.z + up.z);
                temp2V3.set(tempV2.x - left.x + up.x, tempV2.y - left.y + up.y, tempV2.z - left.z + up.z);
                temp3V3.set(tempV2.x + left.x - up.x, tempV2.y + left.y - up.y, tempV2.z + left.z - up.z);
                temp4V3.set(tempV2.x - left.x - up.x, tempV2.y - left.y - up.y, tempV2.z - left.z - up.z);
                temp1aV3.set(tempV2.x + left33.x + up.x, tempV2.y + left33.y + up.y, tempV2.z + left33.z + up.z);
                temp2aV3.set(tempV2.x - left33.x + up.x, tempV2.y - left33.y + up.y, tempV2.z - left33.z + up.z);
                temp3aV3.set(tempV2.x + left33.x - up.x, tempV2.y + left33.y - up.y, tempV2.z + left33.z - up.z);
                temp4aV3.set(tempV2.x - left33.x - up.x, tempV2.y - left33.y - up.y, tempV2.z - left33.z - up.z);
                temp1bV3.set(tempV2.x + left66.x + up.x, tempV2.y + left66.y + up.y, tempV2.z + left66.z + up.z);
                temp2bV3.set(tempV2.x - left66.x + up.x, tempV2.y - left66.y + up.y, tempV2.z - left66.z + up.z);
                temp3bV3.set(tempV2.x + left66.x - up.x, tempV2.y + left66.y - up.y, tempV2.z + left66.z - up.z);
                temp4bV3.set(tempV2.x - left66.x - up.x, tempV2.y - left66.y - up.y, tempV2.z - left66.z - up.z);

                // Face 1
                positions.put(temp1V3.x)
                        .put(temp1V3.y)
                        .put(temp1V3.z);
                positions.put(temp2V3.x)
                        .put(temp2V3.y)
                        .put(temp2V3.z);
                positions.put(temp3V3.x)
                        .put(temp3V3.y)
                        .put(temp3V3.z);
                positions.put(temp4V3.x)
                        .put(temp4V3.y)
                        .put(temp4V3.z);


                // Face 2
                positions.put(temp1aV3.x)
                        .put(temp1aV3.y)
                        .put(temp1aV3.z);
                positions.put(temp2aV3.x)
                        .put(temp2aV3.y)
                        .put(temp2aV3.z);
                positions.put(temp3aV3.x)
                        .put(temp3aV3.y)
                        .put(temp3aV3.z);
                positions.put(temp4aV3.x)
                        .put(temp4aV3.y)
                        .put(temp4aV3.z);

                // Face 3
                positions.put(temp1bV3.x)
                        .put(temp1bV3.y)
                        .put(temp1bV3.z);
                positions.put(temp2bV3.x)
                        .put(temp2bV3.y)
                        .put(temp2bV3.z);
                positions.put(temp3bV3.x)
                        .put(temp3bV3.y)
                        .put(temp3bV3.z);
                positions.put(temp4bV3.x)
                        .put(temp4bV3.y)
                        .put(temp4bV3.z);
            }

            if (isUniqueTexCoords()) {

                final int imgX = particleData.spriteCol;
                final int imgY = particleData.spriteRow;

                final float startX = 1f / getSpriteCols() * imgX;
                final float startY = 1f / getSpriteRows() * imgY;
                final float endX = startX + 1f / getSpriteCols();
                final float endY = startY + 1f / getSpriteRows();

                texcoords.put(startX).put(endY);
                texcoords.put(endX).put(endY);
                texcoords.put(startX).put(startY);
                texcoords.put(endX).put(startY);

                texcoords.put(startX).put(endY);
                texcoords.put(endX).put(endY);
                texcoords.put(startX).put(startY);
                texcoords.put(endX).put(startY);

                texcoords.put(startX).put(endY);
                texcoords.put(endX).put(endY);
                texcoords.put(startX).put(startY);
                texcoords.put(endX).put(startY);
            }

            particleData.color.a *= particleData.alpha;
            int abgr = particleData.color.asIntABGR();
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
        }

        //	this.setBuffer(VertexBuffer.Type.Position, 3, positions);
        positions.clear();
        colors.clear();

        if (!isUniqueTexCoords())
            texcoords.clear();
        else {
            texcoords.clear();
            tvb.updateData(texcoords);
        }

        // force renderer to re-send data to GPU
        pvb.updateData(positions);
        cvb.updateData(colors);

        updateBound();
    }

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
        super.cloneFields(cloner, original);

        left33 = cloner.clone(left33);
        left66 = cloner.clone(left66);
        temp1V3 = cloner.clone(temp1V3);
        temp2V3 = cloner.clone(temp2V3);
        temp3V3 = cloner.clone(temp3V3);
        temp4V3 = cloner.clone(temp4V3);
        temp1aV3 = cloner.clone(temp1aV3);
        temp2aV3 = cloner.clone(temp2aV3);
        temp3aV3 = cloner.clone(temp3aV3);
        temp4aV3 = cloner.clone(temp4aV3);
        temp1bV3 = cloner.clone(temp1bV3);
        temp2bV3 = cloner.clone(temp2bV3);
        temp3bV3 = cloner.clone(temp3bV3);
        temp4bV3 = cloner.clone(temp4bV3);

        q33 = cloner.clone(q33);
    }
}