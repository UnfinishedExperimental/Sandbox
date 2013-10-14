/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dheinrich.sandbox
import shapeless._
import Nat._

object Bullshit {

  trait B[E] {
    def get(): String
  }

  object B {
    implicit object B11 extends B[Int] {
      def get() = "B11"
    }
  }

  trait A[E <: Nat, F <: Nat] {
    def get(): String
  }

  object A extends Lower {
    implicit object A11 extends A[_1, _1] {
      def get() = "1on1"
    }
  }

  trait Lower extends Lowerer {
    implicit def A1N[N <: Nat](implicit ev: LT[_1, N]) = new A[_1, N] {
      def get() = "1onN"
    }
    implicit def AN1[N <: Nat](implicit ev: LT[_1, N]) = new A[N, _1] {
      def get() = "Non1"
    }
  }

  trait Lowerer {
    implicit def A1NA[N <: Nat]() = new A[_1, N] {
      def get() = "1onN unspecific"
    }
    implicit def AN1A[N <: Nat]() = new A[N, _1] {
      def get() = "Non1 unspecific"
    }
  }
}
