package tonegod.emitter.test;

import static tonegod.emitter.test.util.TestUtils.createEmitter;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.influencers.impl.*;
import tonegod.emitter.test.util.TestUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleEmitterNodeTest extends SetUpTest {

    @Test
    public void testCreateEmitterNode() throws InterruptedException {

        var application = getApplication();
        var emitter = createEmitter();
        var rootNode = application.getRootNode();

        application.enqueue(() -> rootNode.attachChild(emitter));

        Thread.sleep(5000);

        rootNode.detachChild(emitter);
    }

    @Test
    public void testAddAllInfluencers() throws InterruptedException {

        var application = getApplication();
        var emitter = createEmitter();
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
        var emitter = createEmitter();
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
        var emitter = createEmitter();
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

        var emitter = createEmitter();
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

    @Test
    public void testCloneEmitterNode() throws InterruptedException {

        var emitter = createEmitter();
        createInfluencers().forEach(emitter::addInfluencer);

        var clone = emitter.clone();
    }

    @Test
    public void testDeepCloneEmitterNode() throws InterruptedException {

        var emitter = createEmitter();
        createInfluencers().forEach(emitter::addInfluencer);

        var clone = emitter.deepClone();
    }

    @Test
    public void testCloneAndModifyEmitters() throws InterruptedException {

        var original = createEmitter();

        TestUtils.attach(original);
        TestUtils.move(original, new Vector3f(3, 0, 0));

        Thread.sleep(2000);

        var random = ThreadLocalRandom.current();

        var influencers = createInfluencers();
        influencers.forEach(influencer -> TestUtils.tryAdd(original, influencer));

        var cloned = TestUtils.tryClone(original);

        TestUtils.attach(cloned);
        TestUtils.move(cloned, new Vector3f(-3, 0, 0));

        for (int i = 0, max = influencers.size() / 2; i < max; i++) {
            //TestUtils.tryRemove(cloned, influencers.get(random.nextInt(influencers.size() - 1)));
        }

        Thread.sleep(15000);

        TestUtils.detach(original);
        TestUtils.detach(cloned);
    }

    private @NotNull List<ParticleInfluencer<?>> createInfluencers() {
        return List.of(new ColorInfluencer(), new SizeInfluencer(0.1F, 0F), new AlphaInfluencer(),
                new DestinationInfluencer(), new RotationInfluencer(), new PhysicsInfluencer(), new ImpulseInfluencer(),
                new RadialVelocityInfluencer(), new SpriteInfluencer(), new GravityInfluencer());
    }
}
