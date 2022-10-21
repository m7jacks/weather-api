name := "Banno Weather"
organization := "com.banno"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.10"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
//libraryDependencies += "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % Test
//libraryDependencies += "org.mockito" % "mockito-scala-scalatest_2.13" % "1.17.12" % Test
//libraryDependencies += "org.scalatestplus" %% "mockito-4-6" % "3.2.14.0" % "test"
libraryDependencies += "org.mockito" % "mockito-core" % "4.8.1"
libraryDependencies += ws

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.banno.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.banno.binders._"
