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
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      libraryDependencies ++= Seq(
	"com.typesafe.akka" % "akka-actor" % "2.0.4",
	"com.github.scala-incubator.io" %% "scala-io-core" % "0.4.1",
	"com.github.scala-incubator.io" %% "scala-io-file" % "0.4.1"
      )
    )
  )
}
