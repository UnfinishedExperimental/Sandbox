/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import darwin.core.controls.*;
import darwin.util.math.base.Quaternion;
import darwin.util.math.base.matrix.Matrix4;
import darwin.util.math.base.vector.Vector3;
import darwin.util.math.composits.ViewMatrix;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class FPSController implements ViewModel {

    private final ViewMatrix matrix = new ViewMatrix();

    public FPSController() {
        matrix.loadIdentity();
    }

    @Override
    public ViewMatrix getView() {
        return matrix;
    }
    float x, y;

    @Override
    public void dragged(float dx, float dy) {
        x += 90 * dx;
        y += 90 * dy;

        Quaternion q = new Quaternion();
        q.setEularAngles(y, x, 0);
                
        Vector3 up = q.mult(new Vector3(0, 1, 0));
        Vector3 target = q.mult(new Vector3(0, 0, -1)).add(matrix.getTranslation());
                
        matrix.lookAt(matrix.getTranslation(), target, up);
    }

    @Override
    public void steps(int steps, boolean ctrl, boolean shift) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resetView() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resetViewX() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resetViewY() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resetViewZ() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addListener(ViewListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeListener(ViewListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static void main(String... args) {
        
        Quaternion q = new Quaternion();
        q.setEularAngles(45, 0, 0);
        
        Quaternion x = new Quaternion();
        x.setEularAngles(0, 90, 0);
        
        q = x.add(q).normalize();        
        System.out.println(q);
        System.out.println(q.mult(new Vector3(0, 0, -1)));
        
        q.mapVector(new Vector3(0, 0, -1), new Vector3(-1, 1, 0).normalize());
        System.out.println(q);
        System.out.println(q.mult(new Vector3(0, 0, -1)));
        
        Matrix4 m = new Matrix4();
        m.loadIdentity();
        m.rotateEuler(0, 90, 0);
        m.rotateEuler(45, 0, 0);
        q=m.getRotation().normalize();
        System.out.println(q);
        System.out.println(q.mult(new Vector3(0, 0, -1)));
        System.out.println(m.fastMult(new Vector3(0, 0, -1)));
    }
}
