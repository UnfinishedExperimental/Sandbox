/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import darwin.util.math.base.Quaternion;
import darwin.util.math.base.matrix.Matrix4;
import darwin.util.math.base.vector.Vector3;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class BufferTest {

    

    public static void main(String... args) {
        
//        quaternion.setAxisAngle(new Vector3(0, 1, 0), 45);
//
//        quaternion = quaternion.normalize();
//        Quaternion[] dual = quaternion.toDualQuaternion(new Vector3());
//
//        Vector3 pos = new Vector3(1, 0, 0);
//        float[] d0 = dual[0].toArray();
//        float[] d1 = dual[1].toArray();
//
//        Vector3 inner = new Vector3(d0[1], d0[2], d0[3]).cross(pos).add(pos.clone().mul(d0[0]));
//        Vector3 newPos = new Vector3(d0[1], d0[2], d0[3]).cross(inner).mul(2).add(pos);
//
//        System.out.println(newPos);
//        System.out.println(dual[0].getRotationMatrix().fastMult(pos));
//
//        Matrix4 m = new Matrix4();
//        m.loadIdentity();
//        m.rotateEuler(0, 45, 0);
//        System.out.println(pos);
//        System.out.println(m.fastMult(pos));
//        System.out.println(m.getRotation().mult(pos));
        
        
//    position = position + 2.0 * cross(dual[0].yzw, cross(dual[0].yzw, position) + dual[0].x*position);
//    vec3 trans = 2.0*(dual[0].x*dual[1].yzw - dual[1].x*dual[0].yzw + cross(dual[0].yzw, dual[1].yzw));
//    position += trans;
    }
}
