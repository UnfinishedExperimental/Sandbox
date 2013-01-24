/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox.g2d;

import java.awt.Image;

import darwin.annotations.ServiceProvider;
import darwin.resourcehandling.factory.*;

import de.dheinrich.sandbox.g2d.ImageLoader.Scale;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
@ServiceProvider(ResourceFromHandleProvider.class)
public class ImageLoaderProvider extends ResourceFromHandleProvider<Image> {

    public ImageLoaderProvider() {
        super(Image.class);
    }

    @Override
    public ResourceFromHandle<Image> get(String[] options) {
        Scale scale = null;
        for (String o : options) {
            String[] s = o.split(":");
            if (s.length == 2 && s[0].equals("scale")) {
                try {
                    if (s[1].endsWith("px")) {
                        String n = s[1].substring(0, s[1].length() - 2);
                        float v = Float.parseFloat(n);
                        scale = new Scale(true, v);
                    } else if (s[1].endsWith("%")) {
                        String n = s[1].substring(0, s[1].length() - 1);
                        float v = Float.parseFloat(n);
                        scale = new Scale(false, v);
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return new ImageLoader(scale);
    }
}
