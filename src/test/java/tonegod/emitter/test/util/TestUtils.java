package tonegod.emitter.test.util;

import org.jetbrains.annotations.NotNull;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.test.SetUpTest;

/**
 * @author JavaSaBr
 */
public class TestUtils {

    public static void tryAdd(@NotNull ParticleEmitterNode emitterNode, @NotNull ParticleInfluencer<?> influencer) {

        SetUpTest.getApplication()
            .enqueue(() -> emitterNode.addInfluencer(influencer));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        SetUpTest.checkErrors();
    }

    public static void tryRemove(@NotNull ParticleEmitterNode emitterNode, @NotNull ParticleInfluencer<?> influencer)
        throws InterruptedException {

        SetUpTest.getApplication()
            .enqueue(() -> emitterNode.removeInfluencer(influencer));

        Thread.sleep(2000);

        SetUpTest.checkErrors();
    }
}
