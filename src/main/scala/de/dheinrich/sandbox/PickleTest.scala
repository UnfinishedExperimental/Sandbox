package de.dheinrich.sandbox

import de.dheinrich.sandbox.matrix.Vector
import scala.pickling._
import json._

object	 PickleTest {
  def main(args: Array[String]) {
    val v = Vector[shapeless.Nat._3](2,3,4).pickle
    println(v.value)
  }
}