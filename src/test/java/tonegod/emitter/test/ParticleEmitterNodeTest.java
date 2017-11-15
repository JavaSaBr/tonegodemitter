package tonegod.emitter.test;

import com.jme3.app.SimpleApplication;
import org.junit.jupiter.api.Test;
import tonegod.emitter.ParticleEmitterNode;

public class ParticleEmitterNodeTest extends SetUpTest {

    @Test
    public void testCreateEmitterNode() {

        final SimpleApplication application = getApplication();

        final ParticleEmitterNode emitterNode = new ParticleEmitterNode();
        final ParticleEmitterNode secondNode = new ParticleEmitterNode(application.getAssetManager());
    }
}
