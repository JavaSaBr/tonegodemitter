package tonegod.emitter.test.util;

import static tonegod.emitter.test.SetUpTest.getApplication;
import com.jme3.math.Vector3f;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.test.SetUpTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author JavaSaBr
 */
public class TestUtils {

    public static void tryAdd(@NotNull ParticleEmitterNode emitterNode, @NotNull ParticleInfluencer<?> influencer) {

        getApplication()
            .enqueue(() -> emitterNode.addInfluencer(influencer));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        SetUpTest.checkErrors();
    }

    public static void tryRemove(@NotNull ParticleEmitterNode emitterNode, @NotNull ParticleInfluencer<?> influencer)
        throws InterruptedException {

        getApplication()
            .enqueue(() -> emitterNode.removeInfluencer(influencer));

        Thread.sleep(2000);

        SetUpTest.checkErrors();
    }


    public static @NotNull ParticleEmitterNode tryClone(@NotNull ParticleEmitterNode emitterNode)
            throws InterruptedException {

        var ref = new AtomicReference<ParticleEmitterNode>();
        var waiter = new CountDownLatch(1);

        var application = getApplication();
        application.enqueue(() -> {
            ref.set(emitterNode.clone());
            waiter.countDown();
        });

        waiter.await();

        SetUpTest.checkErrors();

        return ref.get();
    }

    public static @NotNull ParticleEmitterNode createEmitter() throws InterruptedException {

        var ref = new AtomicReference<ParticleEmitterNode>();
        var waiter = new CountDownLatch(1);

        var application = getApplication();
        application.enqueue(() -> {

            var emitter = new ParticleEmitterNode(application.getAssetManager());
            emitter.setEnabled(true);

            ref.set(emitter);
            waiter.countDown();
        });

        waiter.await();

        SetUpTest.checkErrors();

        return ref.get();
    }

    public static void attach(@NotNull ParticleEmitterNode emitterNode)
            throws InterruptedException {

        var waiter = new CountDownLatch(1);
        var application = getApplication();
        application.enqueue(() -> {
            var rootNode = application.getRootNode();
            rootNode.attachChild(emitterNode);
            waiter.countDown();
        });

        waiter.await();

        SetUpTest.checkErrors();
    }

    public static void detach(@NotNull ParticleEmitterNode emitterNode)
            throws InterruptedException {

        var waiter = new CountDownLatch(1);
        var application = getApplication();
        application.enqueue(() -> {
            var rootNode = application.getRootNode();
            rootNode.detachChild(emitterNode);
            waiter.countDown();
        });

        waiter.await();

        SetUpTest.checkErrors();
    }

    public static void move(@NotNull ParticleEmitterNode emitterNode, @NotNull Vector3f position)
            throws InterruptedException {

        var waiter = new CountDownLatch(1);
        var application = getApplication();
        application.enqueue(() -> {
            emitterNode.setLocalTranslation(position);
            waiter.countDown();
        });

        waiter.await();

        SetUpTest.checkErrors();
    }
}
