package tonegod.emitter.particle;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.util.BufferUtils;
import com.jme3.util.clone.Cloner;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.BillboardMode;
import tonegod.emitter.ParticleEmitterNode;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Objects;

/**
 * The implementation of particle data mesh to use like some template.
 *
 * @author t0neg0d, JavaSaBr
 */
public final class ParticleDataTemplateMesh extends RotatedParticleDataMesh {

    @NotNull
    private Matrix3f mat3;

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
    public @NotNull Mesh getTemplateMesh() {
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
            e.printStackTrace();
        }

        this.finIndexes = BufferUtils.createShortBuffer(templateIndexes.size() * numParticles);
        this.finNormals = BufferUtils.createFloatBuffer(templateNormals.capacity() * numParticles);
        this.finColors = BufferUtils.createFloatBuffer(templateVerts.capacity() / 3 * 4 * numParticles);

        int index = 0, index2 = 0, index3 = 0, index4 = 0;
        int indexOffset = 0;

        for (int i = 0; i < numParticles; i++) {
            templateVerts.rewind();

            for (int v = 0; v < templateVerts.capacity(); v += 3) {
                finVerts.put(index, templateVerts.get(v));
                index++;
                finVerts.put(index, templateVerts.get(v + 1));
                index++;
                finVerts.put(index, templateVerts.get(v + 2));
                index++;
            }
            try {

                templateCoords.rewind();

                for (int v = 0; v < templateCoords.capacity(); v++) {
                    finCoords.put(index2, templateCoords.get(v));
                    index2++;
                }

            } catch (final Exception e) {
                e.printStackTrace();
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

        // Clear & sign buffers
        clearBuffer(VertexBuffer.Type.Position);
        setBuffer(VertexBuffer.Type.Position, 3, finVerts);
        clearBuffer(VertexBuffer.Type.TexCoord);

        try {
            setBuffer(VertexBuffer.Type.TexCoord, 2, finCoords);
        } catch (final Exception e) {
            e.printStackTrace();
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
        final BillboardMode billboardMode = emitterNode.getBillboardMode();
        final Vector3f worldTranslation = emitterNode.getWorldTranslation();

        for (int i = 0; i < particles.length; i++) {

            final ParticleData particleData = particles[i];

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

                    updateRotation(particleData, billboardMode, camera);

                    tempV1.set(templateVerts.get(x), templateVerts.get(x + 1), templateVerts.get(x + 2));
                    tempV1.set(rotStore.mult(tempV1, tempV2));
                    tempV1.multLocal(particleData.size);

                    rotStore.fromAngles(particleData.angles.x, particleData.angles.y, particleData.angles.z);
                    tempV1.set(rotStore.mult(tempV1, tempV2));

                    tempV1.addLocal(particleData.position);

                    if (!emitterNode.isParticlesFollowEmitter()) {
                        tempV1.subtractLocal(worldTranslation.subtract(particleData.initialPosition, tempV2));//.divide(8f));
                    }

                    finVerts.put(offset + x, tempV1.getX());
                    finVerts.put(offset + x + 1, tempV1.getY());
                    finVerts.put(offset + x + 2, tempV1.getZ());
                }
            }

            if (emitterNode.isApplyLightingTransform()) {
                for (int v = 0; v < templateNormals.capacity(); v += 3) {

                    tempV1.set(templateNormals.get(v), templateNormals.get(v + 1), templateNormals.get(v + 2));
                    rotStore.fromAngles(particleData.angles.x, particleData.angles.y, particleData.angles.z);
                    rotStore.toRotationMatrix(mat3);

                    float vx = tempV1.x, vy = tempV1.y, vz = tempV1.z;

                    tempV1.x = mat3.get(0, 0) * vx + mat3.get(0, 1) * vy + mat3.get(0, 2) * vz;
                    tempV1.y = mat3.get(1, 0) * vx + mat3.get(1, 1) * vy + mat3.get(1, 2) * vz;
                    tempV1.z = mat3.get(2, 0) * vx + mat3.get(2, 1) * vy + mat3.get(2, 2) * vz;

                    finNormals.put(offset + v, tempV1.getX());
                    finNormals.put(offset + v + 1, tempV1.getY());
                    finNormals.put(offset + v + 2, tempV1.getZ());
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

    @Override
    public void cloneFields(@NotNull final Cloner cloner, @NotNull final Object original) {
        super.cloneFields(cloner, original);

        mat3 = cloner.clone(mat3);
        template = cloner.clone(template);

        extractTemplateFromMesh(template);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        super.read(importer);

        final InputCapsule capsule = importer.getCapsule(this);
        extractTemplateFromMesh((Mesh) capsule.readSavable("template", null));
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        super.write(exporter);

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(template, "template", null);
    }
}