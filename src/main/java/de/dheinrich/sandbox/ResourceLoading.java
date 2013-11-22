/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import de.dheinrich.sandbox.meshstuff.MeshUtil;

import darwin.core.controls.*;
import darwin.core.gui.*;
import darwin.core.timing.*;
import darwin.geometrie.unpacked.*;
import darwin.renderer.geometrie.packed.RenderModel;
import darwin.renderer.geometrie.packed.RenderModel.RenderModelFactory;
import darwin.renderer.shader.*;
import darwin.resourcehandling.dependencies.annotation.*;
import darwin.resourcehandling.shader.ShaderLoader;
import darwin.resourcehandling.texture.*;
import darwin.util.math.composits.AABB;
import darwin.util.math.util.*;

import com.google.common.base.*;
import com.jogamp.opengl.util.texture.*;
import de.dheinrich.sandbox.meshstuff.*;
import javax.inject.Inject;
import javax.media.opengl.*;


/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class ResourceLoading implements GLEventListener {

    @InjectBundle(files = {"sphere.frag", "sphere.vert"}, prefix = ShaderLoader.SHADER_PATH_PREFIX)
    private Shader shader;
    @InjectResource(file = "kinect.ctm")
    private Model[] kinect;
    @InjectResource(file = "out.png")
    private Texture rgb;
    
    @Inject
    private RenderModelFactory modeler;
    @Inject 
    private TextureUtil texUtil;
    
    private RenderModel model;
    private MatrixCache cache = new MatrixCache();
    private GameTime time = new GameTime();

    public static void main(String[] args) throws InstantiationException {
        boolean debug = true;

        Client client = Client.createClient(debug);
        ClientWindow window = new ClientWindow(1000, 500, false, client);
        window.startUp();

        final ResourceLoading a = client.addGLEventListener(ResourceLoading.class);

        FPSController vm = new FPSController();
        a.cache.setView(vm.getView());

        client.addMouseListener(new InputController(vm, null, a.cache));
        client.addKeyListener(vm);

        // a.time.addListener(vm.forCache(a.cache));
    }

    @Override
    public void init(GLAutoDrawable glad) {
//        glad.setGL(new DebugGL4(glad.getGL().getGL4()));

//        cache.addListener(shader);
        cache.addListener(shader);
        
        texUtil.setTexturePara(rgb, GL.GL_LINEAR, GL2.GL_CLAMP_TO_BORDER);
        float[] color = {0.1f, 0.1f, 0.1f, 0f};
        rgb.setTexParameterfv(glad.getGL(), GL2.GL_TEXTURE_BORDER_COLOR, color, 0);

        GL2GL3 gl = glad.getGL().getGL2GL3();
        gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        gl.glDisable(GL.GL_BLEND);
        gl.glEnable(GL.GL_DEPTH_TEST);
        
        gl.glFrontFace(GL.GL_CCW);
//        gl.glEnable(GL.GL_CULL_FACE);
        gl.glPolygonMode(GL.GL_FRONT, GL2.GL_LINE);

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
    }

    @Override
    public void display(GLAutoDrawable glad) {
        time.update();
        glad.getGL().glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        if (shader.isInitialized()) {
            Optional<Sampler> diff = shader.getSampler("diffuse");
            diff.get().bindTexture(rgb);            
            
            if (model == null) {
//                NormalGenerator g = new NormalGenerator();
//                Mesh m = g.modifie(test[0].getMesh());
                model = modeler.create(kinect[0], shader);
                NormalGenerator ng = new NormalGenerator();
                Mesh m = ng.modifie(kinect[0].getMesh());
                model = modeler.create(new Model(m, null), shader);
                
                AABB a = MeshUtil.calcAABB(kinect[0].getMesh().getVertices());
                System.out.println(a);
//                for (Vector3 v : a.getCorners()) {
//                    GenericVector gv = new GenericVector(v.x, v.y, v.z, 1);
//                    Vector mult = cache.getViewProjection().mult(gv);
//                    mult.div(mult.getCoords()[3]);
//                    System.out.println(mult);
//                }
            }
            
            shader.updateUniformData();
            model.render();
        }
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {
        final float ratio = (float) width / Math.max(1, height);
        cache.getProjektion().perspective(60, ratio, 0.001f, 1000f); //ortho(-10, -10, -11110.1, 10, 10, 100);//
        cache.fireChange(MatType.PROJECTION);
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
    }
}
