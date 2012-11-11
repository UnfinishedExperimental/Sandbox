package de.dheinrich.sandbox;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import darwin.core.gui.Client;
import darwin.core.gui.ClientWindow;
import darwin.geometrie.data.*;
import darwin.renderer.GraphicContext;
import darwin.renderer.geometrie.packed.RenderMesh;
import darwin.renderer.geometrie.packed.RenderMesh.RenderMeshFactory;
import darwin.renderer.opengl.*;
import darwin.renderer.opengl.VertexBO.VBOFactoy;
import darwin.renderer.shader.Shader;
import darwin.renderer.shader.Shader.ShaderFactory;
import darwin.renderer.shader.ShaderUniform;
import darwin.resourcehandling.ResourceChangeListener;
import darwin.resourcehandling.core.ResourceHandle;
import darwin.resourcehandling.handle.ClasspathFileHandler;
import darwin.resourcehandling.handle.ClasspathFileHandler.FileHandlerFactory;
import darwin.resourcehandling.io.ShaderFile;
import darwin.resourcehandling.io.ShaderUtil;
import darwin.resourcehandling.resmanagment.ResourcesLoader;

import com.google.inject.*;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import javax.media.opengl.*;

/**
 * Hello world!
 *
 */
public class App implements GLEventListener {

    private final Client client;
    private final VBOFactoy vboFactory;
    private final RenderMeshFactory meshFactory;
    private RenderMesh mesh;
    private int x, y, w, h;
    private ShaderUniform lightPos;
    private Shader shader;
    @Inject
    private ShaderUtil util;
    @Inject
    private ShaderFactory sFactory;
    @Inject
    private GraphicContext gcontext;
    @Inject
    private FileHandlerFactory fileFactory;
    private ClasspathFileHandler vertex;
    private ClasspathFileHandler fragment;

    @Inject
    public App(Client client, VBOFactoy vboFactory,
               RenderMeshFactory meshFactory) {
        this.client = client;
        this.vboFactory = vboFactory;
        this.meshFactory = meshFactory;
    }

    public static void main(String[] args) throws InstantiationException {

        Stage stage = Arrays.binarySearch(args, "-devmode") < 0 ? Stage.DEVELOPMENT : Stage.PRODUCTION;
        Injector inj = Guice.createInjector(stage, Client.getRequiredModules());
        App a = inj.getInstance(App.class);
        a.start();
    }

    public void start() throws InstantiationException {
        client.addGLEventListener(this);
        ClientWindow window = new ClientWindow(500, 500, false, client);
        window.startUp().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent me) {
                x = me.getX();
                y = me.getY();
            }
        });
    }

    @Override
    public void init(GLAutoDrawable glad) {
        glad.setGL(new DebugGL2(glad.getGL().getGL2()));

        GL2ES2 gl = glad.getGL().getGL2();

        Element position = new Element(GLSLType.VEC2, "Position");
        DataLayout layout = new DataLayout(position);

        VertexBuffer vb = new VertexBuffer(layout, 4);
        vb.newVertex().setAttribute(position, 1, 0);
        vb.newVertex().setAttribute(position, 0, 0);
        vb.newVertex().setAttribute(position, 1, 1);
        vb.newVertex().setAttribute(position, 0, 1);

        VertexBO vbo = vboFactory.create(vb);

        //        util.setResourceLoader(new ResourceProvider() {
        //            @Override
        //            public InputStream getRessource(String name) throws IOException {
        //                return fileFactory.create(Paths.get(name)).getStream();
        //            }
        //        });
        Path base = Paths.get("src/main/resources");
        fragment = fileFactory.create(base.resolve("resources/shaders/simple.frag"));
        vertex = fileFactory.create(base.resolve("resources/shaders/simple.vert"));

        fragment.registerChangeListener(shaderUpdater);
        vertex.registerChangeListener(shaderUpdater);

        shaderUpdater.resourceChanged(null);

        lightPos = shader.getUniform("lightPos").get();

        mesh = meshFactory.create(shader, GL.GL_TRIANGLE_STRIP, null, vbo);

        // Enable VSync
        gl.setSwapInterval(1);

        gl.glClearColor(0, 110, 0, 0);

        gl.glEnable(GL.GL_BLEND);
    }
    private final ResourceChangeListener shaderUpdater = new ResourceChangeListener() {
        @Override
        public void resourceChanged(ResourceHandle handle) {
            try {
                final ShaderFile loadShader = ShaderFile.Builder.create("simple").
                        withFragment(util.getData(fragment.getStream())).
                        withVertex(util.getData(vertex.getStream())).
                        create();

                if (shader == null) {
                    shader = sFactory.create(loadShader);
                }

                gcontext.getGLWindow().invoke(false, new GLRunnable() {
                    @Override
                    public boolean run(GLAutoDrawable glad) {
                        try {
                            ShaderProgramm compileShader = util.compileShader(loadShader);
                            shader.ini(compileShader);
                        } catch (Throwable t) {
                            System.out.println(t);
                        }
                        return true;
                    }
                });
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    };

    @Override
    public void dispose(GLAutoDrawable glad) {
    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL2ES2 gl = glad.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        if (shader != null && shader.isInitialized()) {
            lightPos.setData(getNormalized(x, w), getNormalized(h - y, h), 3);
            shader.updateUniformData();
            if (mesh != null) {
                mesh.render();
            }
        }
    }

    private float getNormalized(float v, float m) {
        return ((v / m) - 0.5f) * 30;
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int width, int height) {
        w = width;
        h = height;
    }
}
