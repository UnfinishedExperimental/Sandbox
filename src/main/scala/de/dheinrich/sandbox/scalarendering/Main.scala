/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dheinrich.sandbox.scalarendering

import shapeless._

object Main{
  import GLType._
  
  def createUniform[T <: GLType](ele : GLElement[T])(implicit b:UniformBuilder[T]) = {
    b.build(ele)
  }
  
  val includePrefix = "#pragma include"
  
  def rest(r : Stream[String]) : Stream[String] = {
    if(r.isEmpty) 
      Stream.empty 
    else{
      val include = r.head.substring(includePrefix.length).trim
      val path = "/resources/shaders/" + include
      
      val o = loadShaderFile(path)
      
      val (a,b) = r.tail.span(! _.trim.startsWith(includePrefix))
          
      o #::: a #::: rest(b)
    }
  }
  
  def loadShaderFile(path:String) : Stream[String] = {
    
    val stream = getResource(path)
    val source = io.Source.fromInputStream(stream)
    
    val lines = source.getLines()
    val (a,b) = lines.toStream.span(! _.trim.startsWith(includePrefix))
      
    a #::: rest(b)   
  }
  
  def main(param : Array[String]){
    val s = loadShaderFile("/resources/shaders/sphere.frag")
    
    s foreach println
  }
  
  def getResource(path:String) = getClass.getResourceAsStream(path)
}
