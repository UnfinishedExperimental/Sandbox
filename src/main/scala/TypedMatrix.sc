package de.dheinrich.sandbox

import matrix._
import shapeless.Nat._

object TypedMatrix {

  def main(args: Array[String]) {}

  def bench(be: Bench[_]) {

    val count = 10000
   
    Bench.runs(10) { () =>
      be.time(count)
    }

    timed { () =>
      be.time(count)
    }
 
    def timed(f: () => Any) = {
      val time = System.nanoTime()
      val a = f()
      val diff = System.nanoTime() - time
      println(diff / 1000000f + "ms")
    }

    //Bench.test
  }
   
  
  bench(Bench.bufPlusCopy)
  bench(Bench.myPlus)
  bench(Bench.bufPlus)
  bench(Bench.ldxPlus)
  
//import com.google.caliper._

	//val marks = Array(classOf[BenchDot], classOf[BenchCross], classOf[BenchPlus])
  
  
}