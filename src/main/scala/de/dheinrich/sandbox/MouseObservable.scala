package de.dheinrich.sandbox.observable

import com.jogamp.newt.{event => jogamp}
//import rx._
import rx.lang.scala._

/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 01.11.13
 * Time: 17:25
 * To change this template use File | Settings | File Templates.
 */

sealed trait MouseEvent {
  val x: Int
  val y: Int
}

case class Clicked(button: Int, count: Int, x: Int, y: Int) extends MouseEvent

case class Dragged(x: Int, y: Int) extends MouseEvent

case class Moved(x: Int, y: Int) extends MouseEvent

case class Wheel(count: Int, x: Int, y: Int) extends MouseEvent

case class Pressed(button: Int, x: Int, y: Int) extends MouseEvent

case class Released(button: Int, x: Int, y: Int) extends MouseEvent

object MouseObservable {

  type MouseProvider = {
    def addMouseListener(ka: jogamp.MouseListener)
    //    def removeKeyListener(ka: KeyListener)
  }

  private case class Listener(o: Observer[MouseEvent]) extends jogamp.MouseListener {
    def mouseClicked(e: jogamp.MouseEvent) = o.onNext(Clicked(e.getButton, e.getClickCount, e.getX, e.getY))

    def mouseDragged(e: jogamp.MouseEvent) = o.onNext(Dragged(e.getX, e.getY))

    def mouseMoved(e: jogamp.MouseEvent) = o.onNext(Moved(e.getX, e.getY))

    def mouseWheelMoved(e: jogamp.MouseEvent) = o.onNext(Wheel(e.getWheelRotation, e.getX, e.getY))

    def mousePressed(e: jogamp.MouseEvent) = o.onNext(Pressed(e.getButton, e.getX, e.getY))

    def mouseReleased(e: jogamp.MouseEvent) = o.onNext(Released(e.getButton, e.getX, e.getY))

    def mouseEntered(p1: jogamp.MouseEvent) {}

    def mouseExited(p1: jogamp.MouseEvent) {}
  }


  def observe(p: MouseProvider) = Observable{o : Observer[MouseEvent]=>
    val l = Listener(o)
    p.addMouseListener(l)

    new Subscription{
      def unsubscribe() {
        //        p.removeKeyListener(l)
      }
    }
  }


}
