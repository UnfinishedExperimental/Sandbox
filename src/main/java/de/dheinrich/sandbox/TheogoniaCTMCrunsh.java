/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.*;

import darwin.geometrie.io.*;
import darwin.geometrie.io.ctm.*;
import darwin.geometrie.io.obj.ObjModelReader;
import darwin.geometrie.unpacked.Model;
import darwin.jopenctm.compression.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class TheogoniaCTMCrunsh {

    private final static Path MODEL_PATH = Paths.get("/home/daniel/Projekte/Theogonia/trunk/Resources/src/resources/Models");
    private final static Path OUT_PATH = Paths.get("/home/daniel/Projekte/Sandbox/test");

    private static void compress(Path obj, Path ctm) throws IOException, WrongFileTypeException {
        ModelReader reader = new ObjModelReader();
        ModelWriter writer = new CtmModelWriter(new MG1Encoder(), null, 9);


        Model[] model = reader.readModel(Files.newInputStream(obj));
        writer.writeModel(Files.newOutputStream(ctm), model);
    }

    public static void main(String... args) throws IOException, WrongFileTypeException, InterruptedException {
        DirectoryStream<Path> dir = Files.newDirectoryStream(MODEL_PATH, "*.obj");
        for (Path path : dir) {
            String name = path.getFileName().toString();
            name = name.substring(0, name.length() - 4) + ".zctm";
            final Path out = OUT_PATH.resolve(name);
            try {
                compress(path, out);
                System.out.println("compressed: " + path);
            } catch (Throwable ex) {
                System.out.println("failed to compress: " + path);
                ex.printStackTrace();
                try {
                    Files.delete(out);
                } catch (IOException ex1) {
                }
            }
        }
    }
}
