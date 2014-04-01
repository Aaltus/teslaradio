/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package effects;

import com.jme3.asset.AssetManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author David
 */
public class SignalEmitter extends Node{
    
    private List<Signal> signals = new ArrayList<Signal>();
    private List<Vector3f> paths = new ArrayList<Vector3f>();
    private Geometry particle;
    
    
    public SignalEmitter(List<Vector3f> paths, Geometry particle) {
        this.paths = paths;
        this.particle = particle;
        
    }
    
    public void simpleUpdate(float tpf) {
        for (Signal signal : signals) {
            signal.updatePosition(tpf);
        }
    }

    public void emitParticles() {
        for (Vector3f path : paths) {
            Signal mySignal = new Signal(particle, path, 10f);
            this.attachChild(mySignal);
            signals.add(mySignal);
        }
    }
    
}
