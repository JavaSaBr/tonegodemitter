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
import com.jme3.math.Vector3f;
import com.jme3.util.SafeArrayList;
import java.io.IOException;
import java.util.ArrayList;
import emitter.Interpolation;
import emitter.particle.ParticleData;

/**
 *
 * @author t0neg0d
 */
public class DirectionInfluencer implements ParticleInfluencer {
	private SafeArrayList<Vector3f> directions = new SafeArrayList(Vector3f.class);
	private SafeArrayList<Interpolation> interpolations = new SafeArrayList(Interpolation.class);
	private boolean enabled = true;
	private float blend;
	private boolean useRandomStartDirection = false;
	private float weight = 1;
	private float chance = 1;
	private boolean cycle = false;
	private float fixedDuration = 0f;
	private Vector3f tempV3a = new Vector3f(), tempV3b = new Vector3f();
	private float len;
	
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			p.directionInterval += tpf;
			if (p.directionInterval >= p.directionDuration)
				updateDirection(p);

			blend = p.directionInterpolation.apply(p.directionInterval/p.directionDuration);

			len = p.velocity.length();
			tempV3a.set(p.velocity).normalizeLocal();
			tempV3b.set(directions.getArray()[p.directionIndex]).normalizeLocal();
		}
	}
	
	private void updateDirection(ParticleData p) {
		p.directionIndex++;
		if (p.directionIndex == directions.size())
			p.directionIndex = 0;
		
		len = p.velocity.length();
		p.velocity.set(directions.getArray()[p.directionIndex].normalize()).multLocal(len);
		p.directionInterpolation = interpolations.getArray()[p.directionIndex];
		p.directionInterval -= p.directionDuration;
	}
	
	public void initialize(ParticleData p) {
		if (useRandomStartDirection) {
			p.directionIndex = FastMath.nextRandomInt(0,directions.size()-1);
		} else {
			p.directionIndex = 0;
		}
		p.directionInterval = 0f;
		p.directionDuration = (cycle) ? fixedDuration : p.startlife/((float)directions.size());
		
		p.directionInterpolation = interpolations.getArray()[p.directionIndex];
	}

	public void reset(ParticleData p) {
		
	}
	
	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	public float getWeight() { return this.weight; }
	
	public void setChance(float chance) {
		this.chance = chance;
	}
	
	public float getChance() { return this.chance; }
	
	public void setUseRandomStartDirection(boolean useRandomStartDirection) {
		this.useRandomStartDirection = useRandomStartDirection;
	}
	
	public void addDirection(Vector3f direction) {
		addDirection(direction, Interpolation.linear);
	}
	
	public void addDirection(Vector3f direction, Interpolation interpolation) {
		this.directions.add(direction.clone());
		this.interpolations.add(interpolation);
	}
	
	public void removeDirection(int index) {
		this.directions.remove(index);
		this.interpolations.remove(index);
	}
	
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.writeSavableArrayList(new ArrayList(directions), "directions", null);
		oc.writeSavableArrayList(new ArrayList(interpolations), "interpolations", null);
		oc.write(enabled, "enabled", true);
		oc.write(useRandomStartDirection, "useRandomStartDirection", false);
		oc.write(cycle, "cycle", false);
		oc.write(weight, "weight", 1f);
		oc.write(chance, "chance", 1f);
		oc.write(fixedDuration, "fixedDuration", 0.125f);
	}

	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		directions = new SafeArrayList<Vector3f>(Vector3f.class, ic.readSavableArrayList("directions", null));
		interpolations = new SafeArrayList<Interpolation>(Interpolation.class, ic.readSavableArrayList("interpolations", null));
		enabled = ic.readBoolean("enabled", true);
		useRandomStartDirection = ic.readBoolean("useRandomStartDirection", false);
		cycle = ic.readBoolean("cycle", false);
		weight = ic.readFloat("weight", 1f);
		chance = ic.readFloat("chance", 1f);
		fixedDuration = ic.readFloat("fixedDuration", 0.125f);
	}

	@Override
	public ParticleInfluencer clone() {
		try {
			DirectionInfluencer clone = (DirectionInfluencer) super.clone();
			clone.directions.addAll(directions);
			clone.interpolations.addAll(interpolations);
			clone.enabled = enabled;
			clone.useRandomStartDirection = useRandomStartDirection;
			clone.weight = weight;
			clone.chance = chance;
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
		return DirectionInfluencer.class;
	}
}