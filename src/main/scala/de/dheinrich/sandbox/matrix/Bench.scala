package de.dheinrich.sandbox.matrix

import com.google.caliper._
import Vector._
import com.google.caliper.api.BeforeRep
import scala.reflect.ClassTag
import scala.util.Random
import com.badlogic.gdx.math.{ Vector3 => LGXVector3 }
import shapeless.Nat._

case class Bench[T: ClassTag](data: Seq[T])(f: (T, T) => Float){
  import Bench._
  
  def time(r: Int){
    runs(r){()=>
      val mid = data.length / 2
      var er = 0f
      var i = 0      
      while (i < mid) {
        er += f(data(i), data(i + mid))
        i += 1
      }
      er
    }
  }
}

object Bench {

  def runCaliper(cl: Class[_ <: Benchmark]) {
    runner.CaliperMain.main(cl, Array[String]())
  }

  def nextVec3 = Vector[_3](Random.nextFloat, Random.nextFloat, Random.nextFloat)
  def nextVec3LGX = new LGXVector3(Random.nextFloat, Random.nextFloat, Random.nextFloat)
  
  
  val count = 1000
  
  
  val myData = Array.tabulate(count)(i => nextVec3)
  val ldxData = Array.tabulate(count)(i => nextVec3LGX)
  val bufData = MappedObject.alloc[Vec](count)
  for(v <- bufData){
    v.x = Random.nextFloat
    v.y = Random.nextFloat
    v.z = Random.nextFloat
  }
  
  val myDot = Bench(myData)( _ dot _)
  val myCross = Bench(myData)((a,b) => (a cross b).x)
  val myPlus = Bench(myData)((a,b) => {(a addI b); a.x})
  
  val ldxDot = Bench(ldxData)( _ dot _)
  val ldxCross = Bench(ldxData)((a,b) => (a crs b).x)
  val ldxPlus = Bench(ldxData)((a,b) => (a add b).x)
  
  val bufDot = Bench(bufData)( _ dot _)
  val bufCross = Bench(bufData)((a,b) => (a cross b).x)
  val bufPlus = Bench(bufData)((a,b) => {(a addI b); a.x})
  
  def bufPlusCopy = {    
	var i = 0
    Bench(myData)((a,b) => {(a addI b); bufData(i%count).copyInto(a); i+=1; a.x})
  }

  def runs(runs: Int)(f: () => Any) {
    var r = runs
    var a: Any = null
    while (r > 0) {
      a = f();
      r -= 1
    }
    a
  }
}