/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox.g2d;

import java.awt.*;
import java.awt.image.*;

import darwin.core.timing.*;
import darwin.resourcehandling.dependencies.*;
import darwin.resourcehandling.dependencies.annotation.InjectResource;

import com.google.inject.Guice;
import de.darwin.autoatlas.TextureAtlasElement;
import javax.inject.Inject;
import javax.swing.JFrame;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class SmallGame extends JFrame {

    private static class Ball {

        @InjectResource(file = "resources/textures/pea.png")
        private TextureAtlasElement normal;
        @InjectResource(file = "resources/textures/peaglow.png")
        private TextureAtlasElement glow;
        
        private int x, y;
        private boolean isGlowing;

        public void draw(Graphics2D g) {
            g.drawImage(isGlowing ? glow.getSubImage() : normal.getSubImage(), x, y, null);
        }
    }
    private final Ball ball;

    @Inject
    public SmallGame(Ball ball) throws HeadlessException {
        super("SmallGame");
        this.ball = ball;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIgnoreRepaint(true);
        setBounds(0, 0, 800, 600);
        setResizable(false);

        setVisible(true);
        createBufferStrategy(2);
    }

    public void start() {
        ball.y = 200;

        GameTime time = new GameTime();
        time.addListener(100, new StepListener() {
            private Ball b = ball;

            @Override
            public void update(int tickCount, float lerp, float tickTimeSpan) {
                for (int i = 0; i < tickCount; i++) {
                    b.x += 1;
                    if (b.x > 400) {
                        b.x = 0;
                        b.isGlowing ^= true;
                    }
                }
            }
        });

        BufferStrategy bs = getBufferStrategy();
        while (true) {
            time.update();
            Graphics2D drawGraphics = (Graphics2D) bs.getDrawGraphics();
            drawGraphics.clearRect(0, 0, 800, 600);

            ball.draw(drawGraphics);

            bs.show();
            Toolkit.getDefaultToolkit().sync();
        }
    }

    public static void main(String... args) {
        Guice.createInjector(new ResourceHandlingModul())
                .getInstance(SmallGame.class).start();
    }
}
