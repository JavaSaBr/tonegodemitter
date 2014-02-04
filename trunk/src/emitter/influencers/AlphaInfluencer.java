package emitter.influencers;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.util.SafeArrayList;
import java.io.IOException;
import java.util.ArrayList;
import emitter.Interpolation;
import emitter.particle.ParticleData;

/**
 *
 * @author t0neg0d
 */
public class AlphaInfluencer implements ParticleInfluencer {
	private SafeArrayList<Float> alphas = new SafeArrayList(Float.class);
	private SafeArrayList<Interpolation> interpolations = new SafeArrayList(Interpolation.class);
	private boolean useRandomStartAlpha = false;
	private boolean initialized = false;
	private boolean enabled = true;
	private boolean cycle = false;;
	private float startAlpha = 1;
	private float endAlpha = 0;
	private float blend;
	private float fixedDuration = 0f;
	
	@Override
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			p.alphaInterval += tpf;
			if (p.alphaInterval >= p.alphaDuration)
				updateAlpha(p);
			
			blend = p.alphaInterpolation.apply(p.alphaInterval/p.alphaDuration);
			
			startAlpha = alphas.getArray()[p.alphaIndex];
			
			if (p.alphaIndex == alphas.size()-1)
				endAlpha = alphas.getArray()[0];
			else
				endAlpha = alphas.getArray()[p.alphaIndex+1];
			
			p.alpha = FastMath.interpolateLinear(blend, startAlpha, endAlpha);
		}
	}
	
	private void updateAlpha(ParticleData p) {
		p.alphaIndex++;
		if (p.alphaIndex >= alphas.size())
			p.alphaIndex = 0;
		p.alphaInterpolation = interpolations.getArray()[p.alphaIndex];
		p.alphaInterval -= p.alphaDuration;
	}
	
	@Override
	public void initialize(ParticleData p) {
		if (!initialized) {
			if (alphas.isEmpty()) {
				addAlpha(1f);
				addAlpha(0f);
			} else if (alphas.size() == 1) {
				setEnabled(false);
			}
			initialized = true;
		}
		if (useRandomStartAlpha) {
			p.alphaIndex = FastMath.nextRandomInt(0,alphas.size()-1);
		} else {
			p.alphaIndex = 0;
		}
		p.alphaInterval = 0f;
		p.alphaDuration = (cycle) ? fixedDuration : p.startlife/((float)alphas.size()-1);
		
		p.alpha = alphas.getArray()[p.alphaIndex];
		p.alphaInterpolation = interpolations.getArray()[p.alphaIndex];
	}

	@Override
	public void reset(ParticleData p) {
		p.alpha = 0;
	}
	
	/**
	 * Adds a alpha step value using linear interpolation to the chain of values used throughout the particles life span
	 * @param alpha 
	 */
	public void addAlpha(float alpha) {
		addAlpha(alpha, Interpolation.linear);
	}
	
	/**
	 * Adds a alpha step value to the chain of values used throughout the particles life span
	 * @param alpha
	 * @param interpolation 
	 */
	public void addAlpha(float alpha, Interpolation interpolation) {
		this.alphas.add(alpha);
		this.interpolations.add(interpolation);
	}
	
	/**
	 * Returns an array containing all alpha step values
	 * @return 
	 */
	public Float[] getAlphas() { return this.alphas.getArray(); }
	
	/**
	 * Returns an array containing all interpolation step values
	 * @return 
	 */
	public Interpolation[] getInterpolations() { return this.interpolations.getArray(); }
	
	/**
	 * Removes the alpha step value at the given index
	 * @param index 
	 */
	public void removeAlpha(int index) {
		this.alphas.remove(index);
		this.interpolations.remove(index);
	}
	
	/**
	 * Removes all added alpha step values
	 */
	public void removeAll() {
		this.alphas.clear();
		this.interpolations.clear();
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.writeSavableArrayList(new ArrayList(alphas), "alphas", null);
		oc.writeSavableArrayList(new ArrayList(interpolations), "interpolations", null);
		oc.write(enabled, "enabled", true);
		oc.write(useRandomStartAlpha, "useRandomStartAlpha", false);
		oc.write(cycle, "cycle", false);
		oc.write(fixedDuration, "fixedDuration", 0.125f);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		alphas = new SafeArrayList<Float>(Float.class, ic.readSavableArrayList("alphas", null));
		interpolations = new SafeArrayList<Interpolation>(Interpolation.class, ic.readSavableArrayList("interpolations", null));
		enabled = ic.readBoolean("enabled", true);
		useRandomStartAlpha = ic.readBoolean("useRandomStartAlpha", false);
		cycle = ic.readBoolean("cycle", false);
		fixedDuration = ic.readFloat("fixedDuration", 0.125f);
	}

	@Override
	public ParticleInfluencer clone() {
		try {
			AlphaInfluencer clone = (AlphaInfluencer) super.clone();
			clone.alphas.addAll(alphas);
			clone.interpolations.addAll(interpolations);
			clone.enabled = enabled;
			clone.useRandomStartAlpha = useRandomStartAlpha;
			clone.cycle = cycle;
			clone.fixedDuration = fixedDuration;
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
	
	/**
	 * Animated texture should cycle and use the provided duration between frames (0 diables cycling)
	 * @param fixedDuration duration between frame updates
	 */
	public void setFixedDuration(float fixedDuration) {
		if (fixedDuration != 0) {
			this.cycle = true;
			this.fixedDuration = fixedDuration;
		} else {
			this.cycle = false;
			this.fixedDuration = 0;
		}
	}
	/**
	 * Returns the current duration used between frames for cycled animation
	 * @return 
	 */
	public float getFixedDuration() { return this.fixedDuration; }

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public Class getInfluencerClass() {
		return AlphaInfluencer.class;
	}
}
