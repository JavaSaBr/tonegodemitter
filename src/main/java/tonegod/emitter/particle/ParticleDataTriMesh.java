package tonegod.emitter.particle;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
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
 * The type Particle data tri mesh.
 *
 * @author t0neg0d, JavaSaBr
 */
public final class ParticleDataTriMesh extends RotatedParticleDataMesh {

    /**
     * The Color.
     */
    @NotNull
    protected ColorRGBA color;

    public ParticleDataTriMesh() {
        this.color = new ColorRGBA();
    }

    @Override
    public void initialize(@NotNull ParticleEmitterNode emitterNode, int numParticles) {
        super.initialize(emitterNode, numParticles);

        setUniqueTexCoords(false);
        setMode(Mode.Triangles);
        preparePositionBuffer(numParticles * 4);
        prepareColorBuffer(numParticles * 4 * 4);

        // set texcoords
        FloatBuffer tb = BufferUtils.createVector2Buffer(numParticles * 4);

        for (int i = 0; i < numParticles; i++) {
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
            VertexBuffer ivb = new VertexBuffer(VertexBuffer.Type.Index);
            ivb.setupData(Usage.Static, 3, Format.UnsignedShort, ib);
            setBuffer(ivb);
        }

        updateCounts();
    }

    @Override
    public void setImagesXY(int imagesX, int imagesY) {
        super.setImagesXY(imagesX, imagesY);

        if (imagesX != 1 || imagesY != 1) {
            VertexBuffer buffer = getBuffer(VertexBuffer.Type.TexCoord);
            buffer.setUsage(Usage.Stream);
        }
    }

    @Override
    public void updateParticleData(
            @NotNull ParticleData[] particles,
            @NotNull Camera camera,
            @NotNull Matrix3f inverseRotation
    ) {

        ParticleEmitterNode emitterNode = getEmitterNode();
        Vector3f worldTranslation = emitterNode.getWorldTranslation();
        BillboardMode billboardMode = emitterNode.getBillboardMode();

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

                updateRotation(particleData, billboardMode, camera);

                particleData.upVec.set(up);

                if (emitterNode.isVelocityStretching()) {
                    Vector3f velocity = particleData.getVelocity();
                    up.multLocal(velocity.length() * emitterNode.getVelocityStretchFactor());
                }

                Vector3f size = particleData.getSize();
                Vector3f angles = particleData.getAngles();

                up.multLocal(size.y);
                left.multLocal(size.x);

                rotStore.fromAngleNormalAxis(angles.y, tempV3.set(left).normalizeLocal());

                left.set(rotStore.mult(left, tempV2));
                up.set(rotStore.mult(up, tempV2));

                rotStore.fromAngleNormalAxis(angles.x, tempV3.set(up).normalizeLocal());

                left.set(rotStore.mult(left, tempV2));
                up.set(rotStore.mult(up, tempV2));

                rotStore.fromAngleNormalAxis(angles.z, tempV3.set(dir).normalizeLocal());

                left.set(rotStore.mult(left, tempV2));
                up.set(rotStore.mult(up, tempV2));

                if (emitterNode.isParticlesFollowEmitter()) {
                    tempV1.set(particleData.position);
                } else {

                    Vector3f subtract = worldTranslation
                            .subtract(particleData.initialPosition, tempV2);

                    tempV1.set(particleData.position)
                            .subtractLocal(subtract);
                }

                positions.put(tempV1.x + left.x + up.x)
                        .put(tempV1.y + left.y + up.y)
                        .put(tempV1.z + left.z + up.z);

                positions.put(tempV1.x - left.x + up.x)
                        .put(tempV1.y - left.y + up.y)
                        .put(tempV1.z - left.z + up.z);

                positions.put(tempV1.x + left.x - up.x)
                        .put(tempV1.y + left.y - up.y)
                        .put(tempV1.z + left.z - up.z);

                positions.put(tempV1.x - left.x - up.x)
                        .put(tempV1.y - left.y - up.y)
                        .put(tempV1.z - left.z - up.z);
            }

            if (isUniqueTexCoords()) {

                float startX = 1f / emitterNode.getSpriteColCount() * particleData.spriteCol;
                float startY = 1f / emitterNode.getSpriteRowCount() * particleData.spriteRow;

                float endX = startX + 1f / emitterNode.getSpriteColCount();
                float endY = startY + 1f / emitterNode.getSpriteRowCount();

                texcoords.put(startX).put(endY);
                texcoords.put(endX).put(endY);
                texcoords.put(startX).put(startY);
                texcoords.put(endX).put(startY);
            }

            color.set(particleData.color);
            color.a *= particleData.alpha;

            int abgr = color.asIntABGR();
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
        }

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

    @Override
    public void cloneFields(@NotNull Cloner cloner, @NotNull Object original) {
        super.cloneFields(cloner, original);
        color = cloner.clone(color);
    }
}