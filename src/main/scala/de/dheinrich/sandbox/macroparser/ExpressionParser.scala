package de.dheinrich.sandbox.macroparser

import scala.util.parsing.combinator.{ImplicitConversions, JavaTokenParsers}


sealed trait Expression {
  def value: Long
}

case class NumericalConst(value: Long) extends Expression

trait BinaryOp

trait UnaryOp

case class Operation(f: (Long, Long) => Long)(left: Expression, right: Expression) extends Expression

object ExpressionParser extends JavaTokenParsers with ImplicitConversions {

  type Number = Long
  type NbParser = Parser[Number]

  def binaryOp(operator: String, f: (Number, Number) => Number)(next: NbParser) = next ~ rep(operator ~> next) ^^ {
    case n ~ ns => ns.foldLeft(n)(f)
  }

  def unaryOp(operator: String, f: Number => Number)(next: NbParser) = operator ~> next ^^ {
    case ex => f(ex)
  }

  def combine(set: Set[NbParser => NbParser]) = set reduce ((a, b) => (n: NbParser) => a(n) | b(n))

  type UO = (String, Number => Number)
  type BO = (String, (Number, Number) => Number)

  val expOrder = Seq(
    (next: NbParser) => "(" ~> next <~ ")",
    combine(Set[UO](
      ("-", -_),
      ("+", +_),
      ("!", n => if (n == 0) 1 else 0),
      ("~", ~_)
    ) map unaryOp),
    combine(Set[BO](
      ("*", _ * _),
      ("/", _ / _),
      ("%", _ % _)
    ) map binaryOp),
    combine(Set[BO](
      ("+", _ + _),
      ("-", _ - _)
    ) map binaryOp),
    combine(Set[BO](
      ("<<", _ << _),
      (">>", _ >> _)
    ) map binaryOp),
    combine(Set[BO](
      ("<", if (_ < _) 1 else 0),
      ("<=", if (_ <= _) 1 else 0),
      (">", if (_ > _) 1 else 0),
      (">=", if (_ >= _) 1 else 0)
    ) map binaryOp),
    combine(Set[BO](
      ("==", if (_ == _) 1 else 0),
      ("!=", if (_ != _) 1 else 0)
    ) map binaryOp),
    binaryOp("&", _ & _) _,
    binaryOp("^", _ ^ _) _,
    binaryOp("|", _ | _) _,
    binaryOp("&&", _ && _) _,
    binaryOp("||", _ || _) _
  )

}
