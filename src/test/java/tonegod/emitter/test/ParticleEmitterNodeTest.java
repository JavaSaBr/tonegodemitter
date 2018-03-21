package tonegod.emitter.test;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.impl.AlphaInfluencer;
import tonegod.emitter.influencers.impl.ColorInfluencer;
import tonegod.emitter.influencers.impl.SizeInfluencer;

public class ParticleEmitterNodeTest extends SetUpTest {

    @Test
    public void testCreateEmitterNode() throws InterruptedException {

        final SimpleApplication application = getApplication();

        final ParticleEmitterNode emitter = createEmitter(application);
        final Node rootNode = application.getRootNode();

        application.enqueue(new Runnable() {

            @Override
            public void run() {
                rootNode.attachChild(emitter);
            }
        });

        Thread.sleep(10000);

        rootNode.detachChild(emitter);
    }

    @Test
    public void testModifyInfluencersEmitterNode() throws InterruptedException {

        final SimpleApplication application = getApplication();

        final ParticleEmitterNode emitter = createEmitter(application);
        final Node rootNode = application.getRootNode();

        application.enqueue(new Runnable() {

            @Override
            public void run() {
                rootNode.attachChild(emitter);
            }
        });

        Thread.sleep(2000);

        application.enqueue(new Runnable() {

            @Override
            public void run() {
                emitter.removeInfluencer(SizeInfluencer.class);
            }
        });

        Thread.sleep(2000);

        application.enqueue(new Runnable() {

            @Override
            public void run() {
                emitter.addInfluencer(new ColorInfluencer(ColorRGBA.Green, ColorRGBA.Blue));
            }
        });

        Thread.sleep(2000);

        application.enqueue(new Runnable() {

            @Override
            public void run() {
                emitter.addInfluencer(new SizeInfluencer(0.1f, 1F), 1);
            }
        });

        Thread.sleep(2000);

        rootNode.detachChild(emitter);
    }

    private @NotNull ParticleEmitterNode createEmitter(@NotNull final SimpleApplication application) {

        final ParticleEmitterNode emitter = new ParticleEmitterNode(application.getAssetManager());
        emitter.addInfluencers(new ColorInfluencer(), new AlphaInfluencer(), new SizeInfluencer(0.1F, 0F));
        emitter.setEnabled(true);

        return emitter;
    }
}
