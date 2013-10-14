package de.dheinrich.sandbox

object ScalaSobel extends SobelSpeedTest{
  def main(args: Array[String]) {
    ScalaSobel.test    
  }
  
  val xrange = 1 to WIDTH - 2
  val yrange = 1 to HEIGHT - 2
  val frange = 0 to 2
  
  override def sobel(img:Array[Short]):Array[Byte] =
  {
    val byteData = new Array[Byte](WIDTH*HEIGHT)
    
    for(x <- xrange;
        y <- yrange)
    {
      var sobelx = 0f
      var sobely = 0f
      
      var i = -1
      while(i < 2)
      {
        val cc = c(i+1)
        sobelx += (getNormalizedDepth(img, x-1, y+i) - getNormalizedDepth(img, x+1, y+i))*cc        
        sobely += (getNormalizedDepth(img, x+i, y-1) - getNormalizedDepth(img, x+i, y+1))*cc
        i += 1
      }
      
      sobelx /= 4
      sobely /= 4
      
      val value = Math.sqrt(sobelx*sobelx+sobely*sobely)
      
      val depth = value * 255
      byteData(WIDTH*y+x) = depth.asInstanceOf[Byte]
    }
    
    return byteData
  }
  
  
}

