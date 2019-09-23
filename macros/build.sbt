name := """macros"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.0"

Compile / scalacOptions += "-Ymacro-annotations"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
libraryDependencies += "eu.timepit" %% "refined"                 % "0.9.9"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.4"
libraryDependencies += "com.typesafe.play" %% "play" % "2.7.3"

