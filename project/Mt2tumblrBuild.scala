import sbt._
import sbt.Keys._

object Mt2tumblrBuild extends Build {

  lazy val mt2tumblr = Project(
    id = "mt2tumblr",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "mt2tumblr",
      organization := "net.khonda",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.2",
      resolvers ++= Seq(
	"Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
	"twttrRepo" at "http://maven.twttr.com"
      ),
      libraryDependencies ++= Seq(
	"com.typesafe.akka" % "akka-actor" % "2.0.4",
	"com.github.scala-incubator.io" %% "scala-io-core" % "0.4.1",
	"com.github.scala-incubator.io" %% "scala-io-file" % "0.4.1",
        "net.databinder" %% "dispatch-http" % "0.8.8",
	"net.databinder" %% "dispatch-http-json" % "0.8.8",
	"net.databinder" %% "dispatch-oauth" % "0.8.8",
	"com.twitter" % "util-eval"   % "5.3.13"
      )
    )
  )
}
