package net.khonda.mt2tumblr

import scala.io.Source

object Parser {
  def apply(file: String) = {
    new Parser(file)
  }
}

class Parser(file: String) {
  
  val s = Source.fromFile(file, "UTF-8")

  def read: Unit = {
    try {
      for (line <- s.getLines) { println(line) }
    } finally { s.close }
  }

}
