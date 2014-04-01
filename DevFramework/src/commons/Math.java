/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package commons;

import com.jme3.math.Vector3f;

/**
 *
 * @author David
 */
public class Math {
    
    public static float SpeedToDurationAlongVector(float speed, Vector3f vector) {
        return speed/vector.length();
    }
    
    public static Vector3f VectorToUnitary(Vector3f vector) {
        Vector3f unitary = new Vector3f();
        unitary.x = vector.x/vector.length();
        unitary.y = vector.y/vector.length();
        unitary.z = vector.z/vector.length();
        return unitary;
    }
    
}
