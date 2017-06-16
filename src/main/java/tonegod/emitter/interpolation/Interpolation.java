package tonegod.emitter.interpolation;

import com.jme3.export.Savable;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.Messages;
import tonegod.emitter.interpolation.impl.Bounce;
import tonegod.emitter.interpolation.impl.BounceIn;
import tonegod.emitter.interpolation.impl.BounceOut;
import tonegod.emitter.interpolation.impl.CircleInInterpolation;
import tonegod.emitter.interpolation.impl.CircleInterpolation;
import tonegod.emitter.interpolation.impl.CircleOutInterpolation;
import tonegod.emitter.interpolation.impl.Elastic;
import tonegod.emitter.interpolation.impl.ElasticIn;
import tonegod.emitter.interpolation.impl.ElasticOut;
import tonegod.emitter.interpolation.impl.Exp;
import tonegod.emitter.interpolation.impl.ExpIn;
import tonegod.emitter.interpolation.impl.ExpOut;
import tonegod.emitter.interpolation.impl.FadeInterpolation;
import tonegod.emitter.interpolation.impl.LinearInterpolation;
import tonegod.emitter.interpolation.impl.Pow;
import tonegod.emitter.interpolation.impl.PowIn;
import tonegod.emitter.interpolation.impl.PowOut;
import tonegod.emitter.interpolation.impl.SineInInterpolation;
import tonegod.emitter.interpolation.impl.SineInterpolation;
import tonegod.emitter.interpolation.impl.SineOutInterpolation;
import tonegod.emitter.interpolation.impl.Swing;
import tonegod.emitter.interpolation.impl.SwingIn;
import tonegod.emitter.interpolation.impl.SwingOut;

/**
 * The interface Interpolation.
 *
 * @author t0neg0d, JavaSaBr Based on original code from Nathan Sweet
 */
public interface Interpolation extends Savable {

    /**
     * The constant LINEAR.
     */
    Interpolation LINEAR = new LinearInterpolation(Messages.INTERPOLATION_LINEAR);
    /**
     * The constant FADE.
     */
    Interpolation FADE = new FadeInterpolation(Messages.INTERPOLATION_FADE);

    /**
     * The constant SINE.
     */
    Interpolation SINE = new SineInterpolation(Messages.INTERPOLATION_SINE);
    /**
     * The constant SINE_IN.
     */
    Interpolation SINE_IN = new SineInInterpolation(Messages.INTERPOLATION_SINE_IN);
    /**
     * The constant SINE_OUT.
     */
    Interpolation SINE_OUT = new SineOutInterpolation(Messages.INTERPOLATION_SINE_OUT);

    /**
     * The constant EXP_10.
     */
    Interpolation EXP_10 = new Exp(2, 10, Messages.INTERPOLATION_EXP_10);
    /**
     * The constant EXP_10_IN.
     */
    Interpolation EXP_10_IN = new ExpIn(2, 10, Messages.INTERPOLATION_EXP_10_IN);
    /**
     * The constant EXP_10_OUT.
     */
    Interpolation EXP_10_OUT = new ExpOut(2, 10, Messages.INTERPOLATION_EXP_10_OUT);

    /**
     * The constant EXP_5.
     */
    Interpolation EXP_5 = new Exp(2, 5, Messages.INTERPOLATION_EXP_5);
    /**
     * The constant EXP_5_IN.
     */
    Interpolation EXP_5_IN = new ExpIn(2, 5, Messages.INTERPOLATION_EXP_5_IN);
    /**
     * The constant EXP_5_OUT.
     */
    Interpolation EXP_5_OUT = new ExpOut(2, 5, Messages.INTERPOLATION_EXP_5_OUT);

    /**
     * The constant CIRCLE.
     */
    Interpolation CIRCLE = new CircleInterpolation(Messages.INTERPOLATION_CIRCLE);
    /**
     * The constant CIRCLE_IN.
     */
    Interpolation CIRCLE_IN = new CircleInInterpolation(Messages.INTERPOLATION_CIRCLE_IN);
    /**
     * The constant CIRCLE_OUT.
     */
    Interpolation CIRCLE_OUT = new CircleOutInterpolation(Messages.INTERPOLATION_CIRCLE_OUT);

    /**
     * The constant SWING.
     */
    Interpolation SWING = new Swing(1.5f, Messages.INTERPOLATION_SWING);
    /**
     * The constant SWING_IN.
     */
    Interpolation SWING_IN = new SwingIn(2f, Messages.INTERPOLATION_SWING_IN);
    /**
     * The constant SWING_OUT.
     */
    Interpolation SWING_OUT = new SwingOut(2f, Messages.INTERPOLATION_SWING_OUT);

    /**
     * The constant BOUNCE.
     */
    Interpolation BOUNCE = new Bounce(4, Messages.INTERPOLATION_BOUNCE);
    /**
     * The constant BOUNCE_IN.
     */
    Interpolation BOUNCE_IN = new BounceIn(4, Messages.INTERPOLATION_BOUNCE_IN);
    /**
     * The constant BOUNCE_OUT.
     */
    Interpolation BOUNCE_OUT = new BounceOut(4, Messages.INTERPOLATION_BOUNCE_OUT);

    /**
     * The constant POW_2.
     */
    Interpolation POW_2 = new Pow(2, Messages.INTERPOLATION_POW_2);
    /**
     * The constant POW_2_IN.
     */
    Interpolation POW_2_IN = new PowIn(2, Messages.INTERPOLATION_POW_2_IN);
    /**
     * The constant POW_2_OUT.
     */
    Interpolation POW_2_OUT = new PowOut(2, Messages.INTERPOLATION_POW_2_OUT);

    /**
     * The constant POW_3.
     */
    Interpolation POW_3 = new Pow(3, Messages.INTERPOLATION_POW_3);
    /**
     * The constant POW_3_IN.
     */
    Interpolation POW_3_IN = new PowIn(3, Messages.INTERPOLATION_POW_3_IN);
    /**
     * The constant POW_3_OUT.
     */
    Interpolation POW_3_OUT = new PowOut(3, Messages.INTERPOLATION_POW_3_OUT);

    /**
     * The constant POW_4.
     */
    Interpolation POW_4 = new Pow(4, Messages.INTERPOLATION_POW_4);
    /**
     * The constant POW_4_IN.
     */
    Interpolation POW_4_IN = new PowIn(4, Messages.INTERPOLATION_POW_4_IN);
    /**
     * The constant POW_4_OUT.
     */
    Interpolation POW_4_OUT = new PowOut(4, Messages.INTERPOLATION_POW_4_OUT);

    /**
     * The constant POW_5.
     */
    Interpolation POW_5 = new Pow(5, Messages.INTERPOLATION_POW_5);
    /**
     * The constant POW_5_IN.
     */
    Interpolation POW_5_IN = new PowIn(5, Messages.INTERPOLATION_POW_5_IN);
    /**
     * The constant POW_5_OUT.
     */
    Interpolation POW_5_OUT = new PowOut(5, Messages.INTERPOLATION_POW_5_OUT);

    /**
     * The constant ELASTIC.
     */
    Interpolation ELASTIC = new Elastic(2, 10, Messages.INTERPOLATION_ELASTIC);
    /**
     * The constant ELASTIC_IN.
     */
    Interpolation ELASTIC_IN = new ElasticIn(2, 10, Messages.INTERPOLATION_ELASTIC_IN);
    /**
     * The constant ELASTIC_OUT.
     */
    Interpolation ELASTIC_OUT = new ElasticOut(2, 10, Messages.INTERPOLATION_ELASTIC_OUT);

    /**
     * Apply float.
     *
     * @param a blend value between 0 and 1.
     * @return the float
     */
    float apply(float a);

    /**
     * Apply float.
     *
     * @param start the start
     * @param end   the end
     * @param a     blend value between 0 and 1.
     * @return the float
     */
    default float apply(float start, float end, float a) {
        return start + (end - start) * apply(a);
    }

    /**
     * Gets name.
     *
     * @return the name of this interpolation.
     */
    @NotNull
    String getName();
}
