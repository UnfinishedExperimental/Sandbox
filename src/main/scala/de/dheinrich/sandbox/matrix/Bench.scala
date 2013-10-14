package de.dheinrich.sandbox.matrix

import com.google.caliper._
import Vector._
import com.google.caliper.api.BeforeRep
import scala.util.Random
import com.badlogic.gdx.math.{ Vector3 => LGXVector3 }
import shapeless.Nat._

trait Bench extends Benchmark {
  import Bench._
  val count = 1000
  var a = Array.tabulate(count)(i => nextVec3)
  var b = Array.tabulate(count)(i => nextVec3LGX)

  def timeMine(r: Int) {
    runs(r) { () =>
      var er = 0f
      var i = 0
      while (i < count / 2) {
        er += mine(a(i), a(i + 1))
        i += 1
      }
      er
    }
  }

  def timeLibGDX(r: Int) {
    runs(r) { () =>
      var er = 0f
      var i = 0
      while (i < count / 2) {
        er += libGDX(b(i), b(i + 1))
        i += 1
      }
      er
    }
  }

  def mine(a: Vector3, b: Vector3): Float
  def libGDX(a: LGXVector3, b: LGXVector3): Float
}

class BenchDot extends Bench {
  def mine(a: Vector3, b: Vector3) = a dot b
  def libGDX(a: LGXVector3, b: LGXVector3) = a dot b
}
class BenchCross extends Bench {
  def mine(a: Vector3, b: Vector3) = (a cross b).x
  def libGDX(a: LGXVector3, b: LGXVector3) = (new LGXVector3(a) crs b).x
}
class BenchPlus extends Bench {
  def mine(a: Vector3, b: Vector3) = (a + b).x
  def libGDX(a: LGXVector3, b: LGXVector3) = (new LGXVector3(a) add b).x
}

object Bench {

  def runCaliper(cl: Class[_ <: Benchmark]) {
    runner.CaliperMain.main(cl, Array[String]())
  }

  def nextVec3 = Vector[_3](Random.nextFloat, Random.nextFloat, Random.nextFloat)
  def nextVec3LGX = new LGXVector3(Random.nextFloat, Random.nextFloat, Random.nextFloat)

  def runs(runs: Int)(f: () => Any) {
    var r = runs
    var a: Any = null
    while (r > 0) {
      a = f();
      r -= 1
    }
    a
  }

  def what() {
    val m = Matrix.builder[_3, _3]().build(i => i)
    val v = m.row(0) cross m.column(1)

    println(m)
    println(v)
  }
}

object BenchApp {
  import Bench._

  def main(args: Array[String]) {
    what()
  }

}