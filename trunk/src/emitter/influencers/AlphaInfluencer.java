/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
	//	if (!cycle) {
			if (p.alphaIndex >= alphas.size())
				p.alphaIndex = 0;
	//	} else {
	//		if (p.alphaIndex >= alphas.size()-1)
	//			p.alphaIndex = 0;
	//	}
		p.alphaInterpolation = interpolations.getArray()[p.alphaIndex];
		p.alphaInterval -= p.alphaDuration;
	}
	
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

	public void reset(ParticleData p) {
		p.alpha = 0;
	}
	
	public void addAlpha(float alpha) {
		addAlpha(alpha, Interpolation.linear);
	}
	
	public void addAlpha(float alpha, Interpolation interpolation) {
		this.alphas.add(alpha);
		this.interpolations.add(interpolation);
	}
	
	public Float[] getAlphas() { return this.alphas.getArray(); }
	
	public Interpolation[] getInterpolations() { return this.interpolations.getArray(); }
	
	public void removeAlpha(int index) {
		this.alphas.remove(index);
		this.interpolations.remove(index);
	}
	
	public void removeAll() {
		this.alphas.clear();
		this.interpolations.clear();
	}
	
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.writeSavableArrayList(new ArrayList(alphas), "alphas", null);
		oc.writeSavableArrayList(new ArrayList(interpolations), "interpolations", null);
		oc.write(enabled, "enabled", true);
		oc.write(useRandomStartAlpha, "useRandomStartAlpha", false);
		oc.write(cycle, "cycle", false);
		oc.write(fixedDuration, "fixedDuration", 0.125f);
	}

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

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public Class getInfluencerClass() {
		return AlphaInfluencer.class;
	}
}
