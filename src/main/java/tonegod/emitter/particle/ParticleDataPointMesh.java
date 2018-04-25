package tonegod.emitter.particle;

import com.jme3.math.Matrix3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Format;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.util.BufferUtils;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.ParticleEmitterNode;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

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
    public void initialize(@NotNull ParticleEmitterNode particleEmitterNode, int numParticles) {
        super.initialize(particleEmitterNode, numParticles);

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
    public void updateParticleData(
            @NotNull ParticleData[] particles,
            @NotNull Camera camera,
            @NotNull Matrix3f inverseRotation
    ) {

        VertexBuffer pvb = getBuffer(VertexBuffer.Type.Position);
        FloatBuffer positions = (FloatBuffer) pvb.getData();

        VertexBuffer cvb = getBuffer(VertexBuffer.Type.Color);
        ByteBuffer colors = (ByteBuffer) cvb.getData();

        VertexBuffer svb = getBuffer(VertexBuffer.Type.Size);
        FloatBuffer sizes = (FloatBuffer) svb.getData();

        VertexBuffer tvb = getBuffer(VertexBuffer.Type.TexCoord);
        FloatBuffer texcoords = (FloatBuffer) tvb.getData();

        //float sizeScale = emitter.getWorldScale().x;

        // update data in vertex buffers
        positions.rewind();
        colors.rewind();
        sizes.rewind();
        texcoords.rewind();

        for (ParticleData particleData : particles) {

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
    public void extractTemplateFromMesh(@NotNull Mesh mesh) {
    }
}