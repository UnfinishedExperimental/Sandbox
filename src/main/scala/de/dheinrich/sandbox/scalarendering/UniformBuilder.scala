/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dheinrich.sandbox.scalarendering

import GLType._
  
trait UniformBuilder[T <: GLType]{
  def build(el : GLElement[T]) : Int
}

trait LowPriorityBuilder{  
  /**
   * CHECK type is so it is certain that this method is only called with a known type of GLType
   */
  implicit def elseBuilder[T <: GLType, CHECK <: T#num] = new UniformBuilder[T]{
    def build(el : GLElement[T]) = el.elementCount
  }
}

object UniformBuilder extends LowPriorityBuilder{
  implicit object Mat3Builder extends UniformBuilder[Mat3]{
    def build(el : GLElement[Mat3]) = 9
  }  
  implicit object Mat4Builder extends UniformBuilder[Mat4]{
    def build(el : GLElement[Mat4]) = 16
  }  
}
