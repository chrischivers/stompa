import sbt.Attributed

name := "stompa"

version := "0.1"

val fs2Version = "0.10.2"
val catsVersion = "1.5.0"
val circeVersion = "0.9.3"
val scalaTestVersion = "3.0.1"
val scalaLoggingVersion = "3.5.0"

lazy val kernelSettings = Seq(
  organization := "io.chiv",
  scalaVersion := "2.12.6",
  scalacOptions ++= Seq("-feature", "-deprecation", "-Xfatal-warnings", "-language:higherKinds"),
)

lazy val core = project
  .settings(name := "io.chiv")
  .settings(moduleName := "stompa-core")
  .settings(kernelSettings: _*)
  .settings(resolvers += "spring-plugins" at "http://repo.spring.io/plugins-release/")
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-effect" % "1.1.0",
      "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "net.ser1" % "gozirra-client" % "0.4.1",
      "io.circe"                   %% "circe-core"    % circeVersion,
      "io.circe"                   %% "circe-parser"    % circeVersion,
      "io.circe"                   %% "circe-generic"    % circeVersion % "test"

    ),
  )
  .settings(testDependencies)
  .configs(IntegrationTest)

val testDependencies = libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest"     % scalaTestVersion % "test",
  "co.fs2" %% "fs2-core" % fs2Version % "test")

lazy val fs2 = project
  .settings(name := "io.chiv")
  .settings(moduleName := "stompa-fs2")
  .settings(kernelSettings: _*)
  .aggregate(core)
  .dependsOn(core)
  .settings(Defaults.itSettings)
  .settings(
    internalDependencyClasspath in IntegrationTest += Attributed.blank((classDirectory in Test).value)
  )
  .settings(
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-core" % fs2Version
    )
  )
  .settings(testDependencies)
  .configs(IntegrationTest)
