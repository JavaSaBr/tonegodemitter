package tonegod.emitter.test;

import com.jme3.asset.DesktopAssetManager;
import org.junit.jupiter.api.Test;
import tonegod.emitter.ParticleEmitterNode;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** CPU-only particle-count regression; no renderer or display is required. */
public class ParticleCountRegressionTest {
    @Test
    public void activeCountTracksOnlyActiveParticles() {
        verifyCounts();
    }

    public static void main(String[] args) {
        verifyCounts();
        System.out.println("PASS tonegodemitter count invariants: construction, resize, emit, repeated clear");
    }

    private static void verifyCounts() {
        ParticleEmitterNode emitter = new ParticleEmitterNode(new DesktopAssetManager(true));
        assertEquals(0, emitter.getActiveParticleCount(), "initial inactive particles");
        emitter.setMaxParticles(10);
        assertEquals(0, emitter.getActiveParticleCount(), "resizing inactive particles");
        emitter.emitNumParticles(3);
        assertEquals(3, emitter.getActiveParticleCount(), "three emitted particles");
        emitter.killAllParticles();
        assertEquals(0, emitter.getActiveParticleCount(), "clearing active particles");
        emitter.killAllParticles();
        assertEquals(0, emitter.getActiveParticleCount(), "repeated clear");
        emitter.emitNumParticles(2);
        assertEquals(2, emitter.getActiveParticleCount(), "emission after clear");
        emitter.setMaxParticles(20);
        assertEquals(0, emitter.getActiveParticleCount(), "resizing active particles");
    }
}
