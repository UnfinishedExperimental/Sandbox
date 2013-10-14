/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class Test<E> {

    public Test() {
        E[] r = alloc(7);
        System.out.println(r.length);
        E[] rr = (E[]) new Test[10];
        System.out.println(rr.length);
    }

    final <H> H[] alloc(int size, H... a) {
        return Arrays.copyOf(a, size);
    }

    public static void main(String[] args) throws IOException {
        int count = 7;
        String[] cmd = {"sh", "-c", "echo \"" + count + " new attacks\" | festival --tts"};
        Runtime.getRuntime().exec(cmd);
    }
}
