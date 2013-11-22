package de.dheinrich.sandbox

import rx.lang.scala._
import rx.lang.scala.ImplicitFunctionConversions._
import subjects._
import rx.subjects.PublishSubject
import scala.swing.event.KeyReleased

object asdsada {

  sealed trait MouseEvent
  case class MouseDown extends MouseEvent
  case class MouseUp extends MouseEvent
  case class MouseMove(x: Int, y: Int) extends MouseEvent

  sealed trait KeyEvent
  case class KeyPressed(code: Int) extends KeyEvent
  case class KeyReleased(code: Int) extends KeyEvent

  val pub = PublishSubject.create[MouseEvent]()
  val obs = Observable(pub)

  //val a = obs.firstOf[KeyPressed]

  var l: List[(Int, Int)] = Nil

  def paths(o: Observable[MouseEvent]): Observable[MouseEvent] = {
    val down = o.dropWhile(!_.isInstanceOf[MouseDown])
    down.takeUntil(down.firstOf[MouseUp])
  }

  paths(obs).repeate().subscribe(println(_))

  pub.onNext(MouseMove(2, 1))
  pub.onNext(MouseDown())
  pub.onNext(MouseMove(2, 1))
  pub.onNext(MouseMove(2, 5))
  pub.onNext(MouseMove(2, 3))
  pub.onNext(MouseUp())
  pub.onNext(MouseDown())
  pub.onNext(MouseMove(3, 6))
  pub.onNext(MouseMove(3, 4))
  pub.onNext(MouseUp())

  implicit class ObservableOps[T](ob: Observable[T]) {
    def firstOf[E <: T](implicit m: ClassManifest[E]) = {
      ob.filter(_.getClass.isAssignableFrom(m.runtimeClass)).
        map(_.asInstanceOf[E]).first
    }
  }
}