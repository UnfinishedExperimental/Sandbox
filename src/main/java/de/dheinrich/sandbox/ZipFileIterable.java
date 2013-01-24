/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class ZipFileIterable extends ZipFile implements Iterable<ZipEntry> {

    public ZipFileIterable(File file) throws ZipException, IOException {
        super(file);
    }

    @Override
    public Iterator<ZipEntry> iterator() {
        return new Iterator<ZipEntry>() {
            Enumeration<? extends ZipEntry> e = entries();

            @Override
            public boolean hasNext() {
                return e.hasMoreElements();
            }

            @Override
            public ZipEntry next() {
                return e.nextElement();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
}
