/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import darwin.core.gui.*;
import darwin.core.timing.*;
import darwin.geometrie.unpacked.Model;
import darwin.renderer.geometrie.packed.RenderModel;
import darwin.renderer.geometrie.packed.RenderModel.RenderModelFactory;
import darwin.renderer.shader.*;
import darwin.resourcehandling.dependencies.annotation.*;
import darwin.resourcehandling.shader.ShaderLoader;
import darwin.util.math.base.Quaternion;
import darwin.util.math.base.matrix.Matrix4;
import darwin.util.math.base.vector.*;
import darwin.util.math.composits.AABB;
import darwin.util.math.util.*;

import com.google.common.base.Optional;
import de.dheinrich.sandbox.g2d.MeshUtil;
import javax.inject.Inject;
import javax.media.opengl.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class ResourceLoading implements GLEventListener {

    @InjectBundle(files = {"simple.frag", "simple.vert"}, prefix = ShaderLoader.SHADER_PATH_PREFIX)
    private Shader shader;
    @InjectResource(file = "sp.ctm")
    private Model[] test;
    @Inject
    private RenderModelFactory modeler;
    private RenderModel model;
    private MatrixCache cache = new MatrixCache();
    private GameTime time = new GameTime();

    public static void main(String[] args) throws InstantiationException {
        boolean debug = true;

        Client client = Client.createClient(debug);
        ClientWindow window = new ClientWindow(500, 500, false, client);
        window.startUp();

        ResourceLoading a = client.addGLEventListener(ResourceLoading.class);
    }

    @Override
    public void init(GLAutoDrawable glad) {
        cache.addListener(shader);

        cache.getView().loadIdentity();
        cache.getView().translate(0, 0, 5).inverse();
        cache.getView().lookAt(new Vector3(2, 4, 5),
                               new Vector3(0),
                               new Vector3(0, 1, 0));
        cache.fireChange(MatType.VIEW);

        GL gl = glad.getGL();

        time.addListener(1, new StepListener() {
            int frames;

            @Override
            public void update(int tickCount, float lerp, float tickTimeSpan) {
                frames++;
                if (tickCount > 0) {
                    System.out.println(frames);
                    frames = 0;
                }
            }
        });

        time.addListener(new DeltaListener() {
            Quaternion r, q = new Quaternion();

            {
                r = new Quaternion();
                r.setAxisAngle(new Vector3(0, 1, 0), 520);
            }
            float time = 0;

            @Override
            public void update(double timeDelta) {
                time += timeDelta;
                q = r.getInterpolated((time/5) %1);

                Quaternion[] dual = q.toDualQuaternion(new Vector3());
                Optional<ShaderUniform> un1 = shader.getUniform("dual1");
                un1.get().setData(dual[0].toArray());
                Optional<ShaderUniform> un2 = shader.getUniform("dual2");
                un2.get().setData(dual[1].toArray());
            }
        });
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
    }

    @Override
    public void display(GLAutoDrawable glad) {
        time.update();
        glad.getGL().glClear(GL.GL_COLOR_BUFFER_BIT);

        if (shader.isInitialized()) {
            if (model == null) {
                model = modeler.create(test[0], shader);
                AABB a = MeshUtil.calcAABB(test[0].getMesh().getVertices());
                System.out.println(a);
                System.out.println(cache.getViewProjection().fastMult(new Vector3(9, -8, 90)));
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
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {
        final float ratio = (float) height / Math.min(1, width);
        cache.getProjektion().ortho(-10, -10, -100, 10, 10, 100);//perspective(50, ratio, 0.001, 1000);
        cache.fireChange(MatType.PROJECTION);
    }
}
