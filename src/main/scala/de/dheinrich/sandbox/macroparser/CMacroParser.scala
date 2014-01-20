/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dheinrich.sandbox.macroparser

import scala.util.parsing.combinator._

case class Args(p:Set[String], varagrs:Boolean)
case class Define(name:String, args:Option[Args], expand:Option[String])
case class UnDefine(name:String)

trait Cond{
  def exp:String
  def oElse: Option[Cond]
  def content: Seq[Any]
}
case class If(exp:String, content: Seq[Any], oElse: Option[Cond]) extends Cond
case class Else(content: Seq[Any]) extends Cond{
  def exp = "true"
  def oElse: Option[Cond] = None
}

object CMacroParser extends JavaTokenParsers with ImplicitConversions {
  val space = rep1(elem("Whitespace", _.isWhitespace)) 
  val newLine = rep1(elem("new line", _ == '\n')) 
  val lineStart = "(?m)^".r
  val lineEnd = rep(elem("", c => c.isWhitespace && c!='\n')) ~ (newLine | "(?m)$".r)
  def line[T](p:Parser[T]) : Parser[T] = lineStart ~> p <~ lineEnd
  
  val parameter = "(" ~> repsep(ident, ",") ~ (opt("," ~ "...") ^^ {_.isDefined}) <~ ")" ^^ 
                  {case p~b => Args(p.toSet, b)}
  val exp = "(?m)(.+)$".r
  val sameLineExp = (not(newLine) ~> exp)
  val define = lineStart ~> "#define" ~> ident ~ opt(not(space) ~> parameter) ~ opt(sameLineExp) ^^
               {case id ~ args ~ ex => Define(id, args, ex)}  
  val unDefine = line("#undef" ~> ident) ^^ UnDefine      
  
//  #if, #ifdef, #ifndef, #else, #elif und #endif
  val mIf = lineStart ~> ("#if" ||| "#ifdef" ||| "#ifndef") ~> sameLineExp ^^ {If.curried(_)}
  val mElseIf = lineStart ~> "#elif" ~> sameLineExp ^^ {If.curried(_)}
  val mElse = line("#else")
  val mEndIf = line("#endif")
  
  lazy val content = rep(lineStart ~> (cmacro | (not(mElseIf | mElse | mEndIf) ~> exp)))
  def f2[A, B](f:B=>A, b:B) = f(b)
  lazy val conditional = {
    (mIf ~ content ^^ {case b ~ c => b(c)}) ~ 
    rep(mElseIf ~ content ^^ {case b ~ c => b(c)}) ~ 
    opt(mElse ~> content ^^ {Else(_)}) <~ mEndIf ^^ {
      case i ~ ei ~ e => i(
          ei.foldRight(e:Option[Cond])((a,b) => Some(a(b)))
        )     
    }
  }
  
  lazy val cmacro : Parser[Any] = define | conditional
  lazy val cline = cmacro | exp
  
  val example = """asdasd
      #if defined HI
        #import blub.txt
      #elif CONST > 3
        #define BOO
        #define BOO2(x,y) (x*x + 2*y)
      #else
        #define HUU
        do that
      #endif
      hui booasd
      
      #define SUCKIT
     """
}

//import CMacroParser._
//parse(cline ~ conditional, example)
  
