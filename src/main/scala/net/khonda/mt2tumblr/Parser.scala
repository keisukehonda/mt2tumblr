package net.khonda.mt2tumblr

import scala.io.Source

object Parser {
  def apply(file: String) = {
    new Parser(file)
  }
}

class Parser(file: String) {
  
  def read(start: Int): (Int, Blog) = {
    val source = Source.fromFile(file, "UTF-8")
    val block = source.getLines.drop(start).takeWhile(_ != "--------").toList
    val blocksize = block.length
    
    try {                  
      
      for (line <- block) { println(line) }

    } finally { source.close }    
    val blog = Blog("title", "category", "date", "body")    
    (start+blocksize, blog)

  }

}
