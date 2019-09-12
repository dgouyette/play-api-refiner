name := """play-api-refined"""
organization := "com"

version := "1.0-SNAPSHOT"

lazy val macros = project


lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .dependsOn(macros)
  

scalaVersion := "2.13.0"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test


Compile / scalacOptions += "-Ymacro-annotations"



libraryDependencies ++= Seq( "eu.timepit" %% "refined"                 % "0.9.9")



// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.binders._"
