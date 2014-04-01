/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package effects;

import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author David
 */
public class SignalEmitter {
    
    private ArrayList<Signal> signals;
    private ArrayList<Vector3f> paths;
    
    public SignalEmitter(ArrayList<Vector3f> paths) {
        this.paths = paths;
    }
    
    public void simpleUpdate(float tpf) {
        for (Signal signal : signals) {
            signal.updatePosition(tpf);
        }
    }

    public void emitParticles(String name, boolean isPressed, float tpf) {
        for (Vector3f path : paths) {
            Signal mySignal = new Signal(SignalType.Sound, path, 1.0f);
            signals.add(mySignal);
        }
    }
    
}
