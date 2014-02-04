/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emitter.shapes;

import com.jme3.scene.shape.Sphere;

/**
 *
 * @author t0neg0d
 */
public class SphereEmitterShape extends Sphere {
	
	public SphereEmitterShape(int divisions, float radius) {
		super(divisions,divisions,radius);
	}
}
