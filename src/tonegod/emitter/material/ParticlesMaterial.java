package tonegod.emitter.material;

import com.jme3.material.Material;

/**
 * The struct for describing the material of particles.
 *
 * @author JavaSaBr
 */
public class ParticlesMaterial {

    /**
     * The material of particles.
     */
    private final Material material;

    /**
     * The name of material parameter which contains a texture of particles in the material.
     */
    private final String textureParam;

    /**
     * Forces update of normals and should only be used if the emitter material uses a lighting shader.
     */
    private final boolean applyLightingTransform;

    public ParticlesMaterial(final Material material, final String textureParam, final boolean applyLightingTransform) {
        this.material = material;
        this.textureParam = textureParam;
        this.applyLightingTransform = applyLightingTransform;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        final ParticlesMaterial that = (ParticlesMaterial) object;
        if (applyLightingTransform != that.applyLightingTransform) return false;
        if (!material.equals(that.material)) return false;
        return textureParam.equals(that.textureParam);
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
    public Material getMaterial() {
        return material;
    }

    /**
     * @return the name of material parameter which contains a texture of particles in the material.
     */
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
