name := """macros"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.0"

Compile / scalacOptions += "-Ymacro-annotations"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value