package de.dheinrich.sandbox;

import java.io.IOException;

import darwin.core.gui.*;
import darwin.geometrie.data.*;
import darwin.renderer.GraphicContext;
import darwin.renderer.geometrie.packed.RenderMesh;
import darwin.renderer.geometrie.packed.RenderMesh.RenderMeshFactory;
import darwin.renderer.opengl.FrameBuffer.*;
import darwin.renderer.opengl.*;
import darwin.renderer.opengl.VertexBO.VBOFactoy;
import darwin.renderer.shader.*;
import darwin.resourcehandling.io.TextureUtil;
import darwin.resourcehandling.resmanagment.ShaderLoader;
import darwin.resourcehandling.resmanagment.texture.ShaderDescription;
import darwin.util.misc.SaveClosable;

import com.google.inject.*;
import com.jogamp.newt.event.*;
import com.jogamp.opengl.util.texture.Texture;
import javax.media.opengl.*;

/**
 * Hello world!
 *
 */
public class App implements GLEventListener {

    private final Client client;
    private final VBOFactoy vboFactory;
    private final RenderMeshFactory meshFactory;
    private final ShaderLoader sLoader;
    @Inject
    private GraphicContext gcontext;
    @Inject
    private GLClientConstants constants;
    //
    private RenderMesh drawMesh, processMesh;
    private int x, y, w, h;
    private Sampler drawSampler, prossSampler;
    private Shader simple, processing;
    @Inject
    @Default
    private FrameBufferObject DEFAULT;
    private FrameBufferObject particleData;
    private int accBuffer;
    private boolean initialized = false;

    @Inject
    public App(Client client, VBOFactoy vboFactory, RenderMeshFactory meshFactory, ShaderLoader sLoader) {
        this.client = client;
        this.vboFactory = vboFactory;
        this.meshFactory = meshFactory;
        this.sLoader = sLoader;
    }

    public static void main(String[] args) throws InstantiationException {
        Stage stage = Stage.PRODUCTION;
        for (String arg : args) {
            if (arg.equals("-devmode")) {
                stage = Stage.DEVELOPMENT;
                break;
            }
        }
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

        particleData = new FrameBufferObject(gcontext, constants);
        TextureUtil util = new TextureUtil(gcontext);
        Texture data1 = util.newTexture(GL2.GL_RGB16, 128, 128, 0, GL.GL_RGB, GL.GL_SHORT, false);
        Texture data2 = util.newTexture(GL2.GL_RGB16, 128, 128, 0, GL.GL_RGB, GL.GL_SHORT, false);

        try (SaveClosable sc = particleData.use()) {
            particleData.setColor_Attachment(0, data1);
            particleData.setColor_Attachment(1, data2);
            System.out.println(particleData.getStatusString());
        };
        
//        DEFAULT.bind();

        Element position = new Element(GLSLType.VEC2, "Position");
        DataLayout layout = new DataLayout(position);

        VertexBuffer vb = new VertexBuffer(layout, 4);
        vb.newVertex().setAttribute(position, 1, 0);
        vb.newVertex().setAttribute(position, 0, 0);
        vb.newVertex().setAttribute(position, 1, 1);
        vb.newVertex().setAttribute(position, 0, 1);

        VertexBO vbo = vboFactory.create(vb);

        try {
            simple = sLoader.loadShader(new ShaderDescription("simple", false));
            drawSampler = simple.getSampler("data").get();
            processing = sLoader.loadShader(new ShaderDescription("processing.frag", "simple.vert", null));
            this.prossSampler = processing.getSampler("data").get();
            drawMesh = meshFactory.create(simple, GL.GL_TRIANGLE_STRIP, null, vbo);
            processMesh = meshFactory.create(processing, GL.GL_TRIANGLE_STRIP, null, vbo);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Enable VSync
        gl.setSwapInterval(1);

        gl.glClearColor(0, 110, 0, 0);

        initialized = true;
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
    }

    @Override
    public void display(GLAutoDrawable glad) {
        if (!initialized) {
            return;
        }

        GL2ES2 gl = glad.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

//        int readBuffer = accBuffer;
//        accBuffer = (accBuffer + 1) % 2;
//        int drawBuffer = accBuffer;

        try (SaveClosable closable = particleData.use()) {
//            gl.glClear(GL.GL_COLOR_BUFFER_BIT);
            processing.bind();
//            data2.bindTexture(particleData.getColorAttachmentTexture(readBuffer));
//            gl.getGL2GL3().glDrawBuffer(GL.GL_COLOR_ATTACHMENT0 + drawBuffer);
            processMesh.render();
        particleData.copyColorTo(DEFAULT);
        };
        

//        simple.bind();
//        drawSampler.bindTexture(particleData.getColorAttachmentTexture(0));
//        drawMesh.render();
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
