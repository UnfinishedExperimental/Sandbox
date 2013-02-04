/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import de.dheinrich.sandbox.meshstuff.MeshUtil;
import java.nio.charset.Charset;
import java.nio.file.*;

import darwin.core.controls.*;
import darwin.core.gui.*;
import darwin.core.timing.*;
import darwin.geometrie.unpacked.*;
import darwin.renderer.geometrie.packed.RenderModel;
import darwin.renderer.geometrie.packed.RenderModel.RenderModelFactory;
import darwin.renderer.shader.*;
import darwin.resourcehandling.dependencies.annotation.*;
import darwin.resourcehandling.shader.ShaderLoader;
import darwin.util.math.base.Quaternion;
import darwin.util.math.base.vector.*;
import darwin.util.math.composits.AABB;
import darwin.util.math.util.*;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.jogamp.newt.event.*;
import de.dheinrich.sandbox.meshstuff.*;
import javax.inject.Inject;
import javax.media.opengl.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class ResourceLoading implements GLEventListener {

    @InjectBundle(files = {"sphere.frag", "sphere.vert"}, prefix = ShaderLoader.SHADER_PATH_PREFIX)
    private Shader sphereS;
    @InjectBundle(files = {"simple.frag", "simple.vert"}, prefix = ShaderLoader.SHADER_PATH_PREFIX)
    private Shader shader;
    @InjectResource(file = "crytek-sponza/sponza.ctm")
    private Model[] test;
    @Inject
    private RenderModelFactory modeler;
    private RenderModel model, sphere;
    private MatrixCache cache = new MatrixCache();
    private GameTime time = new GameTime();
    private boolean forward, backward, left, right;

    public static void main(String[] args) throws InstantiationException {
        boolean debug = true;

        Client client = Client.createClient(debug);
        ClientWindow window = new ClientWindow(1000, 500, false, client);
        window.startUp();

        final ResourceLoading a = client.addGLEventListener(ResourceLoading.class);

        ViewModel vm = new FPSController();
        a.cache.setView(vm.getView());
        client.addMouseListener(new InputController(vm, null, a.cache));
        client.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent ke) {
                switch (ke.getKeyCode()) {
                    case 'W':
                        a.forward = false;
                        break;
                    case 'A':
                        a.left = false;
                        break;
                    case 'S':
                        a.backward = false;
                        break;
                    case 'D':
                        a.right = false;
                        break;
                }
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                switch (ke.getKeyCode()) {
                    case 'W':
                        a.forward = true;
                        break;
                    case 'A':
                        a.left = true;
                        break;
                    case 'S':
                        a.backward = true;
                        break;
                    case 'D':
                        a.right = true;
                        break;
                }
            }
        });
//        client.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseMoved(MouseEvent me) {
//                float lerp = me.getX() / 500f;
//                q.setAxisAngle(new Vector3(0, 1, 0), 360 * lerp);
//            }
//        });
    }

    @Override
    public void init(GLAutoDrawable glad) {
//        glad.setGL(new DebugGL4(glad.getGL().getGL4()));

        cache.addListener(shader);
        cache.addListener(sphereS);

        cache.getView().loadIdentity();
//        cache.getView().translate(0, 0, 5);
//        cache.getView().rotateEuler(-10, 0, 0);
//        cache.getView().inverse();
//        cache.fireChange(MatType.VIEW);

        GL2GL3 gl = glad.getGL().getGL2GL3();
        gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        gl.glDisable(GL.GL_BLEND);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glFrontFace(GL.GL_CCW);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glPolygonMode(GL.GL_BACK, GL2.GL_LINE);

//        time.addListener(1, new StepListener() {
//            int frames;
//
//            @Override
//            public void update(int tickCount, float lerp, float tickTimeSpan) {
//                frames++;
//                if (tickCount > 0) {
//                    System.out.println(frames);
//                    frames = 0;
//                }
//            }
//        });

        time.addListener(new DeltaListener() {
            final Quaternion q = new Quaternion();
            float time = 0;

            @Override
            public void update(double timeDelta) {
                time += timeDelta;
                q.setAxisAngle(new Vector3(0, 1, 0), 720 * ((time / 10) % 1));
                Quaternion[] dual = q.toDualQuaternion(new Vector3());
                Optional<ShaderUniform> un1 = shader.getUniform("dual1");
                un1.get().setData(dual[0].toArray());
                Optional<ShaderUniform> un2 = shader.getUniform("dual2");
                un2.get().setData(dual[1].toArray());


                float speed = (float) (100 * timeDelta);
                
                cache.getView().translate(left || right ? conv(left) * speed : 0,
                                          0,
                                          forward || backward ? conv(forward) * speed : 0);
                cache.fireChange(MatType.VIEW);
            }
        });
    }

    private int conv(boolean b) {
        return b ? 1 : -1;
    }

    @Override
    public void display(GLAutoDrawable glad) {
        time.update();
        glad.getGL().glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        if (shader.isInitialized()) {
            if (model == null) {
//                NormalGenerator g = new NormalGenerator();
//                Mesh m = g.modifie(test[0].getMesh());
//                model = modeler.create(new Model(m, null), shader);
                model = modeler.create(test[0], shader);
                AABB a = MeshUtil.calcAABB(test[0].getMesh().getVertices());
                System.out.println(a);
                for (Vector3 v : a.getCorners()) {
                    GenericVector gv = new GenericVector(v.x, v.y, v.z, 1);
                    Vector mult = cache.getViewProjection().mult(gv);
                    mult.div(mult.getCoords()[3]);
                    System.out.println(mult);
                }
            }
            shader.updateUniformData();
            model.render();

        }
        if (sphereS.isInitialized()) {
            if (sphere == null) {
                SphereGenerator gen = new SphereGenerator(10);
                Model m = new Model(new Mesh(gen.getIndices(), gen.getVertexBuffer(), GL.GL_TRIANGLES), null);
                AABB a = MeshUtil.calcAABB(m.getMesh().getVertices());
                System.out.println(a);
                sphere = modeler.create(m, sphereS);
            }
            sphereS.updateUniformData();

//            cache.getModel().loadIdentity();
//            cache.fireChange(MatType.MODEL);
//            sphere.render();
//
//            cache.getModel().translate(-3, 0, 0);
//            cache.fireChange(MatType.MODEL);
//            sphere.render();
//
//            cache.getModel().translate(6, 0, 0);
//            cache.fireChange(MatType.MODEL);
//            sphere.render();

        }
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {
        final float ratio = (float) width / Math.max(1, height);
        cache.getProjektion().perspective(60, ratio, 0.1, 10000);//ortho(-10, -10, 0.1, 10, 10, 100);//
        cache.fireChange(MatType.PROJECTION);
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
    }
}
