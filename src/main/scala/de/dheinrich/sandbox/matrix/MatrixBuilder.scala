package de.dheinrich.sandbox.matrix

import shapeless._
import shapeless.Nat._
import Matrix.Builder
import scala.annotation.implicitNotFound

trait LowBuilder extends Low2Builder {
  import Matrix._

  implicit object matrix1Builder extends Builder[_1, _1, Vector1] {
    def buildEmpty() = Vector1(0)
    def build(f: (Int, Int) => Float) = Vector1(f(0, 0))
    def build(f: Int => Float) = Vector1(f(0))
  }

  implicit object columnMatrix3Builder extends Builder[_1, _3, Vector3 with ColumnVector[_3]] {
    def buildEmpty() = new Vector3 with ColumnVector[_3]
    def build(f: (Int, Int) => Float) = build(i => f(0, i))
    def build(f: Int => Float) = new Vector3(f(0), f(1), f(2)) with ColumnVector[_3]
  }

  implicit object rowMatrix3Builder extends Builder[_3, _1, Vector3 with RowVector[_3]] {
    def buildEmpty() = new Vector3 with RowVector[_3]
    def build(f: (Int, Int) => Float) = build(i => f(i, 0))
    def build(f: Int => Float) = {
      val v = buildEmpty()
      v.x = f(0)
      v.y = f(1)
      v.z = f(2)
      v
    }
  }
}

trait Low2Builder extends Low3Builder {
  import Matrix._

  implicit def rowMatrixBuilder[N <: Nat](implicit toInt: ToInt[N]) = new Builder[N, _1, RowVector[N]] {
    def buildEmpty() = new ArrayVector[N] with RowVector[N]

    def build(f: (Int, Int) => Float) = {
      val s = toInt()
      build(i => f(i % s, i / s))
    }

    def build(f: Int => Float) = {
      val m = buildEmpty()

      var i = 0
      while (i < m.size) {
        m(i) = f(i)
        i += 1
      }
      m
    }
  }

  implicit def columnMatrixBuilder[N <: Nat](implicit toInt: ToInt[N]) = new Builder[_1, N, ColumnVector[N]] {
    def buildEmpty() = new ArrayVector[N] with ColumnVector[N]

    def build(f: (Int, Int) => Float) = {
      val s = toInt()
      build(i => f(i / s, i % s))
    }

    def build(f: Int => Float) = {
      val m = buildEmpty()

      var i = 0
      while (i < m.size) {
        m(i) = f(i)
        i += 1
      }

      m
    }
  }
}

trait Low3Builder {
  import Matrix._

  implicit def genMatrixBuilder[X <: Nat, Y <: Nat](implicit toIntX: ToInt[X], toIntY: ToInt[Y],
    ev: LT[_0, X], ev2: LT[_0, Y]) = new Builder[X, Y, GenMatrix[X, Y]] {
    
    def buildEmpty() = new GenMatrix[X, Y]

    def build(f: (Int, Int) => Float) = {
      val m = buildEmpty()

      val w = toIntX()
      val h = toIntY()

      var x = 0
      while (x < w) {
        var y = 0
        while (y < h) {
          m(x, y) = f(x, y)
          y += 1
        }
        x += 1
      }
      m
    }

    def build(f: Int => Float) = {
      val w = toIntX()
      build((x, y) => f(w * x + y))
    }
  }
}
