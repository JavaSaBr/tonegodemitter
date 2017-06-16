import com.jme3.math.ColorRGBA;

import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.influencers.impl.ColorInfluencer;

/**
 * Created by ronn on 21.12.16.
 */
public class TestCloningInfluencers {

    public static void main(String[] args) {
        final ColorInfluencer influencer = new ColorInfluencer();
        influencer.addColor(ColorRGBA.Blue);
        influencer.addColor(ColorRGBA.Black);
        final ParticleInfluencer clone = influencer.clone();
    }
}
