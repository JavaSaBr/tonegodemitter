package tonegod.emitter.particle;

import com.jme3.math.Matrix3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Format;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.util.BufferUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import tonegod.emitter.ParticleEmitterNode;

/**
 * The implementation of data mesh to use point mesh.
 *
 * @author t0neg0d, JavaSaBr
 */
public final class ParticleDataPointMesh extends ParticleDataMesh {

    public ParticleDataPointMesh() {
        super();
    }

    @Override
    public void initParticleData(@NotNull final ParticleEmitterNode particleEmitterNode, final int numParticles) {
        super.initParticleData(particleEmitterNode, numParticles);

        setMode(Mode.Points);
        preparePositionBuffer(numParticles);
        prepareColorBuffer(numParticles);

        // set sizes
        FloatBuffer sb = BufferUtils.createFloatBuffer(numParticles);

        VertexBuffer buf = getBuffer(VertexBuffer.Type.Size);

        if (buf != null) {
            buf.updateData(sb);
        } else {
            VertexBuffer svb = new VertexBuffer(VertexBuffer.Type.Size);
            svb.setupData(Usage.Stream, 1, Format.Float, sb);
            setBuffer(svb);
        }

        // set UV-scale
        FloatBuffer tb = BufferUtils.createFloatBuffer(numParticles * 4);

        buf = getBuffer(VertexBuffer.Type.TexCoord);

        if (buf != null) {
            buf.updateData(tb);
        } else {
            VertexBuffer tvb = new VertexBuffer(VertexBuffer.Type.TexCoord);
            tvb.setupData(Usage.Stream, 4, Format.Float, tb);
            setBuffer(tvb);
        }

        updateCounts();
    }

    @Override
    public void updateParticleData(@NotNull final ParticleData[] particles, @NotNull final Camera camera,
                                   @NotNull final Matrix3f inverseRotation) {

        final VertexBuffer pvb = getBuffer(VertexBuffer.Type.Position);
        final FloatBuffer positions = (FloatBuffer) pvb.getData();

        final VertexBuffer cvb = getBuffer(VertexBuffer.Type.Color);
        final ByteBuffer colors = (ByteBuffer) cvb.getData();

        final VertexBuffer svb = getBuffer(VertexBuffer.Type.Size);
        final FloatBuffer sizes = (FloatBuffer) svb.getData();

        final VertexBuffer tvb = getBuffer(VertexBuffer.Type.TexCoord);
        final FloatBuffer texcoords = (FloatBuffer) tvb.getData();

        //float sizeScale = emitter.getWorldScale().x;

        // update data in vertex buffers
        positions.rewind();
        colors.rewind();
        sizes.rewind();
        texcoords.rewind();

        for (final ParticleData particleData : particles) {

            positions.put(particleData.position.x)
                    .put(particleData.position.y)
                    .put(particleData.position.z);

            sizes.put(particleData.size.x); // * worldSace);

            particleData.color.a *= particleData.alpha;
            colors.putInt(particleData.color.asIntABGR());

            int imgX = particleData.spriteCol; //particleData.imageIndex % imagesX;
            int imgY = particleData.spriteRow; //(particleData.imageIndex - imgX) / imagesY;

            float startX = ((float) imgX) / getSpriteCols();
            float startY = ((float) imgY) / getSpriteRows();
            float endX = startX + (1f / getSpriteCols());
            float endY = startY + (1f / getSpriteRows());

            texcoords.put(startX).put(startY).put(endX).put(endY);
        }

        positions.flip();
        colors.flip();
        sizes.flip();
        texcoords.flip();

        // force renderer to re-send data to GPU
        pvb.updateData(positions);
        cvb.updateData(colors);
        svb.updateData(sizes);
        tvb.updateData(texcoords);

        updateBound();
    }

    @Override
    public void extractTemplateFromMesh(@NotNull final Mesh mesh) {
    }
}