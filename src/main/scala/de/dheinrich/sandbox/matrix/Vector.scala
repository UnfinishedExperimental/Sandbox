package de.dheinrich.sandbox.matrix
import shapeless._
import shapeless.Nat._
import Matrix._

trait Vector[N <: Nat] {
  val size: Int

  def apply(i: Int): Float

  def update(i: Int, v: Float): Unit

  @inline
  def inplace(o: Vector[N])(f: (Float, Float) => Float) {
    var i = 0
    while (i < size) {
      this(i) = f(this(i), o(i))
    }
  }
  @inline
  def inplace(o: Float)(f: (Float, Float) => Float) {
    var i = 0
    while (i < size) {
      this(i) = f(this(i), o)
    }
  }

  @inline
  def onCopy[M <: ColumnVector[N]](o: Vector[N])(f: (Float, Float) => Float)(implicit b:Builder[_1, N, M]) = {
    b.build(x => f(this(x), o(x)))
  }
  @inline
  def onCopy[M <: ColumnVector[N]](o: Float)(f: (Float, Float) => Float)(implicit b:Builder[_1, N, M]) = {
    b.build(x => f(this(x), o))
  }

  def unary_-[M <: ColumnVector[N]]()(implicit b:Builder[_1, N, M]) = {
    b.build(x => -this(x))
  }

  def -[M <: ColumnVector[N]](o: Vector[N])(implicit b: Builder[_1, N, M]) = onCopy(o)(_ - _)
  def -[M <: ColumnVector[N]](o: Float)(implicit b: Builder[_1, N, M]) = onCopy(o)(_ - _)
  def subI(o: Vector[N]) = inplace(o)(_ - _)
  def subI(o: Float) = inplace(o)(_ - _)

  def +[M <: ColumnVector[N]](o: Vector[N])(implicit b: Builder[_1, N, M]) = onCopy(o)(_ + _)
  def +[M <: ColumnVector[N]](o: Float)(implicit b: Builder[_1, N, M]) = onCopy(o)(_ + _)
  def addI(o: Vector[N]) = inplace(o)(_ + _)
  def addI(o: Float) = inplace(o)(_ + _)

  def *[M <: ColumnVector[N]](o: Vector[N])(implicit b: Builder[_1, N, M]) = onCopy(o)(_ * _)
  def *[M <: ColumnVector[N]](o: Float)(implicit b: Builder[_1, N, M]) = onCopy(o)(_ * _)
  def mukI(o: Vector[N]) = inplace(o)(_ * _)
  def mulI(o: Float) = inplace(o)(_ * _)

  def /[M <: ColumnVector[N]](o: Vector[N])(implicit b: Builder[_1, N, M]) = onCopy(o)(_ / _)
  def /[M <: ColumnVector[N]](o: Float)(implicit b: Builder[_1, N, M]) = onCopy(o)(_ / _)
  def divI(o: Vector[N]) = inplace(o)(_ / _)
  def divI(o: Float) = inplace(o)(_ / _)

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
    def apply[M <: Matrix[_1, N]](values: Float*)(implicit b: Matrix.Builder[_1, N, M]) = b.build(values(_))
  }

//  def apply(x: Float) = apply[_1](x)
//  def apply(x: Float, y: Float) = apply[_2](x)
//  def apply(x: Float, y: Float, z: Float) = apply[_3](x)
//  def apply(x: Float, y: Float, z: Float, w: Float) = apply[_4](x)
}


