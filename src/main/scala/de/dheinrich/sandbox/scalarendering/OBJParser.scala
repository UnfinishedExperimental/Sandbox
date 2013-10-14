/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dheinrich.sandbox.scalarendering

import scala.util.parsing.combinator._

@specialized
case class Vec3[T <: AnyVal](x:T, y:T, z:T)


object OBJParser extends JavaTokenParsers with ImplicitConversions with PackratParsers{
  
  val integer = wholeNumber ^^ {_.toInt}
  val float = floatingPointNumber ^^ {_.toFloat}
  val name = "(.+)"r
  
  def toVec3[T <: AnyVal] = (_:Seq[T]) match {case Seq(a,b,c) => Vec3(a, b, c)}
  
  val position = "v" ~> repN(3, float) ^^ toVec3[Float]
  val normal = "vn" ~> repN(3, float) ^^ toVec3[Float]
  val uv = "vt" ~> repN(3, float) ^^ toVec3[Float]
  
  val materialLib =  "mtllib" ~> name ^^ {x => "lib"} //
  val material = "usemtl" ~> name ^^ {x => "mat"} //
  
  val groupe = "g" ~> name ^^ {x => "group"} //
 
  val vertex = repsep(integer, "/") ^^ toVec3[Int]
  val face = "f" ~> rep1(vertex) filter(_.length >= 3)
  
  def file = rep(position | normal | uv | materialLib | material | groupe | face | comment | s)
  
  def comment = "#" ~ "(.*)".r
  def s = "s" ~> integer
  
  def main(arg : Array[String]){
    val a = parse(file, "f 1/1/1 2/2/2 3/3/3 \n f 1/1/1 2/2/2 3/3/3 4/4/4 \n vn 23 43 3.24")
    println(a)
      
  }
}
