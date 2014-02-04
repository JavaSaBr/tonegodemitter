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
import com.jme3.math.ColorRGBA;
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
public class ColorInfluencer implements ParticleInfluencer {
	private SafeArrayList<ColorRGBA> colors = new SafeArrayList(ColorRGBA.class);
	private SafeArrayList<Interpolation> interpolations = new SafeArrayList(Interpolation.class);
	private boolean initialized = false;
	private boolean enabled = true;
	private boolean useRandomStartColor = false;
	private boolean cycle = false;
	private transient ColorRGBA resetColor = new ColorRGBA(0,0,0,0);
	private ColorRGBA startColor = new ColorRGBA().set(ColorRGBA.Red);
	private ColorRGBA endColor = new ColorRGBA().set(ColorRGBA.Yellow);
	private float blend;
	private float fixedDuration = 0f;
	
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			p.colorInterval += tpf;
			if (p.colorInterval >= p.colorDuration)
				updateColor(p);
			
			blend = p.colorInterpolation.apply(p.colorInterval/p.colorDuration);
			
			startColor.set(colors.getArray()[p.colorIndex]);
			
			if (p.colorIndex == colors.size()-1)
				endColor.set(colors.getArray()[0]);
			else
				endColor.set(colors.getArray()[p.colorIndex+1]);
			
			p.color.interpolate(startColor, endColor, blend);
		}
	}
	
	private void updateColor(ParticleData p) {
		p.colorIndex++;
	//	if (!cycle) {
			if (p.colorIndex >= colors.size())
				p.colorIndex = 0;
	//	} else {
	//		if (p.colorIndex >= colors.size()-1)
	//			p.colorIndex = 0;
	//	}
		p.colorInterpolation = interpolations.getArray()[p.colorIndex];
		p.colorInterval -= p.colorDuration;
	}
	
	public void initialize(ParticleData p) {
		if (!initialized) {
			if (colors.isEmpty()) {
				addColor(ColorRGBA.Red);
				addColor(ColorRGBA.Yellow);
			} else if (colors.size() == 1) {
				setEnabled(false);
			}
			initialized = true;
		}
		if (useRandomStartColor) {
			p.colorIndex = FastMath.nextRandomInt(0,colors.size()-1);
		} else {
			p.colorIndex = 0;
		}
		p.colorInterval = 0f;
		p.colorDuration = (cycle) ? fixedDuration : p.startlife/((float)colors.size()-1);
		
		p.color.set(colors.getArray()[p.colorIndex]);
		p.colorInterpolation = interpolations.getArray()[p.colorIndex];
	}

	public void reset(ParticleData p) {
		p.color.set(resetColor);
		p.colorIndex = 0;
		p.colorInterval = 0;
	}
	
	public void setUseRandomStartColor(boolean useRandomStartColor) {
		this.useRandomStartColor = useRandomStartColor;
	}
	
	public boolean getUseRandomStartColor() {
		return this.useRandomStartColor;
	}
	
	public void addColor(ColorRGBA color) {
		addColor(color, Interpolation.linear);
	}
	
	public void addColor(ColorRGBA color, Interpolation interpolation) {
		this.colors.add(color.clone());
		this.interpolations.add(interpolation);
	}
	
	public void removeColor(int index) {
		this.colors.remove(index);
		this.interpolations.remove(index);
	}
	
	public void removeAll() {
		this.colors.clear();
		this.interpolations.clear();
	}
	
	public ColorRGBA[] getColors() {
		return colors.getArray();
	}
	
	public Interpolation[] getInterpolations() {
		return interpolations.getArray();
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.writeSavableArrayList(new ArrayList(colors), "colors", null);
		oc.writeSavableArrayList(new ArrayList(interpolations), "interpolations", null);
		oc.write(enabled, "enabled", true);
		oc.write(useRandomStartColor, "useRandomStartColor", false);
		oc.write(cycle, "cycle", false);
		oc.write(fixedDuration, "fixedDuration", 0.125f);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		colors = new SafeArrayList<ColorRGBA>(ColorRGBA.class, ic.readSavableArrayList("colors", null));
		interpolations = new SafeArrayList<Interpolation>(Interpolation.class, ic.readSavableArrayList("interpolations", null));
		enabled = ic.readBoolean("enabled", true);
		useRandomStartColor = ic.readBoolean("useRandomStartColor", false);
		cycle = ic.readBoolean("cycle", false);
		fixedDuration = ic.readFloat("fixedDuration", 0.125f);
	}

	@Override
	public ParticleInfluencer clone() {
		try {
			ColorInfluencer clone = (ColorInfluencer) super.clone();
			clone.colors.addAll(colors);
			clone.interpolations.addAll(interpolations);
			clone.enabled = enabled;
			clone.useRandomStartColor = useRandomStartColor;
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
		return ColorInfluencer.class;
	}
}
