package tonegod.emitter.particle;

import com.jme3.scene.Mesh;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The class with information about data meash of particles in the emitter.
 *
 * @author JavaSaBr.
 */
public class ParticleDataMeshInfo {

    /**
     * The mesh type.
     */
    @NotNull
    private final Class<? extends ParticleDataMesh> meshType;

    /**
     * The template.
     */
    @Nullable
    private final Mesh template;

    public ParticleDataMeshInfo(@NotNull final Class<? extends ParticleDataMesh> meshType, @Nullable final Mesh template) {
        this.meshType = meshType;
        this.template = template;
    }

    /**
     * @return the mesh type.
     */
    @NotNull
    public Class<? extends ParticleDataMesh> getMeshType() {
        return meshType;
    }

    /**
     * @return the template.
     */
    @Nullable
    public Mesh getTemplate() {
        return template;
    }

    @Override
    public String toString() {
        return "ParticleDataMeshInfo{" +
                "meshType=" + meshType +
                ", particleMeshTemplate=" + template +
                '}';
    }
}
