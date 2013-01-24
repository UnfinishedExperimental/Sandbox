/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class Test {

    private static class A {

        static {
            System.out.println("static init of A");
        }

        {
            System.out.println("init of A");
        }

        public A() {
            System.out.println("constructor of A");
        }
    }

    private static class B extends A {

        static {
            System.out.println("static init of B");
        }

        {
            System.out.println("init of B");
        }

        public B() {
            System.out.println("constructor of B");
        }
    }
}
