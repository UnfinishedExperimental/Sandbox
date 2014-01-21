package de.dheinrich.sandbox.macroparser

import scala.util.parsing.combinator.{ImplicitConversions, JavaTokenParsers}
import scala.Long
import scala.annotation.tailrec

sealed trait Expression {
  def value: Long

}

object Expression {
  import BinaryOp._
  import UnaryOp._

  implicit def IntToConst(i: Int) = NumericalConst(i)

  implicit class ExpressionOps(e: Expression){
    def +(o:Expression) = BinaryOp(add, e, o)
    def -(o:Expression) = BinaryOp(sub, e, o)
    def *(o:Expression) = BinaryOp(mul, e, o)
    def /(o:Expression) = BinaryOp(div, e, o)
    def unary_- = UnaryOp(neg, e)
  }

  val zero = NumericalConst(0)
  val one = NumericalConst(1)
  val two = NumericalConst(2)

  @tailrec
  def simplyfy(e: Expression): Expression = {
    val next = simpleStep(e)
    if(next == e)
      next
    else
      simplyfy(next)
  }

  object True
  {
    def unapply(c: NumericalConst) = if(c.value != 0) Some() else None
  }

  def simpleStep(e: Expression): Expression = {
    e match {
      //consistent
      case BinaryOp(`add`, `zero`, x) => x
      case BinaryOp(`add`, x, `zero`) => x
      case BinaryOp(`mod`, x, y) if x == y => x
      case BinaryOp(`mul`, `one`, x) => x
      case BinaryOp(`mul`, x, `one`) => x
      case BinaryOp(`div`, x, `one`) => x

      //`zero` cases
      case BinaryOp(`mul`, `zero`, _) => zero
      case BinaryOp(`mul`, _, `zero`) => zero
      case BinaryOp(`div`, `zero`, _) => zero
      case BinaryOp(`sub`, x, y) if x == y => zero
      case BinaryOp(`mod`, _, `one`) => zero
      case BinaryOp(`mod`, `zero`, _) => zero

      //unary
      case UnaryOp(`pos`, x) => x
      case UnaryOp(`neg`, UnaryOp(`neg`, x)) => x
      case BinaryOp(`sub`, UnaryOp(`neg`, x), y) => -(x + y)
      case BinaryOp(func, UnaryOp(`neg`, x), UnaryOp(`neg`, y)) => -BinaryOp(func, x, y)

      //undef
      case BinaryOp(`div`, x, `zero`) => Undef
      case BinaryOp(`mod`, x, `zero`) => Undef
      case BinaryOp(_, _, Undef) => Undef
      case BinaryOp(_, Undef, _) => Undef
      case UnaryOp(_, Undef) => Undef

      //changes
      case BinaryOp(`sub`, `zero`, x) => -x
      case BinaryOp(`add`, x, UnaryOp(`neg`, y)) => x - y
      case BinaryOp(`add`, x, y) if x == y => y * 2
      case BinaryOp(`add`, BinaryOp(`mul`, a, x), y) if x == y => x * (a + 1)
      case BinaryOp(`add`, x, BinaryOp(`mul`, a, y)) if x == y => y * (a + 1)
      case BinaryOp(`sub`, BinaryOp(`mul`, a, x), y) if x == y => x * (a - 1)
      case BinaryOp(`div`, BinaryOp(`mul`, a, x), y) if x == y => x * (a - 1)

      //extract
      case BinaryOp(func, BinaryOp(`mul`, a, x), BinaryOp(`mul`, b, y))
        if x == y && (func == add || func == sub) => BinaryOp(func, a, b) * x
      case BinaryOp(func, BinaryOp(`mul`, x, a), BinaryOp(`mul`, b, y))
        if x == y && (func == add || func == sub) => BinaryOp(func, a, b) * x
      case BinaryOp(func, BinaryOp(`mul`, a, x), BinaryOp(`mul`, y, b))
        if x == y && (func == add || func == sub) => BinaryOp(func, a, b) * x
      case BinaryOp(func, BinaryOp(`mul`, x, a), BinaryOp(`mul`, y, b))
        if x == y && (func == add || func == sub) => BinaryOp(func, a, b) * x

      //logic
      case BinaryOp(BinaryOp.==, x, y) if x == y => one
      case BinaryOp(`<=`, x, y) if x == y => one
      case BinaryOp(`>=`, x, y) if x == y => one
      case BinaryOp(`<`, x, y) if x == y => zero
      case BinaryOp(`>`, x, y) if x == y => zero
      case BinaryOp(`>`, UnaryOp(`neg`, x), UnaryOp(`neg`, y)) => BinaryOp(<, x, y)
      case BinaryOp(`<`, UnaryOp(`neg`, x), UnaryOp(`neg`, y)) => BinaryOp(>, x, y)
      case BinaryOp(`>=`, UnaryOp(`neg`, x), UnaryOp(`neg`, y)) => BinaryOp(<=, x, y)
      case BinaryOp(`<=`, UnaryOp(`neg`, x), UnaryOp(`neg`, y)) => BinaryOp(>=, x, y)
      case BinaryOp(`||`, True(_), _) => one
      case BinaryOp(`||`, _, True(_)) => one
      case BinaryOp(`&&`, `zero`, _) => zero
      case BinaryOp(`&&`, _, `zero`) => zero
      case BinaryOp(`&&`, True(_), True(_)) => one

      //binary
      case BinaryOp(`<<`, x, `zero`) => x
      case BinaryOp(`>>`, x, `zero`) => x
      case BinaryOp(`<<`, `zero`, _) => zero
      case BinaryOp(`>>`, `zero`, _) => zero

      //inception
      case BinaryOp(f, x, y) => BinaryOp(f, simpleStep(x), simpleStep(y))
      case UnaryOp(f, x) => UnaryOp(f, simpleStep(x))
      case x => x
    }
  }

}

case class NumericalConst(value: Long) extends Expression
{
  override def toString = value.toString
}

case class Identifier(name: String, values: Map[String, Long]) extends Expression {
  def value = values(name)
}


object Undef extends Expression
{
  def value = ???
}

case class BinaryOp(func: (Long, Long) => Long, left: Expression, right: Expression) extends Expression {
  def value = func(left.value, right.value)

  override def toString = {
    import BinaryOp._
    val symbol = func match{
      case `add` => "+"
      case `sub` => "-"
      case `mul` => "*"
      case `div` => "/"
      case _ => "?"
    }

    s"($left $symbol $right)"
  }
}

object BinaryOp {
  def cbool(f: (Long, Long) => Boolean): (Long, Long) => Long = (a, b) => if (f(a, b)) 1 else 0

  val add = (_: Long) + (_: Long)
  val sub = (_: Long) - (_: Long)
  val mul = (_: Long) * (_: Long)
  val div = (_: Long) / (_: Long)
  val mod = (_: Long) % (_: Long)

  val << = (_: Long) << (_: Long)
  val >> = (_: Long) >> (_: Long)

  val < = cbool((_: Long) < (_: Long))
  val > = cbool((_: Long) > (_: Long))

  val <= = cbool((_: Long) <= (_: Long))
  val >= = cbool((_: Long) >= (_: Long))
  val == = cbool((_: Long) == (_: Long))
  val != = cbool((_: Long) != (_: Long))

  val & = (_: Long) & (_: Long)
  val | = (_: Long) | (_: Long)
  val ^ = (_: Long) ^ (_: Long)

  val && = cbool((_: Long) != 0 && (_: Long) != 0)
  val || = cbool((_: Long) != 0 || (_: Long) != 0)
}

case class UnaryOp(func: Long => Long, exp: Expression) extends Expression {
  def value = func(exp.value)

  override def toString = {
    import UnaryOp._
    val symbol = func match{
      case `neg` => "-"
      case `pos` => ""
      case _ => "?"
    }

    s"$symbol$exp)"
  }
}

object UnaryOp {
  val neg = -(_: Long)
  val pos = +(_: Long)
  val opp_! = (n: Long) => if (n == 0) 1l else 0l
  val opp_~ = ~(_: Long)
}


object ExpressionParser extends JavaTokenParsers with ImplicitConversions {

  val parser = {
    type Number = Long
    type NbParser = Parser[Expression]

    type UO = (String, Number => Number)
    type BO = (String, (Number, Number) => Number)

    def binaryReduce(f: (Number, Number) => Number): PartialFunction[~[Expression, List[Expression]], Expression] = {
      case n ~ ns => {
        val all = n +: ns
        all.reduce((a, b) => BinaryOp(f, a, b))
      }
    }

    def binaryOp(t: BO)(next: NbParser) = next ~ rep(t._1 ~> next) ^^ binaryReduce(t._2)
    def binary(t: BO)(next: NbParser) = next ~ rep1(t._1 ~> next) ^^ binaryReduce(t._2)

    def unary(t: UO)(next: NbParser) = t._1 ~> next ^^ {
      case n => UnaryOp(t._2, n)
    }

    def combine(set: Set[NbParser => NbParser]) = set reduce ((a, b) => (n: NbParser) => a(n) | b(n))

    import UnaryOp._
    import BinaryOp._

    lazy val exp1 = "(" ~> exp12 <~ ")" | (decimalNumber ^^ {
      case l => new NumericalConst(l.toLong)
    })
    lazy val exp2 = combine(Set[UO](
      ("-", neg),
      ("+", pos),
      ("!", opp_!),
      ("~", opp_~)
    ) map unary)(exp1) | exp1
    lazy val exp3 = combine(Set[BO](
      ("/", div),
      ("*", mul),
      ("%", mod)
    ) map binary)(exp2) | exp2

    lazy val exp4 = combine(Set[BO](
      ("+", add),
      ("-", sub)
    ) map binary)(exp3) | exp3
    lazy val exp5 = combine(Set[BO](
      ("<<", <<),
      (">>", >>)
    ) map binary)(exp4) | exp4
    lazy val exp6 = combine(Set[BO](
      ("<", <),
      ("<=", <=),
      (">", >),
      (">=", >=)
    ) map binary)(exp5) | exp5
    lazy val exp7 = combine(Set[BO](
      ("==", BinaryOp.==),
      ("!=", BinaryOp.!=)
    ) map binary)(exp6) | exp6
    lazy val exp8 = binaryOp("&", &)(exp7)
    lazy val exp9 = binaryOp("^", ^)(exp8)
    lazy val exp10 = binaryOp("|", |)(exp9)
    lazy val exp11 = binaryOp("&&", &&)(exp10)
    lazy val exp12: NbParser = binaryOp("||", ||)(exp11)
    exp12
  }
}
