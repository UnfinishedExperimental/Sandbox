/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import darwin.core.controls.*;
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
        
    @Override
    public void dragged(float dx, float dy) {
        float x = 90*dx;
        float y = 90*dy;
        
        matrix.rotateEuler(0, x, 0);
        matrix.rotateEuler(y, 0, 0);
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
        Matrix4 m = new Matrix4();
        m.loadIdentity();
        m.translate(0, 0, -1);
        m.rotateEuler(90, 0, 0);        
        
        System.out.println(m.fastMult(new Vector3()));
    }
}
