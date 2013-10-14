package de.dheinrich.sandbox

import matrix._
import shapeless.Nat._

object TypedMatrix {

  def main(args: Array[String]) {}                //> main: (args: Array[String])Unit

  def bench(be: Bench) {

    val count = 100000

    Bench.runs(10) { () =>
      be.timeMine(count)
      //be.timeCross(count)
      be.timeLibGDX(count)
      //be.timeCrossLGX(count)
    }

    timed { () =>
      be.timeMine(count)
    }
    timed { () =>
      be.timeLibGDX(count)
    }
 
    def timed(f: () => Any) = {
      val time = System.nanoTime()
      val a = f()
      val diff = System.nanoTime() - time
      println(diff / 1000000f + "ms")
    }

    //Bench.test
  }                                               //> bench: (be: de.dheinrich.sandbox.matrix.Bench)Unit
  
  
  //bench(new BenchDot)
  bench(new BenchCross)                           //> 186.55511ms
                                                  //| 90.35115ms
  //bench(new BenchPlus)
  
//import com.google.caliper._

	//val marks = Array(classOf[BenchDot], classOf[BenchCross], classOf[BenchPlus])
  
  
}