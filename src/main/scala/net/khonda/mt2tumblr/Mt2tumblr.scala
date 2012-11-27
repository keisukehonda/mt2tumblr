package net.khonda.mt2tumblr

import akka.actor._
import akka.pattern.ask
import akka.util.duration._
import akka.util.Timeout
import com.twitter.util.Eval
import java.io.File
import net.khonda.mt2tumblr.config.{Mt2tumblr => Mt2tumblrConfig}
import scalax.file.Path

case object Tick
case object Get

class Counter extends Actor {
  var count = 0

  def receive = {
    case Tick => count += 1
    case Get  => sender ! count
  }
}

object Mt2tumblr extends App {
 
  var config = Eval[Mt2tumblrConfig](new File("./config/app.scala"))
  
  //create Parser for filepath
  val filepath = Path(config.datapath, '/')
  if(!filepath.exists && !filepath.canRead)  { println("usage: run /path/mtbackup.txt"); sys.exit }    
  
  println("mt2tumblr Running with "+config.datapath)

  val parser = Parser(config.datapath)
  val res = parser.read(0) 
  println("next blog"+ res._1)

  val system = ActorSystem("Mt2tumblr")

  val counter = system.actorOf(Props[Counter])

  counter ! Tick
  counter ! Tick
  counter ! Tick

  implicit val timeout = Timeout(5 seconds)

  (counter ? Get) onSuccess {
    case count => println("Count is " + count)
  }

  system.shutdown()
}
