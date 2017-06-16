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

    /**
     * Instantiates a new Particle data mesh info.
     *
     * @param meshType the mesh type
     * @param template the template
     */
    public ParticleDataMeshInfo(@NotNull final Class<? extends ParticleDataMesh> meshType, @Nullable final Mesh template) {
        this.meshType = meshType;
        this.template = template;
    }

    /**
     * Gets mesh type.
     *
     * @return the mesh type.
     */
    @NotNull
    public Class<? extends ParticleDataMesh> getMeshType() {
        return meshType;
    }

    /**
     * Gets template.
     *
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
