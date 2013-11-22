package de.dheinrich.sandbox.observable

import darwin.core.timing.{StepListener, GameTime, DeltaListener}
import rx.lang.scala.Observable
import rx.{Subscription, Observer}

/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 01.11.13
 * Time: 17:42
 * To change this template use File | Settings | File Templates.
 */

case class DeltaTimeEvent(delta: Double) extends AnyVal
case class TimeStepEvent(a: Int, b: Float, c: Float)

object TimeObservable {

  private case class Listener(o: Observer[DeltaTimeEvent]) extends DeltaListener{
    def update(p1: Double)  = o.onNext(DeltaTimeEvent(p1))
  }

  private case class Listener2(o: Observer[TimeStepEvent]) extends StepListener{
    def update(p1: Int, p2: Float, p3: Float) = o.onNext(TimeStepEvent(p1,p2,p3))
  }

  def observeDelta(time: GameTime) = Observable{o : Observer[DeltaTimeEvent]=>
    val l = Listener(o)
    time.addListener(l)

    new Subscription{
      def unsubscribe() {
        time.removeListener(l)
      }
    }
  }

  def observeStep(frequ:Int, time: GameTime) = Observable{o : Observer[TimeStepEvent]=>
    val l = Listener2(o)
    time.addListener(frequ, l)

    new Subscription{
      def unsubscribe() {
        time.removeListener(l)
      }
    }
  }
}
