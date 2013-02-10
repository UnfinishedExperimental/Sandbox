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
    private float rx, ry, px, py;
    
    
    public FPSController() {
        matrix.loadIdentity();
    }

    @Override
    public ViewMatrix getView() {
        return matrix;
    }

    @Override
    public void dragged(float dx, float dy) {
        rx += 90 * dx;
        ry += 90 * dy;

        Quaternion q = new Quaternion();
        q.setEularAngles(ry, rx, 0);
                
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
}
