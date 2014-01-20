/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import darwin.core.controls.*;
import darwin.core.timing.*;
import darwin.util.math.base.*;
import darwin.util.math.base.vector.*;
import darwin.util.math.composits.ViewMatrix;
import darwin.util.math.util.*;

import com.jogamp.newt.event.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class FPSController extends KeyAdapter implements ViewModel {

    private static final float SPEED = 120;
    private final ViewMatrix inverse = new ViewMatrix();
    private float x, y;
    public final Quaternion rotation = new Quaternion();
    public final Vector3 position = new Vector3();

    public FPSController() {
        inverse.loadIdentity();
        //rotation.setAxisAngle(Vector3.POS_X, 0);
    }

    @Override
    public ViewMatrix getView() {
        return inverse;
    }

    public void resetInverse() {
        inverse.loadIdentity();
        inverse.rotate(rotation);
        inverse.setWorldTranslate(position);
        
        inverse.inverse();
    }

    @Override
    public void dragged(float dx, float dy) {
        x += dy * SPEED;
        y += dx * SPEED;
        
        //rotation.setAxisAngle(Vector3.POS_Y, y);
        
        Quaternion rx = new Quaternion();
        //rx.setAxisAngle(Vector3.POS_X, x);
        
        rotation.mult(rx);
        
        resetInverse();
    }

    @Override
    public void steps(int steps, boolean ctrl, boolean shift) {
    }
    
    public void setPosition(float x, float y, float z){
        position.x = x;
        position.y = y;
        position.z = z;        
        resetInverse();
    }

    @Override
    public void resetView() {
        position.mul(0);
        x = y = 0;
        resetInverse();
    }

    @Override
    public void resetViewX() {
        position.x = 0;
        resetInverse();
    }

    @Override
    public void resetViewY() {
        position.y = 0;
        resetInverse();
    }

    @Override
    public void resetViewZ() {
        position.z = 0;
        resetInverse();
    }

    @Override
    public void addListener(ViewListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeListener(ViewListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
//    public static void main(String... args) {
//        
//        Quaternion q = new Quaternion();
//        q.setEularAngles(45, 0, 0);
//        
//        Quaternion x = new Quaternion();
//        x.setEularAngles(0, 90, 0);
//        
//        q = x.add(q).normalize();        
//        System.out.println(q);
//        System.out.println(q.mult(new Vector3(0, 0, -1)));
//        
//        q.mapVector(new Vector3(0, 0, -1), new Vector3(-1, 1, 0).normalize());
//        System.out.println(q);
//        System.out.println(q.mult(new Vector3(0, 0, -1)));
//        
//        Matrix4 m = new Matrix4();
//        m.loadIdentity();
//        m.rotateEuler(0, 90, 0);
//        m.rotateEuler(45, 0, 0);
//        q=m.getRotation().normalize();
//        System.out.println(q);
//        System.out.println(q.mult(new Vector3(0, 0, -1)));
//        System.out.println(m.fastMult(new Vector3(0, 0, -1)));
//    }
}
