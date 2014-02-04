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
public class DestinationInfluencer implements ParticleInfluencer {
	private SafeArrayList<Vector3f> destinations = new SafeArrayList(Vector3f.class);
	private SafeArrayList<Float> weights = new SafeArrayList(Float.class);
	private SafeArrayList<Interpolation> interpolations = new SafeArrayList(Interpolation.class);
	private boolean enabled = true;
	private boolean initialized = false;
	private float blend;
	private boolean useRandomStartDestination = false;
//	private boolean particleMovesTowardsDestination = true;
	private float weight = 1f;
	private boolean cycle = false;;
	private float fixedDuration = 0f;
	private Vector3f destinationDir = new Vector3f();
	private float dist;
	
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			p.destinationInterval += tpf;
			if (p.destinationInterval >= p.destinationDuration)
				updateDestination(p);

			blend = p.destinationInterpolation.apply(p.destinationInterval/p.destinationDuration);

			destinationDir.set(destinations.getArray()[p.destinationIndex].subtract(p.position));
			dist = p.position.distance(destinations.getArray()[p.destinationIndex]);
			destinationDir.multLocal(dist);
			
			weight = weights.getArray()[p.destinationIndex];
			
			p.velocity.interpolate(destinationDir, blend*tpf*(weight*10));
		}
	}
	
	private void updateDestination(ParticleData p) {
		p.destinationIndex++;
		if (p.destinationIndex == destinations.size())
			p.destinationIndex = 0;
		
		p.destinationInterpolation = interpolations.getArray()[p.destinationIndex];
		p.destinationInterval -= p.destinationDuration;
	}
	
	public void initialize(ParticleData p) {
		if (!initialized) {
			if (destinations.isEmpty()) {
				addDestination(new Vector3f(0,0,0),0.5f);
			}
			initialized = true;
		}
		if (useRandomStartDestination) {
			p.destinationIndex = FastMath.nextRandomInt(0,destinations.size()-1);
		} else {
			p.destinationIndex = 0;
		}
		p.destinationInterval = 0f;
		p.destinationDuration = (cycle) ? fixedDuration : p.startlife/((float)destinations.size());
		
		p.destinationInterpolation = interpolations.getArray()[p.destinationIndex];
	}

	public void reset(ParticleData p) {
		
	}
	
	public void setUseRandomStartDestination(boolean useRandomStartDestination) {
		this.useRandomStartDestination = useRandomStartDestination;
	}
	
	public boolean getUseRandomStartDestination() { return this.useRandomStartDestination; }
	
	public void addDestination(Vector3f destination, float weight) {
		addDestination(destination, weight, Interpolation.linear);
	}
	
	public void addDestination(Vector3f destination, float weight, Interpolation interpolation) {
		this.destinations.add(destination.clone());
		this.weights.add(weight);
		this.interpolations.add(interpolation);
	}
	
	public void removeDestination(int index) {
		this.destinations.remove(index);
		this.weights.remove(index);
		this.interpolations.remove(index);
	}
	
	public void removeAll() {
		this.destinations.clear();
		this.weights.clear();
		this.interpolations.clear();
	}
	
	public Vector3f[] getDestinations() { return this.destinations.getArray(); }
	public Interpolation[] getInterpolations() { return this.interpolations.getArray(); }
	public Float[] getWeights() { return this.weights.getArray(); }
	
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.writeSavableArrayList(new ArrayList(destinations), "destinations", null);
		oc.writeSavableArrayList(new ArrayList(weights), "weightss", null);
		oc.writeSavableArrayList(new ArrayList(interpolations), "interpolations", null);
		oc.write(enabled, "enabled", true);
		oc.write(useRandomStartDestination, "useRandomStartDestination", false);
	//	oc.write(particleMovesTowardsDestination, "particleMovesTowardsDestination", true);
		oc.write(cycle, "cycle", false);
		oc.write(fixedDuration, "fixedDuration", 0.125f);
	}

	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		destinations = new SafeArrayList<Vector3f>(Vector3f.class, ic.readSavableArrayList("destinations", null));
		weights = new SafeArrayList<Float>(Float.class, ic.readSavableArrayList("weights", null));
		interpolations = new SafeArrayList<Interpolation>(Interpolation.class, ic.readSavableArrayList("interpolations", null));
		enabled = ic.readBoolean("enabled", true);
		useRandomStartDestination = ic.readBoolean("useRandomStartDestination", false);
	//	particleMovesTowardsDestination = ic.readBoolean("particleMovesTowardsDestination", true);
		cycle = ic.readBoolean("cycle", false);
		fixedDuration = ic.readFloat("fixedDuration", 0.125f);
	}

	@Override
	public ParticleInfluencer clone() {
		try {
			DestinationInfluencer clone = (DestinationInfluencer) super.clone();
			clone.destinations.addAll(destinations);
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
		return DestinationInfluencer.class;
	}
}
