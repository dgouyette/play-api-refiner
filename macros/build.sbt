name := """api-refiner"""

version := "1.0-M1"

scalaVersion := "2.13.1"

Compile / scalacOptions += "-Ymacro-annotations"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

bintrayOmitLicense := true

publishMavenStyle := true

resolvers += "dgouyette maven bintray" at "https://bintray.com/dgouyette/maven"


libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
libraryDependencies += "eu.timepit" %% "refined"                 % "0.9.9"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.4"
libraryDependencies += "com.typesafe.play" %% "play" % "2.7.3"
libraryDependencies += "com.typesafe.play" %% "routes-compiler" % "2.7.3"

