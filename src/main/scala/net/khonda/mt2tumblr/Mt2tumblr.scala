
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
import scala.annotation.tailrec
import scala.collection.immutable.Map
import scalax.file.Path


object Tumblr {
  val api = :/("api.tumblr.com") / "v2" / "blog"
  val oauth = :/("www.tumblr.com") / "oauth"
}

object Mt2tumblr extends App {

  val nrOfLimitCall: Int = 240
 
  val config = Eval[Mt2tumblrConfig](new File("./config/app.scala"))
  val consumer = Consumer(config.cons_key, config.cons_secret)
  val username = config.username
  val password = config.password
  val hostname = config.hostname
  
  //create Parser for config.datapath
  val filepath = Path(config.datapath, '/')
  if(!filepath.exists && !filepath.canRead)  { println("usage: run /path/mtbackup.txt"); sys.exit }   
  println("mt2tumblr Running with "+config.datapath)
  val parser = Parser(config.datapath)

  //get access token at once  
  val http = new Http
  val x_auth_params = Map("x_auth_mode" -> "client_auth", "x_auth_username" -> config.username, "x_auth_password" -> config.password)
  val access_token_handler = Tumblr.oauth.secure / "access_token" << x_auth_params <@ (consumer, OAuth.oob) as_token  
  val access_token = http(access_token_handler)


  start(nrOfWorkers = 10)

  case class Post(blog: Blog)
  case class Result(id: String, success: Boolean)
  case class Summary(id: String)

  class Worker extends Actor {
    val http = new Http
    
    //post one blog content to tumbler using dipatch
    def postBlog(blog: Blog) = {  
      println("submit: "+blog.id)
      val post_params = Map("type" -> "text", "title" -> blog.title, "body" -> blog.body, "date" ->blog.date)
      val post_handler = Tumblr.api / hostname / "post" << post_params <@ (consumer, access_token) >|
      val res = http(post_handler)
      Result(blog.id, true)           
    }

    def receive = {      
      case Post(blog) => sender ! postBlog(blog)
    }

  }

  class Master(nrOfWorkers: Int) extends Actor {
    //Worker router
    val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinRouter(nrOfWorkers)), 
				       name = "workerRoter")
    //aggregation result of whole blog posting
    var results: Map[String, Boolean] = Map()
    
    def receive = {
      case Post(blog) => workerRouter ! Post(blog)
      case Result(id, success) => {			
	results += (id -> success)
	if(!success || results.keys.size == nrOfLimitCall) self ! Summary(id)
      }
      case Summary(id) => {
	println("finish: "+results.keys.size+"last blog id: "+id)
	context.system.shutdown()
      }
    }
    
  }

  def start(nrOfWorkers: Int): Unit = {
    //create Actor system
    val system = ActorSystem("Mt2tumblr")

    //create master
    val master = system.actorOf(Props(new Master(nrOfWorkers)), name = "master")

    //loop for submiting whole blog data
    @tailrec
    def submit(startLine: Int): Unit = {
      Thread.sleep(1000)
      val res = parser.read(startLine)
      if(res._1 != -1) {
	val blog = res._2
	master ! Post(blog)
	val nextLine = res._1+1
	submit(nextLine)
      }
    }
    submit(0)    
    //fnish submiting and summary result
    system.shutdown()

  }

}
