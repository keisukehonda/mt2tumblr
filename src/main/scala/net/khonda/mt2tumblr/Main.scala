package net.khonda.mt2tumblr

import scalax.file.Path

object Main extends {

  def main(args: Array[String]) {
    if (args.length == 0 || args(0).isEmpty) { println("usage: run /path/mtbackup.txt"); sys.exit }
    val filepath = Path(args(0), '/')
    if(!filepath.exists && !filepath.canRead)  { println("usage: run /path/mtbackup.txt"); sys.exit }    
    
    println("mt2tumblr Running with "+filepath)
    //create Parser for filepath
    val parser = Parser(args(0))
    parser.read
  }

}

