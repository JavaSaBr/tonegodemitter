package tonegod.emitter.interpolation;

import static java.util.Objects.requireNonNull;
import com.jme3.util.IntMap;
import com.jme3.util.SafeArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The class for managing available interpolations.
 *
 * @author JavaSaBr
 */
public class InterpolationManager {

    @NotNull
    private static final Map<Interpolation, Integer> INTER_TO_ID;

    @NotNull
    private static final Map<String, Interpolation> NAME_TO_INTER;

    @NotNull
    private static final IntMap<Interpolation> ID_TO_INTER;

    @NotNull
    private static final SafeArrayList<Interpolation> INTERPOLATIONS;

    @NotNull
    private static final AtomicInteger ID_FACTORY = new AtomicInteger();

    static {
        INTER_TO_ID = new HashMap<>();
        ID_TO_INTER = new IntMap<>();
        NAME_TO_INTER = new HashMap<>();
        INTERPOLATIONS = new SafeArrayList<>(Interpolation.class);
        register(Interpolation.LINEAR);
        register(Interpolation.FADE);
        register(Interpolation.SINE);
        register(Interpolation.SINE_IN);
        register(Interpolation.SINE_OUT);
        register(Interpolation.EXP_10);
        register(Interpolation.EXP_10_IN);
        register(Interpolation.EXP_10_OUT);
        register(Interpolation.EXP_5);
        register(Interpolation.EXP_5_IN);
        register(Interpolation.EXP_5_OUT);
        register(Interpolation.CIRCLE);
        register(Interpolation.CIRCLE_IN);
        register(Interpolation.CIRCLE_OUT);
        register(Interpolation.SWING);
        register(Interpolation.SWING_IN);
        register(Interpolation.SWING_OUT);
        register(Interpolation.BOUNCE);
        register(Interpolation.BOUNCE_IN);
        register(Interpolation.BOUNCE_OUT);
        register(Interpolation.POW_2);
        register(Interpolation.POW_2_IN);
        register(Interpolation.POW_2_OUT);
        register(Interpolation.POW_3);
        register(Interpolation.POW_3_IN);
        register(Interpolation.POW_3_OUT);
        register(Interpolation.POW_4);
        register(Interpolation.POW_4_IN);
        register(Interpolation.POW_4_OUT);
        register(Interpolation.POW_5);
        register(Interpolation.POW_5_IN);
        register(Interpolation.POW_5_OUT);
        register(Interpolation.ELASTIC);
        register(Interpolation.ELASTIC_IN);
        register(Interpolation.ELASTIC_OUT);
    }

    /**
     * Register an interpolation.
     *
     * @param interpolation the interpolation.
     */
    public static void register(@NotNull final Interpolation interpolation) {
        final int id = ID_FACTORY.incrementAndGet();
        INTER_TO_ID.put(interpolation, id);
        ID_TO_INTER.put(id, interpolation);
        NAME_TO_INTER.put(interpolation.getName(), interpolation);
        INTERPOLATIONS.add(interpolation);
    }

    /**
     * Get a list of available interpolations.
     *
     * @return the list of available interpolations.
     */
    public static @NotNull SafeArrayList<Interpolation> getAvailable() {
        return INTERPOLATIONS;
    }

    /**
     * Get an interpolation by a name.
     *
     * @param name the name.
     * @return the interpolation.
     */
    public static @NotNull Interpolation getInterpolation(@NotNull final String name) {
        return requireNonNull(NAME_TO_INTER.get(name), "Unknown interpolation " + name);
    }

    /**
     * Get an ID of an interpolation.
     *
     * @param interpolation the interpolation.
     * @return its ID.
     */
    public static int getId(@NotNull final Interpolation interpolation) {
        return requireNonNull(INTER_TO_ID.get(interpolation), "Unknown interpolation " + interpolation);
    }

    /**
     * Get an interpolation by an ID.
     *
     * @param id the id.
     * @return the interpolation.
     */
    public static @NotNull Interpolation getInterpolation(final int id) {
        return requireNonNull(ID_TO_INTER.get(id), "Unknown id " + id);
    }
}
