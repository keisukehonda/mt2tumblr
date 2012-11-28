package net.khonda.mt2tumblr

import akka.actor._
import akka.pattern.ask
import akka.routing.RoundRobinRouter
import akka.util.duration._
import akka.util.Timeout
import com.twitter.util.Eval
import dispatch._
import dispatch.oauth.OAuth._
import dispatch.oauth.{ OAuth, Consumer }
import java.io.File
import net.khonda.mt2tumblr.config.{Mt2tumblr => Mt2tumblrConfig}
import scalax.file.Path


object Tumblr {
  val api = :/("api.tumblr.com") / "v2" / "blog"
  val oauth = :/("www.tumblr.com") / "oauth"
}

object Mt2tumblr extends App {
 
  var config = Eval[Mt2tumblrConfig](new File("./config/app.scala"))
  
  //create Parser for config.datapath
  val filepath = Path(config.datapath, '/')
  if(!filepath.exists && !filepath.canRead)  { println("usage: run /path/mtbackup.txt"); sys.exit }   
  println("mt2tumblr Running with "+config.datapath)
  val parser = Parser(config.datapath)

  start(nrOfWorkers = 10)

  case class Post(blog: Blog)
  case class Result(id: Int, success: Boolean)
  case class Summary

  class Worker extends Actor {
    //post one blog content to tumbler using dipatch
    def postBlog(blog: Blog) = {
      println(blog)
      Result(1, true)
    }

    def receive = {      
      case Post(blog) => sender ! postBlog(blog)
    }

  }

  class Master(nrOfWorkers: Int) extends Actor {
    //Worker router
    val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinRouter(nrOfWorkers)), 
				       name = "workerRoter")
    
    def receive = {
      case Post(blog) => println(blog)
      case Result(id, success) => println(id)
      case Summary => 
	println("finish")
	context.system.shutdown()
    }
    
  }

  def start(nrOfWorkers: Int): Unit = {
    //create Actor system
    val system = ActorSystem("Mt2tumblr")

    //create master
    val master = system.actorOf(Props(new Master(nrOfWorkers)), name = "master")

    //loop for submiting whole blog data    
    def submit(startLine: Int): Unit = {
      val res = parser.read(startLine)
      if(res._1 != -1) {
	val nextLine = res._1+1
	submit(nextLine)
      }
    }
    submit(0)
    
    //fnish submiting and summary result
    master ! Summary

  }
}
