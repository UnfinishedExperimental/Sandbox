package de.dheinrich.sandbox

import scala.language.dynamics
import scala.language.experimental.macros

import scala.reflect.ClassTag
import scala.reflect.NameTransformer
import scala.reflect.macros.Context

import scala.tools.reflect.Eval

import shapeless._
import Nat._

object VectorMacros2 {
  
  def updateAll[N <: Nat](c: Context)(other: c.Expr[BaseVector[N]])(func: c.Expr[(Float, Float) => Float])(max: c.Expr[ToInt[N]]): c.Expr[Unit] = {
    import c.universe._
      
    def update(n: Int) = {
       val i = "_"+n
      
      val this_ = Ident("this")
      val v1 = c.Expr[Float](Select(this_, newTermName(i)))
      val v2 = c.Expr[Float](Select(other.tree, newTermName(i)))
      
      val value = reify{func.splice(v1.splice, v2.splice)}
      
      c.Expr[Unit](Apply(
          Apply(
            Select(this_, newTermName("updateDynamic")), c.literal(i).tree :: Nil
          ),value.tree :: Nil
        ))
    }
    
    val mmax = max.eval()
    c.Expr[Unit](Block((1 to mmax) map(i => update(i).tree) :_*))
  }
}