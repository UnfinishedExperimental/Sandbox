/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dheinrich.sandbox.observable

import com.jogamp.newt.event.{KeyEvent => JogAmpKeyEvent}
import com.jogamp.newt.event.KeyListener
import rx.lang.scala._

sealed trait KeyEvent
{
  val code: Int
}
case class KeyPressed(code: Int) extends KeyEvent
case class KeyReleased(code: Int) extends KeyEvent
case class KeyTyped(code: Int) extends KeyEvent

object KeyObservable {
  type KeyProvider = {
    def addKeyListener(ka: KeyListener)
//    def removeKeyListener(ka: KeyListener)
  }  
  
  private case class Listener(o:Observer[KeyEvent]) extends KeyListener
  {
    def keyReleased(ke: JogAmpKeyEvent) { o.onNext(KeyReleased(ke.getKeyCode)) }
    def keyPressed(ke: JogAmpKeyEvent) { o.onNext(KeyPressed(ke.getKeyCode)) }
    def keyTyped(ke: JogAmpKeyEvent) { o.onNext(KeyTyped(ke.getKeyCode)) }      
  }
  
  def observe(p: KeyProvider) = Observable{o : Observer[KeyEvent]=>
    val l = Listener(o)
    p.addKeyListener(l) 

    new Subscription{
      def unsubscribe() {
//        p.removeKeyListener(l)
      }
    }
  }
}