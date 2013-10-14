/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dheinrich.sandbox.scalarendering

import shapeless._
import shapeless.Nat._
import shapeless.TypeOperators._

import com.jogamp.common.nio.Buffers._;
import javax.media.opengl._;


sealed trait GLType{
  type num <: AnyVal
  type size <: Nat
}

object GLType{
  sealed trait Type[T <: AnyVal, N <: Nat] extends GLType{
    type num = T
    type size = N
  }
    
  type GLInt = Type[Int, _1]
  type GLFloat = Type[Float, _1]
  type Vec2 = Type[Float, _2]
  type Vec3 = Type[Float, _3]
  type Vec4 = Type[Float, _4]
  type Mat3 = Type[Float, _9]
  type Mat4 = Type[Float, _16]  
}

sealed abstract class GLConstants[T <: AnyVal](val constant:Int, val size:Int)

object GLConstants{
  implicit object GLShort extends GLConstants[Short](GL.GL_SHORT, SIZEOF_SHORT)
  implicit object GLInt extends GLConstants[Int](GL2ES2.GL_INT, SIZEOF_INT)
  implicit object GLFloat extends GLConstants[Float](GL.GL_FLOAT, SIZEOF_FLOAT)
  implicit object GLDouble extends GLConstants[Double](GL2GL3.GL_DOUBLE, SIZEOF_DOUBLE)
}


class GLElement[T <: GLType](name: String)
(override implicit val constant:GLConstants[T#num], i:ToInt[T#size]) extends 
Element[T#num, T#size](name)(constant, i)

class Element[T <: AnyVal, N <: Nat](name: String)(implicit val constant:GLConstants[T], i:ToInt[N]) {  
  val elementCount : Int = i()
  val byteSize = elementCount * constant.size
}






