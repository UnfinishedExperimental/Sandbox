package de.dheinrich.sandbox.matrix

import shapeless._
import shapeless.Nat._

import Angles._

object Matrix {
  def apply[X <: Nat, Y <: Nat] = new {
    def apply[M <: Matrix[X, Y]]()(implicit b: Builder[X, Y, M]) = b.buildEmpty
  }

  def builder[X <: Nat, Y <: Nat] = new {
    def apply[M <: Matrix[X, Y]]()(implicit b: Builder[X, Y, M]) = b
  }

  val identity = (x: Int, y: Int) => if (x == y) 1f else 0f

  trait Builder[X <: Nat, Y <: Nat, +M <: Matrix[X, Y]] {
    def buildEmpty(): M
    def build(f: (Int, Int) => Float): M
    def build(f: Int => Float): M
  }

  object Builder extends LowBuilder

  type Row[N <: Nat] = Matrix[N, _1] with Vector[N]
  type Column[N <: Nat] = Matrix[_1, N] with Vector[N]

  implicit class TransformOps[X <: Nat, Y <: Nat](m: Matrix[X, Y])(implicit ev: LT[_3, X], ev2: LT[_2, Y], ix: ToInt[X], iy: ToInt[Y]) {

    val w = ix()
    val h = iy()

    def translate(v: ColumnVector[X]) {
      translation = m mult v
    }

    def worldTranslate(v: Vector[Y]) {
      translation += v
    }

    def translation = Builder.columnMatrixBuilder[Y].build(m(w - 1, _))

    def translation_=(v: Vector[Y]) {
      var r = 0
      while (r < h) {
        m(w - 1, r) = v(r)
        r += 1
      }
    }
  }

  implicit def rowDropper[X <: Nat, Y <: Nat](m: Matrix[X, Y])(implicit ev: LT[_1, Y], pred: Pred[Y]) = new {
    def dropRow[M <: Matrix[X, pred.Out]](n: Int)(implicit b: Builder[X, pred.Out, M]) = {
      b.build((x, y) => m(x, if (y >= n) y + 1 else y))
    }
  }

  implicit def collumnDropper[X <: Nat, Y <: Nat](m: Matrix[X, Y])(implicit ev: LT[_1, X], pred: Pred[X]) = new {
    def dropColumn[M <: Matrix[pred.Out, Y]](n: Int)(implicit b: Builder[pred.Out, Y, M]) = {
      b.build((x, y) => m(if (x >= n) x + 1 else x, y))
    }
  }

  implicit def minifier[X <: Nat, Y <: Nat](m: Matrix[X, Y])(implicit ev: LT[_1, X], ev2: LT[_1, Y], predX: Pred[X], predY: Pred[Y]) = new {
    def minor[M <: Matrix[predX.Out, predY.Out]](nx: Int, ny: Int)(implicit b: Builder[predX.Out, predY.Out, M]) = {
      b.build { (x, y) =>
        m(if (x >= nx) x + 1 else x,
          if (y >= ny) y + 1 else y)
      }
    }
  }

  def rotX(a: Radian) = {
    val sin = a.sinF
    val cos = a.cosF

    val m = Matrix[_4, _4]()
    m(3, 3) = 1
    m(0, 0) = 1
    m(1, 1) = cos
    m(2, 2) = cos
    m(1, 2) = -sin
    m(2, 1) = sin
    m
  }

  def rotY(a: Radian) = {
    val sin = a.sinF
    val cos = a.cosF

    val m = Matrix[_4, _4]()
    m(3, 3) = 1
    m(0, 0) = cos
    m(1, 1) = 1
    m(2, 2) = cos
    m(2, 0) = -sin
    m(0, 2) = sin
    m
  }

  def rotZ(a: Radian) = {
    val sin = a.sinF
    val cos = a.cosF

    val m = Matrix[_4, _4]()
    m(3, 3) = 1
    m(0, 0) = cos
    m(1, 1) = cos
    m(2, 2) = 1
    m(0, 1) = -sin
    m(1, 0) = sin
    m
  }
}

trait Matrix[X <: Nat, Y <: Nat] {
  import Matrix._

  def apply(x: Int, y: Int): Float
  def update(x: Int, y: Int, v: Float): Unit
  def update(start: Int, ar: Array[Float]): Unit

  def column[M <: Column[Y]](n: Int)(implicit b: Builder[_1, Y, M]) = b.build(this(n, _))
  def row[M <: Row[X]](n: Int)(implicit b: Builder[X, _1, M]) = b.build(this(_, n))

  def mult[T <: Nat, M <: Matrix[T, Y], M2 <: Row[X], M3 <: Column[X]](o: Matrix[T, X])(implicit b: Builder[T, Y, M],
    b2: Builder[X, _1, M2], b3: Builder[_1, X, M3]) = {
    b.build(o.column(_) dot row(_))
  }

  def transpose[M <: Matrix[Y, X]](implicit b: Builder[Y, X, M]) = b.build((x, y) => this(y, x))

}



