/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox.g2d;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import darwin.resourcehandling.factory.ResourceFromHandle;
import darwin.resourcehandling.handle.ResourceHandle;
import darwin.util.image.ImageUtil2;

import javax.imageio.ImageIO;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class ImageLoader implements ResourceFromHandle<Image> {

    public static class Scale {

        private final boolean absolute;
        private final float value;

        public Scale(boolean absolute, float value) {
            this.absolute = absolute;
            this.value = value;
        }
    }
    private final Scale scaleOption;

    public ImageLoader(Scale scaleOption) {
        this.scaleOption = scaleOption;
    }

    @Override
    public Image create(ResourceHandle handle) throws IOException {
        BufferedImage img = ImageIO.read(handle.getStream());
        if (scaleOption == null) {
            return img;
        }

        float scale = scaleOption.value;
        if (scaleOption.absolute) {
            scale /= img.getWidth();
        }

        return ImageUtil2.getScaledImage(img, Math.round(img.getWidth() * scale),
                                         Math.round(img.getHeight() * scale), true);
    }

    @Override
    public void update(ResourceHandle changed, Image wrapper) {
    }

    @Override
    public Image getFallBack() {
        return new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
    }
}
