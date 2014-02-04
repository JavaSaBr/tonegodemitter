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
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.io.IOException;
import emitter.particle.ParticleData;

/**
 *
 * @author t0neg0d
 */
public class RadialVelocityInfluencer implements ParticleInfluencer {
	public static enum RadialPullAlignment {
		Emission_Point,
		Emitter_Center
	}
	public static enum RadialPullCenter {
		Absolute,
		Variable_X,
		Variable_Y,
		Variable_Z
	}
	public static enum RadialUpAlignment {
		Normal,
		UNIT_X,
		UNIT_Y,
		UNIT_Z
	}
	private boolean enabled = true;
	private float radialPull = 1, tangentForce = 1;
	private Vector3f tangent = new Vector3f();
	private Vector3f store = new Vector3f();
	private Vector3f up = Vector3f.UNIT_Y.clone(), left = new Vector3f();
	private Vector3f upStore = new Vector3f();
	private boolean useRandomDirection = false;
	private RadialPullAlignment alignment = RadialPullAlignment.Emission_Point;
	private RadialPullCenter center = RadialPullCenter.Absolute;
	private RadialUpAlignment upAlignment = RadialUpAlignment.UNIT_Y;
	private Quaternion q = new Quaternion();
	
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			switch (alignment) {
				case Emission_Point:
					p.emitter.getShape().setNext(p.triangleIndex);
					if (p.emitter.getUseRandomEmissionPoint())
						store.set(p.emitter.getShape().getNextTranslation().addLocal(p.randomOffset));
					else
						store.set(p.emitter.getShape().getNextTranslation());
					break;
				case Emitter_Center:
					store.set(p.emitter.getShape().getMesh().getBound().getCenter());
					break;
			}
			
			switch (center) {
				case Absolute:
					break;
				case Variable_X:
					store.setX(p.position.x);
					break;
				case Variable_Y:
					store.setY(p.position.y);
					break;
				case Variable_Z:
					store.setZ(p.position.z);
					break;
			}
			
			store.subtractLocal(p.position).normalizeLocal().multLocal(p.initialLength*radialPull).multLocal(tpf);
			
			switch (upAlignment) {
				case Normal:
					upStore.set(p.emitter.getLocalRotation().inverse().mult(upStore.set(p.emitter.getShape().getNormal())));
					break;
				case UNIT_X:
					upStore.set(Vector3f.UNIT_X);
					break;
				case UNIT_Y:
					upStore.set(Vector3f.UNIT_Y);
					break;
				case UNIT_Z:
					upStore.set(Vector3f.UNIT_Z);
					break;
			}
			
			up.set(store).crossLocal(upStore).normalizeLocal();
			up.set(p.emitter.getLocalRotation().mult(up));
			left.set(store).crossLocal(up).normalizeLocal();

			tangent.set(store).crossLocal(left).normalizeLocal().multLocal(p.tangentForce).multLocal(tpf);
			p.velocity.subtractLocal(tangent);
			p.velocity.addLocal(store.mult(radialPull));
		}
	}
	
	public void initialize(ParticleData p) {
		if (useRandomDirection) {
			if (FastMath.rand.nextBoolean())
				p.tangentForce = tangentForce;
			else
				p.tangentForce = -tangentForce;
		} else
			p.tangentForce = tangentForce;
	}

	public void reset(ParticleData p) {
		
	}
	
	public void setTangentForce(float force) {
		this.tangentForce = force;
	}
	
	public float getTangentForce() {
		return this.tangentForce;
	}
	
	public void setRadialPullAlignment(RadialPullAlignment alignment) {
		this.alignment = alignment;
	}
	
	public RadialPullAlignment getRadialPullAlignment() {
		return this.alignment;
	}
	
	public void setRadialPullCenter(RadialPullCenter center) {
		this.center = center;
	}
	
	public RadialPullCenter getRadialPullCenter() {
		return this.center;
	}
	
	public void setRadialPull(float radialPull) {
		this.radialPull = radialPull;
	}
	
	public float getRadialPull() {
		return this.radialPull;
	}
	
	public void setRadialUpAlignment(RadialUpAlignment upAlignment) {
		this.upAlignment = upAlignment;
	}
	
	public RadialUpAlignment getRadialUpAlignment() {
		return this.upAlignment;
	}
	
	public void setUseRandomDirection(boolean useRandomDirection) {
		this.useRandomDirection = useRandomDirection;
	}
	
	public boolean getUseRandomDirection() { return this.useRandomDirection; }
	
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
	}

	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
	}

	@Override
	public ParticleInfluencer clone() {
		try {
			RadialVelocityInfluencer clone = (RadialVelocityInfluencer) super.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public Class getInfluencerClass() {
		return RadialVelocityInfluencer.class;
	}
}
