package de.dheinrich.sandbox;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import darwin.core.gui.*;
import darwin.core.timing.GameTime;
import darwin.geometrie.data.*;
import darwin.renderer.GraphicContext;
import darwin.renderer.geometrie.factorys.ScreenQuad;
import darwin.renderer.geometrie.packed.RenderMesh;
import darwin.renderer.geometrie.packed.RenderMesh.RenderMeshFactory;
import darwin.renderer.opengl.FrameBuffer.*;
import darwin.renderer.opengl.*;
import darwin.renderer.opengl.FrameBuffer.RenderBuffer.RenderBufferFactory;
import darwin.renderer.opengl.VertexBO.VBOFactoy;
import darwin.renderer.shader.*;
import darwin.renderer.util.FboUtil;
import darwin.resourcehandling.io.TextureUtil;
import darwin.resourcehandling.resmanagment.ShaderLoader;
import darwin.resourcehandling.resmanagment.texture.ShaderDescription;
import darwin.util.misc.SaveClosable;

import com.google.common.base.Optional;
import com.google.inject.*;
import com.jogamp.newt.event.*;
import com.jogamp.opengl.util.texture.*;
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
    private RenderMesh mesh;
    private int x, y, w, h;
    private Sampler drawSampler, prossSampler;
    private Shader simple, processing;
    @Inject
    private RenderBufferFactory factory;
    @Inject
    @Default
    private FrameBufferObject DEFAULT;
    private FrameBufferObject particleData;
    private int accBuffer;
    private boolean initialized = false;
    private final GameTime time = new GameTime();
    private FrameBufferObject spawner;
    private Shader particleShader;
    private RenderMesh particleMesh;
    private Sampler particleSampler;

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
        System.out.println(glad.getContext().getGLExtensionsString());

        TextureUtil util = new TextureUtil(gcontext);
        int width = 1024;
        int height = width;
        Texture data1 = util.newTexture(GL2ES2.GL_RGBA8, width, height, 0, GL2ES2.GL_RGBA, GL.GL_UNSIGNED_BYTE, false);
        util.setTexturePara(data1, GL.GL_NEAREST, GL.GL_CLAMP_TO_EDGE);
        Texture data2 = util.newTexture(GL2ES2.GL_RGBA8, width, height, 0, GL2ES2.GL_RGBA, GL.GL_UNSIGNED_BYTE, false);
        util.setTexturePara(data2, GL.GL_NEAREST, GL.GL_CLAMP_TO_EDGE);

        particleData = new FrameBufferObject(gcontext, constants);
        try (SaveClosable sc = particleData.use()) {
            particleData.setColor_Attachment(0, data1);
            particleData.setColor_Attachment(1, data2);
            gl.getGL2GL3().glDrawBuffers(2, new int[]{GL.GL_COLOR_ATTACHMENT0, GL.GL_COLOR_ATTACHMENT0 + 1}, 0);
            gl.glClearColor(0, 0.f, 0, 0);
//            gl.glClearColor(0, 0.33f, 1, 0);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT);
            System.out.println(particleData.getStatusString());
        };

//        spawner = new FrameBufferObject(gcontext, constants);
//        try (SaveClosable sc = spawner.use()) {
//            Texture d = util.newTexture(GL2.GL_RGBA32F, width, height, 0, GL.GL_RGBA, GL.GL_FLOAT, false);
//            util.setTexturePara(d, GL.GL_NEAREST, GL.GL_CLAMP_TO_EDGE);
//            spawner.setColor_Attachment(0, d);
//            particleData.setColor_Attachment(1, data2);
//            System.out.println(particleData.getStatusString());
//        };
        //        DEFAULT.bind();


        Element idElement = new Element(GLSLType.VEC2, "lookup");
        VertexBuffer a = new VertexBuffer(idElement, width * height);
        float halfTexel = 0.5f/width;
        for (int i = 0; i < a.getSize(); i++) {
            a.newVertex().setAttribute(idElement, (float)(i%width) / width + halfTexel,
                                                  (float)(i/width) / width + halfTexel);
        }
        VertexBO particleBuffer = vboFactory.create(a);

        try {
            particleShader = sLoader.loadShader(new ShaderDescription("particle", false));
            particleSampler = particleShader.getSampler("data").get();
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        particleMesh = meshFactory.create(particleShader, GL.GL_POINTS, null, particleBuffer);

        ScreenQuad quad = new ScreenQuad(meshFactory, vboFactory);

        try {
            simple = sLoader.loadShader(new ShaderDescription("simple", false));
            processing = sLoader.loadShader(new ShaderDescription("processing.frag", "simple.vert", null));
            drawSampler = simple.getSampler("data").get();
            prossSampler = processing.getSampler("data").get();
            mesh = quad.buildRenderable(simple);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Enable VSync
//        gl.setSwapInterval(1);

        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glDisable(GL.GL_CULL_FACE);

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

        long elepsed = time.update();
        System.out.println((float) TimeUnit.SECONDS.toNanos(1) / elepsed);

        GL2ES2 gl = glad.getGL().getGL2();
        gl.glClearColor(0, 0, 0, 0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        int readBuffer = accBuffer;
        accBuffer = (accBuffer + 1) % 2;
        int drawBuffer = accBuffer;

        try (SaveClosable closable = particleData.use()) {
            gl.glClearColor(110, 0, 0, 0);
            processing.bind();
            prossSampler.bindTexture(particleData.getColorAttachmentTexture(readBuffer));
            gl.getGL2GL3().glDrawBuffer(GL.GL_COLOR_ATTACHMENT0 + drawBuffer);
            mesh.render();
        };

        particleShader.bind();
        particleSampler.bindTexture(particleData.getColorAttachmentTexture(drawBuffer));
        particleMesh.render();
    }

    private float getNormalized(float v, float m) {
        return ((v / m) - 0.5f) * 30;
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int width, int height) {
        w = width;
        h = height;
    }

    public static float Pack3PNForFP32(float[] channel) {
        // layout of a 32-bit fp register
        // SEEEEEEEEMMMMMMMMMMMMMMMMMMMMMMM
        // 1 sign bit; 8 bits for the exponent and 23 bits for the mantissa
        int uValue;

        // pack x
        uValue = (int) (channel[0] * 65535.0 + 0.5); // goes from bit 0 to 15

        // pack y in EMMMMMMM
        uValue |= (int) (channel[1] * 255.0 + 0.5) << 16;

        // pack z in SEEEEEEE
        // the last E will never be 1 because the upper value is 254
        // max value is 11111110 == 254
        // this prevents the bits of the exponents to become all 1
        // range is 1.. 254
        // to prevent an exponent that is 0 we add 1.0
        uValue |= (int) (channel[2] * 253.0 + 1.5) << 24;

        return Float.intBitsToFloat(uValue);
    }

    public static float[] Unpack3PNFromFP32(float fFloatFromFP32) {
        float a, b, c;

        int uInputFloat = Float.floatToIntBits(fFloatFromFP32);

        // unpack a
        // mask out all the stuff above 16-bit with 0xFFFF
        a = (uInputFloat & 0xFFFF) / 65535.f;

        b = ((uInputFloat >> 16) & 0xFF) / 255.f;

        // extract the 1..254 value range and subtract 1
        // ending up with 0..253
        c = (((uInputFloat >> 24) & 0xFF) - 1) / 253.f;

        return new float[]{a, b, c};
    }
}
