package de.dheinrich.sandbox

import scala.language.dynamics
import scala.language.experimental.macros

import scala.reflect.ClassTag
import scala.reflect.NameTransformer
import scala.reflect.macros.Context

import shapeless._

trait BaseVector[N <: Nat] extends Dynamic {

  def apply(i: Int): Float

  def update(i: Int, v: Float): Unit
  
  def selectDynamic(name: String) = macro VectorMacros.tupelApply
  
  def updateDynamic(name: String)(value: Float) = macro VectorMacros.tupelUpdate
}

object VectorMacros {
  val reg = "_(\\d+)".r 
  
  def getNum(c: Context)(name: c.Expr[String]) = {
    import c.universe._
    val Literal(Constant(s_name: String)) = name.tree     
    
    val num =  reg.findFirstIn(s_name).map(_.substring(1).toInt)    
    if(num.isEmpty)
      c.error(name.tree.pos, s"""must be of the format "_<number>" not $s_name""")
    
    val n = num.get    
    if(n < 1)
      c.error(name.tree.pos, s"the asked element mus be in the range [1, ???] not $n")
    
    c.literal(n - 1)
  }
  
  def tupelApply(c: Context)(name: c.Expr[String]): c.Expr[Float] = {
    import c.universe._
    val n = getNum(c)(name)
      
    reify{
      val v = c.prefix.asInstanceOf[c.Expr[BaseVector[_]]].splice
      v(n.splice)
    }
  }
  
  def tupelUpdate(c: Context)(name: c.Expr[String])(value: c.Expr[Float]): c.Expr[Unit] = {
    import c.universe._
    val n = getNum(c)(name)
    
    reify{
      val v = c.prefix.asInstanceOf[c.Expr[BaseVector[_]]].splice
      v.update(n.splice, value.splice)
    }
  }
}