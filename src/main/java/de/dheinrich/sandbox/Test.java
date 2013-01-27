/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import darwin.util.math.base.vector.ImmutableVector;
import darwin.util.math.base.vector.Vector3;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class Test {

    public final ImmutableVector<Vector3> a;

    public Test(ImmutableVector<Vector3> a) {
        this.a = a;
    }

    @Override
    public String toString() {
        return a.toString();
    }

    public static void main(String[] args) {
        Vector3 vector3 = new Vector3(1, 2, 3);
        Test test = new Test(vector3);
        System.out.println(test);
        vector3.add(3);
        System.out.println(test);
    }
}
