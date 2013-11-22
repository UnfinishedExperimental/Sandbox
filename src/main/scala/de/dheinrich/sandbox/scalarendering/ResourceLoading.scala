/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox.scalarendering

import com.google.common.base._
import com.jogamp.opengl.util.texture._
import darwin.core.controls._
import darwin.core.gui._
import darwin.core.timing._
import darwin.geometrie.unpacked._
import darwin.renderer.geometrie.packed.RenderModel
import darwin.renderer.shader._
import darwin.resourcehandling.dependencies.annotation._
import darwin.resourcehandling.shader.ShaderLoader
import darwin.resourcehandling.texture._
import darwin.util.math.base.vector.Vector3
import darwin.util.math.composits.AABB
import darwin.util.math.util._
import de.dheinrich.sandbox._
import de.dheinrich.sandbox.meshstuff._
import de.dheinrich.sandbox.observable.{TimeObservable, KeyEvent, KeyPressed, KeyObservable}
import javax.inject.Inject
import javax.media.opengl._
import rx.lang.scala.util._

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
object ResourceLoadingS {
  def main(args: Array[String]) {
    val debug: Boolean = true

    val client: Client = Client.createClient(debug)
    val window: ClientWindow = new ClientWindow(1000, 500, false, client)
    window.startUp

    val a = client.addGLEventListener(classOf[ResourceLoadingS])

    val fps: FPSController = new FPSController
    a.cache.setView(fps.getView)

    val time = TimeObservable.observeDelta(a.time)

    //val events = KeyObservable.observe(client) merge MouseObservable.observe(client)
    val keys = KeyObservable.observe(client)

    val until = keys.buffer(() => time.map(t => Closing())).map(_.groupBy(_.code.toChar).toMap.mapValues(_.last))


    def isPressed(c: Char) = (_: Map[Char, KeyEvent]).get(c).filter(_.isInstanceOf[KeyPressed]).isDefined

    //    val forward = until.map(isPressed('w'))
    //    val back = until.map(isPressed('s'))
    //    val left = until.map(isPressed('a'))
    //    val right = until.map(isPressed('d'))

    def conv(b: Boolean): Int = {
      return if (b) -1 else 1
    }
    val speed = 10
    val dirSwitch = (t: (Boolean, Boolean)) => if (t._1 || t._2) conv(t._1) * speed else 0

    //    val z = (forward zip back).map(dirSwitch)
    //    val x = (left zip right).map(dirSwitch)
    //
    //    val direction = (x zip z) map (t => new Vector3(t._1, 0, t._2))


    val direction = for (m <- until) yield {
      val f = isPressed('w')(m)
      val b = isPressed('s')(m)
      val l = isPressed('a')(m)
      val r = isPressed('d')(m)

      val z = dirSwitch((f, b))
      val x = dirSwitch((l, r))
      new Vector3(x, 0, z)
    }

    direction.subscribe({
      trans =>
        fps.position.add(fps.rotation.getRotationMatrix.fastMult(trans))
        fps.resetInverse
        a.cache.fireChange(MatType.VIEW)
    }, _.printStackTrace())

    client.addMouseListener(new InputController(fps, null, a.cache))
    client.addKeyListener(fps)
  }
}

class ResourceLoadingS extends GLEventListener {
  @InjectBundle(files = Array("sphere.frag", "sphere.vert"), prefix = ShaderLoader.SHADER_PATH_PREFIX)
  private var shader: Shader = null

  @InjectResource(file = "kinect.ctm")
  private var kinect: Array[Model] = null

  @InjectResource(file = "out.png")
  private var rgb: Texture = null

  @Inject
  private var modeler: RenderModel.RenderModelFactory = null
  @Inject
  private var texUtil: TextureUtil = null

  private var model: RenderModel = null
  var cache: MatrixCache = new MatrixCache
  var time: GameTime = new GameTime

  def init(glad: GLAutoDrawable) {
    import GL._
    import GL2GL3._

    cache.addListener(shader)
    texUtil.setTexturePara(rgb, GL_LINEAR, GL_CLAMP_TO_BORDER)

    val color: Array[Float] = Array(0.1f, 0.1f, 0.1f, 0f)
    rgb.setTexParameterfv(glad.getGL, GL_TEXTURE_BORDER_COLOR, color, 0)

    val gl = glad.getGL().getGL2GL3();
    {
      import gl._
      glClearColor(0.3f, 0.3f, 0.3f, 1)
      glDisable(GL_BLEND)
      glEnable(GL_DEPTH_TEST)
      glFrontFace(GL_CCW)
      glPolygonMode(GL_FRONT, GL_LINE)
    }
  }

  def display(glad: GLAutoDrawable) {
    time.update
    glad.getGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT)

    if (shader.isInitialized) {
      val diff: Optional[Sampler] = shader.getSampler("diffuse")
      diff.get.bindTexture(rgb)
      if (model == null) {
        model = modeler.create(kinect(0), shader)
        val ng: NormalGenerator = new NormalGenerator
        val m: Mesh = ng.modifie(kinect(0).getMesh)
        model = modeler.create(new Model(m, null), shader)
        val a: AABB = MeshUtil.calcAABB(kinect(0).getMesh.getVertices)
        System.out.println(a)
      }
      shader.updateUniformData
      model.render
    }
  }

  def reshape(glad: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
    val ratio: Float = width.asInstanceOf[Float] / Math.max(1, height)
    cache.getProjektion.perspective(60, ratio, 0.001f, 1000f)
    cache.fireChange(MatType.PROJECTION)
  }

  def dispose(glad: GLAutoDrawable) {
  }
}