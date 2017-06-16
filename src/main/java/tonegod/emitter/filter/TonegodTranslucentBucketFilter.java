package tonegod.emitter.filter;

import com.jme3.asset.AssetManager;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.math.ColorRGBA;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tonegod.emitter.ParticleEmitterNode;

/**
 * A filter to handle translucent objects when rendering a scene with filters that uses depth like WaterFilter and
 * SSAOFilter just create a TranslucentBucketFilter and add it to the Filter list of a FilterPostPorcessor
 *
 * @author Nehon, JavaSaBr
 */
public class TonegodTranslucentBucketFilter extends Filter {

    private RenderManager renderManager;
    private Texture depthTexture;
    private ViewPort viewPort;

    private boolean enabledSoftParticles;

    /**
     * Instantiates a new Tonegod translucent bucket filter.
     */
    public TonegodTranslucentBucketFilter() {
        super("TonegodTranslucentBucketFilter");
    }

    /**
     * Instantiates a new Tonegod translucent bucket filter.
     *
     * @param enabledSoftParticles the enabled soft particles
     */
    public TonegodTranslucentBucketFilter(final boolean enabledSoftParticles) {
        this();
        this.enabledSoftParticles = enabledSoftParticles;
    }

    @Override
    protected void initFilter(@NotNull final AssetManager manager, @NotNull final RenderManager renderManager,
                              @NotNull final ViewPort viewPort, int width, int height) {

        this.renderManager = renderManager;
        this.viewPort = viewPort;

        material = new Material(manager, "Common/MatDefs/Post/Overlay.j3md");
        material.setColor("Color", ColorRGBA.White);

        final Texture2D texture = processor.getFilterTexture();
        final Image image = texture.getImage();

        material.setTexture("Texture", texture);

        if (image.getMultiSamples() > 1) {
            material.setInt("NumSamples", image.getMultiSamples());
        } else {
            material.clearParam("NumSamples");
        }

        this.renderManager.setHandleTranslucentBucket(false);

        if (enabledSoftParticles && depthTexture != null) {
            initSoftParticles(viewPort, true);
        }
    }

    private void initSoftParticles(@NotNull final ViewPort viewPort, final boolean enabledSP) {
        if (depthTexture == null) return;
        for (final Spatial scene : viewPort.getScenes()) {
            makeSoftParticleEmitter(scene, enabledSP && enabled);
        }
    }

    @Override
    protected void setDepthTexture(@Nullable final Texture depthTexture) {
        this.depthTexture = depthTexture;
        if (enabledSoftParticles && depthTexture != null) {
            initSoftParticles(viewPort, true);
        }
    }

    @Override
    protected boolean isRequiresSceneTexture() {
        return false;
    }

    @Override
    protected boolean isRequiresDepthTexture() {
        return enabledSoftParticles;
    }

    @Override
    protected void postFrame(@NotNull final RenderManager renderManager, @NotNull final ViewPort viewPort,
                             @NotNull final FrameBuffer prevFilterBuffer, @NotNull final FrameBuffer sceneBuffer) {

        renderManager.setCamera(viewPort.getCamera(), false);

        final Renderer renderer = renderManager.getRenderer();

        if (prevFilterBuffer != sceneBuffer) {
            renderer.copyFrameBuffer(prevFilterBuffer, sceneBuffer, false);
        }

        renderer.setFrameBuffer(sceneBuffer);

        final RenderQueue queue = viewPort.getQueue();
        queue.renderQueue(RenderQueue.Bucket.Translucent, renderManager, viewPort.getCamera());
    }

    @Override
    protected void cleanUpFilter(@NotNull final Renderer renderer) {

        if (renderManager != null) {
            renderManager.setHandleTranslucentBucket(true);
        }

        if (viewPort != null) {
            initSoftParticles(viewPort, false);
        }
    }

    @Override
    protected Material getMaterial() {
        return material;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);

        if (renderManager != null) {
            renderManager.setHandleTranslucentBucket(!enabled);
        }

        if (viewPort != null) {
            initSoftParticles(viewPort, enabledSoftParticles);
        }
    }

    /**
     * Refresh using this filter.
     */
    public void refresh() {
        if (viewPort != null) {
            initSoftParticles(viewPort, enabledSoftParticles);
        }
    }

    /**
     * Refresh using this filter.
     *
     * @param spatial the spatial
     */
    public void refresh(@NotNull final Spatial spatial) {
        if (viewPort != null && depthTexture != null) {
            makeSoftParticleEmitter(spatial, enabledSoftParticles);
        }
    }

    private void makeSoftParticleEmitter(@NotNull final Spatial scene, final boolean enabled) {

        if (scene instanceof ParticleEmitterNode) {

            final ParticleEmitterNode emitter = (ParticleEmitterNode) scene;
            final Material material = emitter.getMaterial();
            final MaterialDef materialDef = material.getMaterialDef();
            final MatParam numSamplesDepth = materialDef.getMaterialParam("NumSamplesDepth");
            final MatParam sceneDepthTexture = materialDef.getMaterialParam("SceneDepthTexture");

            if (processor.getNumSamples() > 1 && numSamplesDepth != null) {
                material.setInt("NumSamplesDepth", processor.getNumSamples());
            }

            if (sceneDepthTexture != null) {
                material.setTexture("SceneDepthTexture", processor.getDepthTexture());
            }

        } else if (scene instanceof Node) {
            final Node node = (Node) scene;
            for (Spatial child : node.getChildren()) {
                makeSoftParticleEmitter(child, enabled);
            }
        }
    }
}