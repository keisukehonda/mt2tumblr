package net.khonda.mt2tumblr

import scala.io.Source
import scala.collection.mutable.StringBuilder

object Parser {
  def apply(file: String) = {
    new Parser(file)
  }
}

class Parser(file: String) {
  
  val titlePattern = """(^TITLE: )(.+)""".r
  val primarycategoryPattern = """(^PRIMARY CATEGORY: )(.+)""".r
  val categoryPattern = """(^CATEGORY: )(.+)""".r
  val datePattern = """(^DATE: )(.+)""".r  
  val linePattern = """(^-----)""".r  
  val otherPattern = """(^[A-Z].+):(.+)""".r
  val exPattern = """(^[A-Z].+:)""".r

  
  def read(start: Int): (Int, Blog) = {
    val source = Source.fromFile(file, "UTF-8")
    val block = source.getLines.drop(start).takeWhile(_ != "--------").toList
    val blocksize = block.length
    var author = ""
    var title: String = "";  var category: String = "";  var date: String = "";  
    val body = new StringBuilder()
    
    try {                      
      for (line <- block) {
	line match {	 
	  case titlePattern(p,b) => title = b	
	  case primarycategoryPattern(p,b) => 
	  case categoryPattern(p,b) => category = b
	  case datePattern(p,b) => date = b	  
	  case linePattern(p) =>
	  case otherPattern(p,b) =>
          case exPattern(p) =>  
	  case _ => body.append(line)
	}	
      }
    } finally { source.close }    
    println("body")
    println(body.toString)
    val blog = Blog(title, category, date, body.toString)        
    (if(blocksize < 1) -1 else start+blocksize, blog)

  }

}
