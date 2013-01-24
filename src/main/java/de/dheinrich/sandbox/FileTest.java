/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.*;

import darwin.renderer.shader.CompiledShader;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class FileTest {

    public static void main(String... args) throws IOException, URISyntaxException, InterruptedException {

        CompiledShader[] shaders = new CompiledShader[200];
        for (int i = 0; i < shaders.length; i++) {
            shaders[i] = newRandomShader();
        }

        Path file = Paths.get("testLib.zip");

        try (ZipOutputStream o = new ZipOutputStream(Files.newOutputStream(file))) {
            for (int i = 0; i < shaders.length; i++) {
                o.putNextEntry(new ZipEntry("Shader" + i + ".sbin"));
                ObjectOutputStream oo = new ObjectOutputStream(o);
                oo.writeObject(shaders[i]);
            }
        }
    }
    static Random rnd = ThreadLocalRandom.current();

    private static CompiledShader newRandomShader() {
        int format = rnd.nextInt();
        byte[] data = new byte[(rnd.nextInt(40) + 10)*1024];
        rnd.nextBytes(data);
        return new CompiledShader(format, ByteBuffer.wrap(data));
    }

    private static float byteToMb(long bytes) {
        return bytes / (1024f * 1024f);
    }
}
