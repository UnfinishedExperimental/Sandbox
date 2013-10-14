/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import java.util.Random;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class SobelSpeedTest {

    final int WIDTH = 640;
    final int HEIGHT = 480;

    void test() {
        short[] img = creatRandomImage();

        int c = 0;
        for (int i = 0; i < 100; i++) {
            c += sobel(img).length;
        }

        int count = 1000;
        long time = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            c += sobel(img).length;
        }
        System.out.println((float) (System.currentTimeMillis() - time) / count);
        System.out.println(c);
    }

    short[] creatRandomImage() {
        short[] i = new short[WIDTH * HEIGHT];
        Random r = new Random();
        for (int j = 0; j < i.length; j++) {
            i[j] = (short) r.nextInt();
        }
        return i;
    }
    int[] c = new int[]{1, 2, 1};

    byte[] sobel(short[] a) {
        byte[] r = new byte[a.length];

        for (int x = 1; x < WIDTH - 1; x++) {
            for (int y = 1; y < HEIGHT - 1; y++) {
                float sobelx = 0;
                for (int i = 0; i < 3; i++) {
                    sobelx += getNormalizedDepth(a, x - 1, y - 1 + i) * c[i];
                    sobelx -= getNormalizedDepth(a, x + 1, y - 1 + i) * c[i];
                }
                sobelx /= 4;

                float sobely = 0;
                for (int i = 0; i < 3; i++) {
                    sobely += getNormalizedDepth(a, x - 1 + i, y - 1) * c[i];
                    sobely -= getNormalizedDepth(a, x - 1 + i, y + 1) * c[i];
                }
                sobely /= 4;

                double val = Math.sqrt(sobelx * sobelx + sobely * sobely);
                r[WIDTH * y + x] = (byte) (val * 255);
            }
        }

        return r;
    }
    final short MAX = 1 << 12;

    float getNormalizedDepth(short[] a, int x, int y) {
        x = Math.min(Math.max(x, 0), WIDTH);
        y = Math.min(Math.max(y, 0), HEIGHT);

        int i = WIDTH * y + x;
        return (float) (a[i] >> 3) / MAX;
    }

    public static void main(String... args) {
        new SobelSpeedTest().test();
    }
}
