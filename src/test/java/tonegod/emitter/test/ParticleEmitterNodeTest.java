package tonegod.emitter.test;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.influencers.impl.*;
import tonegod.emitter.test.util.TestUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleEmitterNodeTest extends SetUpTest {

    @Test
    public void testCreateEmitterNode() throws InterruptedException {

        var application = getApplication();
        var emitter = createEmitter(application);
        var rootNode = application.getRootNode();

        application.enqueue(() -> rootNode.attachChild(emitter));

        Thread.sleep(5000);

        rootNode.detachChild(emitter);
    }

    @Test
    public void testAddAllInfluencers() throws InterruptedException {

        var application = getApplication();
        var emitter = createEmitter(application);
        var rootNode = application.getRootNode();

        application.enqueue(() -> rootNode.attachChild(emitter));

        var influencers = createInfluencers();

        influencers.forEach(influencer -> TestUtils.tryAdd(emitter, influencer));

        Thread.sleep(5000);

        rootNode.detachChild(emitter);
    }

    @Test
    public void testAddAllAndRemoveSomeInfluencers() throws InterruptedException {

        var application = getApplication();
        var emitter = createEmitter(application);
        var rootNode = application.getRootNode();

        application.enqueue(() -> rootNode.attachChild(emitter));

        var random = ThreadLocalRandom.current();
        var influencers = createInfluencers();

        influencers.forEach(influencer -> TestUtils.tryAdd(emitter, influencer));

        TestUtils.tryRemove(emitter, influencers.get(influencers.size() - 1));

        for (int i = 0, max = influencers.size() / 2; i < max; i++) {
            TestUtils.tryRemove(emitter, influencers.get(random.nextInt(influencers.size() - 1)));
        }

        Thread.sleep(5000);

        rootNode.detachChild(emitter);
    }

    @Test
    public void testAddAllAndRemoveSomeAndAddAgainInfluencers() throws InterruptedException {

        var application = getApplication();
        var emitter = createEmitter(application);
        var rootNode = application.getRootNode();

        application.enqueue(() -> rootNode.attachChild(emitter));

        var random = ThreadLocalRandom.current();
        var influencers = createInfluencers();

        influencers.forEach(influencer -> TestUtils.tryAdd(emitter, influencer));

        TestUtils.tryRemove(emitter, influencers.get(influencers.size() - 1));

        for (int i = 0, max = influencers.size() / 2; i < max; i++) {
            TestUtils.tryRemove(emitter, influencers.get(random.nextInt(influencers.size() - 1)));
        }

        createInfluencers().forEach(influencer -> TestUtils.tryAdd(emitter, influencer));

        Thread.sleep(5000);

        rootNode.detachChild(emitter);
    }

    @Test
    public void testModifyInfluencersEmitterNode() throws InterruptedException {

        var application = getApplication();

        var emitter = createEmitter(application);
        var rootNode = application.getRootNode();

        application.enqueue(() -> {
            rootNode.attachChild(emitter);
        });

        Thread.sleep(2000);
        checkErrors();

        application.enqueue(() -> emitter.removeInfluencer(SizeInfluencer.class));

        Thread.sleep(2000);
        checkErrors();

        application.enqueue(() -> emitter.addInfluencer(new ColorInfluencer(ColorRGBA.Green, ColorRGBA.Blue)));

        Thread.sleep(2000);
        checkErrors();

        application.enqueue(() -> emitter.addInfluencer(new SizeInfluencer(0.1f, 1F), 1));

        Thread.sleep(2000);
        checkErrors();

        rootNode.detachChild(emitter);
    }

    private @NotNull List<ParticleInfluencer<?>> createInfluencers() {
        return List.of(new ColorInfluencer(), new SizeInfluencer(0.1F, 0F), new AlphaInfluencer(),
                new DestinationInfluencer(), new RotationInfluencer(), new PhysicsInfluencer(), new ImpulseInfluencer(),
                new RadialVelocityInfluencer(), new SpriteInfluencer(), new GravityInfluencer());
    }

    private @NotNull ParticleEmitterNode createEmitter(@NotNull SimpleApplication application) {

        var emitter = new ParticleEmitterNode(application.getAssetManager());
        emitter.setEnabled(true);

        return emitter;
    }
}
