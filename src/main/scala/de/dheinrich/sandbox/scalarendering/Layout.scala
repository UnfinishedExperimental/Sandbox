/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dheinrich.sandbox.scalarendering

import shapeless._

trait ReversedTail[T <: HList]{
  type Out <: HList
  def apply(t: T) : Out
}

object ReversedTail{
  implicit def getRevTail[H, T <: HList](implicit r:Reverse[T]) = new ReversedTail[H :: T]{
    type Out = r.Out
    
    def apply(l : H :: T) = l.tail.reverse
  }
}

object Layout
{
  def apply[L <: HList](implicit s : ToSize[L], o : ToOff[L]) = new {
    def build(implicit r:ReversedTail[o.Out]) = {
      
        val size = s()
        val offsets = r(o()).asInstanceOf[ReversedTail[ToOff[L]#Out]#Out ]
        
        new Layout[L](size, offsets)
    }
  }
}

case class Layout[L <: HList](size:Int, offsets: ReversedTail[ToOff[L]#Out]#Out)

trait ToSize[L <: HList] {
  def apply() : Int
}

object ToSize {
  implicit val toSize0 = new ToSize[HNil] {
    def apply() = 0 
  }
    
  implicit def toSizeSucc[T <: GLType, L <: HList](implicit toIntN : ToSize[L],
                                                   c : GLConstants[T#num],
                                                   i : ToInt[T#size]) = new ToSize[T :: L] {
    def apply() = toIntN() + (c.size * i())
  }
}
  
  
trait ToOff[L <: HList] {  
  type Out <: Int :: HList
  def apply() : Out
}
  
object ToOff {
  implicit val toOff0 = new ToOff[HNil] {
    type Out = Int :: HNil
    def apply() = 0 :: HNil
  }
    
  implicit def toOffSucc[T <: GLType, L <: HList](implicit toOffN : ToOff[L],
                                                  c : GLConstants[T#num],
                                                  i : ToInt[T#size]) = new ToOff[T :: L] { 
    type Out = Int :: toOffN.Out
                                                     
    def apply()  = {
      val list = toOffN()
      
      val a  : Int = list.head
      val size = c.size * i()
      
      (a + size) :: list
    }
  }
}
