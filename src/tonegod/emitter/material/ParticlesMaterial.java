package tonegod.emitter.material;

import com.jme3.material.Material;

import org.jetbrains.annotations.NotNull;

/**
 * The struct for describing the material of particles.
 *
 * @author JavaSaBr
 */
public class ParticlesMaterial {

    /**
     * The material of particles.
     */
    @NotNull
    private final Material material;

    /**
     * The name of material parameter which contains a texture of particles in the material.
     */
    @NotNull
    private final String textureParam;

    /**
     * Forces update of normals and should only be used if the emitter material uses a lighting shader.
     */
    private final boolean applyLightingTransform;

    public ParticlesMaterial(@NotNull final Material material, @NotNull final String textureParam, final boolean applyLightingTransform) {
        this.material = material;
        this.textureParam = textureParam;
        this.applyLightingTransform = applyLightingTransform;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        final ParticlesMaterial that = (ParticlesMaterial) object;
        return applyLightingTransform == that.applyLightingTransform &&
                material.equals(that.material) && textureParam.equals(that.textureParam);
    }

    @Override
    public int hashCode() {
        int result = material.hashCode();
        result = 31 * result + textureParam.hashCode();
        result = 31 * result + (applyLightingTransform ? 1 : 0);
        return result;
    }

    /**
     * @return The material of particles.
     */
    @NotNull
    public Material getMaterial() {
        return material;
    }

    /**
     * @return the name of material parameter which contains a texture of particles in the material.
     */
    @NotNull
    public String getTextureParam() {
        return textureParam;
    }

    /**
     * @return forces update of normals and should only be used if the emitter material uses a lighting shader.
     */
    public boolean isApplyLightingTransform() {
        return applyLightingTransform;
    }

    @Override
    public String toString() {
        return "ParticlesMaterial{" +
                "material=" + material +
                ", textureParam='" + textureParam + '\'' +
                ", applyLightingTransform=" + applyLightingTransform +
                '}';
    }
}
