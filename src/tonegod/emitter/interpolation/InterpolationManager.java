package tonegod.emitter.interpolation;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.IntegerDictionary;
import rlib.util.dictionary.ObjectDictionary;

/**
 * The class for managing available interpolations.
 *
 * @author JavaSaBr
 */
public class InterpolationManager {

    private static final ObjectDictionary<Interpolation, Integer> INTER_TO_ID;
    private static final ObjectDictionary<String, Interpolation> NAME_TO_INTER;
    private static final IntegerDictionary<Interpolation> ID_TO_INTER;

    static {
        INTER_TO_ID = DictionaryFactory.newObjectDictionary();
        INTER_TO_ID.put(Interpolation.LINEAR, 1);
        INTER_TO_ID.put(Interpolation.FADE, 2);
        INTER_TO_ID.put(Interpolation.SINE, 3);
        INTER_TO_ID.put(Interpolation.SINE_IN, 4);
        INTER_TO_ID.put(Interpolation.SINE_OUT, 5);
        INTER_TO_ID.put(Interpolation.EXP_10, 6);
        INTER_TO_ID.put(Interpolation.EXP_10_IN, 7);
        INTER_TO_ID.put(Interpolation.EXP_10_OUT, 8);
        INTER_TO_ID.put(Interpolation.EXP_5, 9);
        INTER_TO_ID.put(Interpolation.EXP_5_IN, 10);
        INTER_TO_ID.put(Interpolation.EXP_5_OUT, 11);
        INTER_TO_ID.put(Interpolation.CIRCLE, 12);
        INTER_TO_ID.put(Interpolation.CIRCLE_IN, 13);
        INTER_TO_ID.put(Interpolation.CIRCLE_OUT, 14);
        INTER_TO_ID.put(Interpolation.SWING, 15);
        INTER_TO_ID.put(Interpolation.SWING_IN, 16);
        INTER_TO_ID.put(Interpolation.SWING_OUT, 17);
        INTER_TO_ID.put(Interpolation.BOUNCE, 18);
        INTER_TO_ID.put(Interpolation.BOUNCE_IN, 19);
        INTER_TO_ID.put(Interpolation.BOUNCE_OUT, 20);
        INTER_TO_ID.put(Interpolation.POW_2, 21);
        INTER_TO_ID.put(Interpolation.POW_2_IN, 22);
        INTER_TO_ID.put(Interpolation.POW_2_OUT, 23);
        INTER_TO_ID.put(Interpolation.POW_3, 24);
        INTER_TO_ID.put(Interpolation.POW_3_IN, 25);
        INTER_TO_ID.put(Interpolation.POW_3_OUT, 26);
        INTER_TO_ID.put(Interpolation.POW_4, 27);
        INTER_TO_ID.put(Interpolation.POW_4_IN, 28);
        INTER_TO_ID.put(Interpolation.POW_4_OUT, 29);
        INTER_TO_ID.put(Interpolation.POW_5, 30);
        INTER_TO_ID.put(Interpolation.POW_5_IN, 31);
        INTER_TO_ID.put(Interpolation.POW_5_OUT, 32);
        INTER_TO_ID.put(Interpolation.ELASTIC, 33);
        INTER_TO_ID.put(Interpolation.ELASTIC_IN, 34);
        INTER_TO_ID.put(Interpolation.ELASTIC_OUT, 35);
        ID_TO_INTER = DictionaryFactory.newIntegerDictionary();
        NAME_TO_INTER = DictionaryFactory.newObjectDictionary();
        INTER_TO_ID.forEach((interpolation, id) -> ID_TO_INTER.put(id, interpolation));
        INTER_TO_ID.forEach((interpolation, id) -> NAME_TO_INTER.put(interpolation.getName(), interpolation));
    }

    public static Interpolation getInterpolation(@NotNull final String name) {
        return Objects.requireNonNull(NAME_TO_INTER.get(name), "Unknown interpolation " + name);
    }

    public static int getId(@NotNull final Interpolation interpolation) {
        return Objects.requireNonNull(INTER_TO_ID.get(interpolation), "Unknown interpolation " + interpolation);
    }

    @NotNull
    public static Interpolation getInterpolation(final int id) {
        return Objects.requireNonNull(ID_TO_INTER.get(id), "Unknown id " + id);
    }
}
