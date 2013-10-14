package de.dheinrich.sandbox.matrix

import shapeless._
import Nat._
import Matrix._

class GenMatrix[X <: Nat, Y <: Nat](implicit val toIntX: ToInt[X], val toIntY: ToInt[Y]) extends Matrix[X, Y] {

  val width = toIntX()
  val height = toIntY()
  val size = width * height

  private val data = new Array[Float](size)

  def apply(x: Int, y: Int) = {
    require(x < width)
    require(y < height)
    data(width * y + x)
  }

  def update(x: Int, y: Int, v: Float) {
    require(x < width)
    require(y < height)
    data(width * y + x) = v
  }

  def update(start: Int, ar: Array[Float]) {
    Array.copy(ar, start, data, 0, data.length)
  }

  override def row[M <: Row[X]](n: Int)(implicit b: Builder[X, _1, M]) = {
    val m = b.buildEmpty()
    m(n * width) = data
    m
  }

  override def toString() = {
    data.toSeq.
      grouped(width).
      map(_.mkString(",\t")).
      mkString("\n[", "\n", "]")
  }
}
//abstract classtrait BaseVector[N <: Nat](implicit val toInt: ToInt[N]) extends Vector[N] {
//  val size = toInt()
//}

class Vector3(var x: Float = 0, var y: Float = 0, var z: Float = 0) extends Vector[_3] {
  override val size = 3

  def apply(i: Int) = i match {
    case 0 => x
    case 1 => y
    case 2 => z
  }

  def update(i: Int, v: Float) {
    i match {
      case 0 => x = v
      case 1 => y = v
      case 2 => z = v
    }
  }

  def update(start: Int, ar: Array[Float]) {
    x = ar(start)
    y = ar(start + 1)
    z = ar(start + 2)
  }

  def dot(ov: Vector3) = x * ov.x + y * ov.y + z * ov.z
  override def dot(o: Vector[_3]) = dot(o.asInstanceOf[Vector3])

  def cross(o: Vector3) = {
    val xx = y * o.z - z * o.y
    val yy = z * o.x - x * o.z
    val zz = x * o.y - y * o.x
    new Vector3(x, y, z) with ColumnVector[_3]
  }
}

class ArrayVector[N <: Nat](implicit val toInt: ToInt[N]) extends Vector[N] {
  val size = toInt()

  private val data = new Array[Float](size)

  def apply(i: Int) = {
    require(i < size)
    data(i)
  }

  def update(i: Int, v: Float) {
    require(i < size)
    data(i) = v
  }

  def update(start: Int, ar: Array[Float]) {
    Array.copy(ar, start, data, 0, data.length)
  }
}

trait RowVector[N <: Nat] extends Matrix[N, _1] with Vector[N] {
  def apply(x: Int, y: Int): Float = apply(x)

  def update(x: Int, y: Int, v: Float) {
    update(x, v)
  }

  def row(n: Int) = {
    require(n == 0)
    this
  }

  override def row[M <: Row[N]](n: Int)(implicit b: Builder[N, _1, M] = null) = {
    require(n == 0, s"You can call only the first Row not: $n")
    this.asInstanceOf[M]
  }

  def column(n: Int) = Vector1(apply(n))
  override def column[M <: Column[_1]](n: Int)(implicit b: Builder[_1, _1, M] = null) = Vector1(apply(n)).asInstanceOf[M]
}

trait ColumnVector[N <: Nat] extends Matrix[_1, N] with Vector[N] {
  def apply(x: Int, y: Int): Float = apply(y)

  def update(x: Int, y: Int, v: Float) {
    update(y, v)
  }

  def row(n: Int) = Vector1(apply(n))

  def column(n: Int) = {
    require(n == 0)
    this
  }

  override def row[M <: Row[_1]](n: Int)(implicit b: Builder[_1, _1, M] = null) = Vector1(apply(n)).asInstanceOf[M]

  override def column[M <: Column[N]](n: Int)(implicit b: Builder[_1, N, M] = null) = {
    require(n == 0)
    this.asInstanceOf[M]
  }
}

case class Vector1(var value: Float) extends Vector[_1] with Matrix[_1, _1] {

  val size = 1

  def apply(i: Int) = {
    require(i == 0)
    value
  }

  def update(i: Int, v: Float) {
    require(i == 0)
    value = v
  }

  def apply(x: Int, y: Int): Float = apply(x + y)
  def update(x: Int, y: Int, v: Float) = update(x + y, v)

  def update(start: Int, ar: Array[Float]) {
    value = ar(start)
  }

  def row(n: Int) = {
    require(n == 0)
    this
  }
  def column(n: Int) = {
    require(n == 0)
    this
  }

  override def row[M <: Row[_1]](n: Int)(implicit b: Builder[_1, _1, M] = null) = {
    require(n == 0)
    this.asInstanceOf[M]
  }
  override def column[M <: Column[_1]](n: Int)(implicit b: Builder[_1, _1, M] = null) = row(n)
}