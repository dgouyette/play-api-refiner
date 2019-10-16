name := """play-api-refiner"""
organization := "org.dgouyette"

version := "1.0-SNAPSHOT"

lazy val macros = project


lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .dependsOn(macros)


scalaVersion := "2.13.0"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
skip in publish := true

Compile / scalacOptions += "-Ymacro-annotations"

libraryDependencies ++= Seq("eu.timepit" %% "refined" % "0.9.9")

