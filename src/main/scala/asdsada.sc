package de.dheinrich.sandbox

import matrix._
import shapeless.Nat._

import Angles._

object asdsada {
  import Matrix._

  val m = Matrix.builder[_4, _3]().build(identity)//> m  : de.dheinrich.sandbox.matrix.GenMatrix[shapeless.Nat._4,shapeless.Nat._3
                                                  //| ] = 
                                                  //| [1.0,	0.0,	0.0,	0.0
                                                  //| 0.0,	1.0,	0.0,	0.0
                                                  //| 0.0,	0.0,	1.0,	0.0]
  //m.translate(Vector[_4](10f, 1f, 3f, 1f))
  val m2 = Matrix.rotX(90 degree)                 //> m2  : de.dheinrich.sandbox.matrix.GenMatrix[shapeless.Nat._4,shapeless.Nat._
                                                  //| 4] = 
                                                  //| [1.0,	0.0,	0.0,	0.0
                                                  //| 0.0,	6.123234E-17,	1.0,	0.0
                                                  //| 0.0,	-1.0,	6.123234E-17,	0.0
                                                  //| 0.0,	0.0,	0.0,	1.0]
  val m3 = (m mult m2)                            //> m3  : de.dheinrich.sandbox.matrix.GenMatrix[shapeless.Nat._4,shapeless.Nat._
                                                  //| 3] = 
                                                  //| [1.0,	0.0,	0.0,	0.0
                                                  //| 0.0,	6.123234E-17,	1.0,	0.0
                                                  //| 0.0,	-1.0,	6.123234E-17,	0.0]
  m3 translate Vector[_4](0, 0, 1f, 1f)

  m3  mult Matrix.rotX(-90 degree)                //> res0: de.dheinrich.sandbox.matrix.GenMatrix[shapeless.Nat._4,shapeless.Nat._
                                                  //| 3] = 
                                                  //| [1.0,	0.0,	0.0,	0.0
                                                  //| 0.0,	1.0,	0.0,	1.0
                                                  //| 0.0,	0.0,	1.0,	6.123234E-17]

}