import sbt.Attributed

name := "stompa"

version := "0.1"

val fs2Version = "0.10.2"
val catsVersion = "1.0.1"
val scalaTestVersion = "3.0.1"
val scalaLoggingVersion = "3.5.0"

lazy val kernelSettings = Seq(
  scalaVersion := "2.12.6",
  scalacOptions ++= Seq("-feature", "-deprecation", "-Xfatal-warnings", "-language:higherKinds")
)

lazy val core = project
  .settings(moduleName := "stompa-core")
  .settings(kernelSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion,
      "com.typesafe.scala-logging" %% "scala-logging"            % scalaLoggingVersion,
      "org.scalatest"              %% "scalatest"     % scalaTestVersion % "test",
      "co.fs2" %% "fs2-core" % fs2Version % "test"
    ),
    unmanagedJars in Compile += file("lib/gozirra-client-0.4.1.jar")
  ).configs(IntegrationTest)


lazy val fs2 = project
  .settings(moduleName := "stompa-fs2")
  .settings(kernelSettings: _*)
  .aggregate(core)
  .dependsOn(core % "test->test;compile->compile")
  .settings(Defaults.itSettings)
  .settings(
    internalDependencyClasspath in IntegrationTest += Attributed.blank((classDirectory in Test).value)
  )
  .settings(
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-core" % fs2Version
    )
  ).configs(IntegrationTest)