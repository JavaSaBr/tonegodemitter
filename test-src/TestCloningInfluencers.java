import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.influencers.impl.ColorInfluencer;

/**
 * Created by ronn on 21.12.16.
 */
public class TestCloningInfluencers {

    public static void main(String[] args) {
        final ColorInfluencer influencer = new ColorInfluencer();
        final ParticleInfluencer clone = influencer.clone();
    }
}
