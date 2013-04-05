/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import java.lang.reflect.*;
import java.util.Arrays;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class Test<E extends Number> {

    void test1(E e) {
        try {
            @SuppressWarnings("unchecked")
            E[] a = alloc(3);
            a[0] = e;
            System.out.println("test1 win");
        } catch (Throwable t) {
            System.out.println("test1 fail");
        }
    }

    void test2(E e) {
        try {
            E b = null;
            E[] a = alloc(3, b);
            a[0] = e;
            System.out.println("test2 win");
        } catch (Throwable t) {
            System.out.println("test2 fail");
        }
    }

    void test3(E e) {
        try {
            E[] a = (E[]) new Number[3];
            a[0] = e;
            System.out.println("test3 win");
        } catch (Throwable t) {
            System.out.println("test2 fail");
        }
    }

    public  E[] alloc(int length, E... base) {
        System.out.println(base.getClass().getComponentType());
        return Arrays.copyOf(base, length);
    }

    void printGeneric(Class<?> c) {
        final Type superclass = c.getGenericSuperclass();

        final Type[] types = ((ParameterizedType) superclass).getActualTypeArguments();
        System.out.println(Arrays.toString(types));
    }

    public static void main(String[] args) {
        Test<Integer> a = new Test<Integer>();
        a.test1(3);
        a.test2(3);
        a.test3(3);
    }
}
