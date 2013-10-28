package de.dheinrich.sandbox.matrix

import de.dheinrich.sandbox._
import scala.language.dynamics
import scala.language.experimental.macros

import shapeless._
import shapeless.Nat._

import Matrix._

trait Vector[N <: Nat] extends BaseVector[N]{
  val size: Int   

  @inline
  protected def inplace(o: Vector[N], f: (Float, Float) => Float) {
    var i = 0
    while (i < size) {
      this(i) = f(this(i), o(i))
      i += 1
    }
  }
  
  @inline
  protected def inplace(o: Float,f: (Float, Float) => Float) {
    var i = 0
    while (i < size) {
      this(i) = f(this(i), o)
      i += 1
    }
  }

  @inline
  protected def onCopy[M <: ColumnVector[N]](o: Vector[N], f: (Float, Float) => Float)(implicit b:Builder[_1, N, M]) = {
    b.build(x => f(this(x), o(x)))
  }
  @inline
  protected def onCopy[M <: ColumnVector[N]](o: Float, f: (Float, Float) => Float)(implicit b:Builder[_1, N, M]) = {
    b.build(x => f(this(x), o))
  }

  def unary_-[M <: ColumnVector[N]]()(implicit b:Builder[_1, N, M]) = {
    b.build(x => -this(x))
  }
  
  def test(other: BaseVector[N])(func:(Float,Float) => Float)(implicit max: ToInt[N]) = macro VectorMacros2.updateAll[N]

  def -[M <: ColumnVector[N]](o: Vector[N])(implicit b: Builder[_1, N, M]) = onCopy(o, (_:Float) - (_:Float))
  def -[M <: ColumnVector[N]](o: Float)(implicit b: Builder[_1, N, M]) = onCopy(o, (_:Float) - (_:Float))
  def subI(o: Vector[N])(implicit m: ToInt[N]) = test(o)(_ - _)
  def subI(o: Float) = inplace(o, (_:Float) - (_:Float))

  def +[M <: ColumnVector[N]](o: Vector[N])(implicit b: Builder[_1, N, M]) = onCopy(o, (_:Float) + (_:Float))
  def +[M <: ColumnVector[N]](o: Float)(implicit b: Builder[_1, N, M]) = onCopy(o, (_:Float) + (_:Float))
  def addI(o: Vector[N]) = inplace(o, (_:Float) + (_:Float))
  def addI(o: Float) = inplace(o, (_:Float) + (_:Float))

  def *[M <: ColumnVector[N]](o: Vector[N])(implicit b: Builder[_1, N, M]) = onCopy(o, (_:Float) * (_:Float))
  def *[M <: ColumnVector[N]](o: Float)(implicit b: Builder[_1, N, M]) = onCopy(o, (_:Float) * (_:Float))
  def mukI(o: Vector[N]) = inplace(o, (_:Float) * (_:Float))
  def mulI(o: Float) = inplace(o, (_:Float) * (_:Float))

  def /[M <: ColumnVector[N]](o: Vector[N])(implicit b: Builder[_1, N, M]) = onCopy(o, (_:Float) / (_:Float))
  def /[M <: ColumnVector[N]](o: Float)(implicit b: Builder[_1, N, M]) = onCopy(o, (_:Float) / (_:Float))
  def divI(o: Vector[N]) = inplace(o, (_:Float) / (_:Float))
  def divI(o: Float) = inplace(o, (_:Float) / (_:Float))

  def dot(o: Vector[N]) = {
    var d = 0f
    var i = 0
    while (i < size) {
      d += apply(i) * o(i)
      i += 1
    }

    d
  }

  def lengthSq() = this dot this
  def length() = Math.sqrt(lengthSq).toFloat
  
  def normalizeI() = divI(length)
  def normalized[M <: ColumnVector[N]](implicit b:Builder[_1, N, M]) = this / length

  override def toString() = ((0 until size) map apply mkString ("Vector(", ", ", ")")) + getClass().getClasses().map(_.getSimpleName()).mkString(", ")
}

object Vector {
  import Matrix._

  def apply[N <: Nat] = new {
    def apply[M <: ColumnVector[N]](values: Float*)(implicit b: Matrix.Builder[_1, N, M]) = b.build(values(_))
  }

  def apply[M <: ColumnVector[_3]](x: Float, y: Float, z: Float)(implicit b: Matrix.Builder[_1, _3, M]) = {
    val v = b.buildEmpty
    v(0) = x
    v(1) = y
    v(2) = z
  }  
  
  implicit class Vec3Ops(v: Vector[_3]){    
    def cross(o: Vector[_3]) = {
      val xx = v(1) * o(2) - v(2) * o(1)
      val yy = v(2) * o(0) - v(0) * o(2)
      val zz = v(0) * o(1) - v(1) * o(0)
      new Vector3(xx, yy, zz) with ColumnVector[_3]
    }
  }
}


