import sbt.Keys._

lazy val common = Seq(
    organization := "com.squid314",
    version := "0.0.2",
    scalaVersion := "2.13.1",
    //scalaVersion := "0.23.0", // dotty compiler; doesn't work with scala.js yet
    scalacOptions ++= Seq("-deprecation", "-feature", "-Xfatal-warnings"),
    homepage := Some(url("https://github.com/squid314/scalajs-screeps")),
    licenses += ("BSD 3-Clause", url("http://opensource.org/licenses/BSD-3-Clause")),
    scmInfo := Some(ScmInfo(
        url("https://github.com/squid314/scalajs-screeps"),
        "scm:git:git@github.com:squid314/scalajs-screeps.git",
        Some("scm:git:git@github.com:squid314/scalajs-screeps.git"))),
    scalaJSLinkerConfig ~= {
        _.withModuleKind(ModuleKind.CommonJSModule)
    }
)

lazy val facade = project
    .settings(common)
    .settings(name := "scalajs-screeps")
    .enablePlugins(ScalaJSPlugin)
lazy val macros = project
    .settings(common)
    .settings(name := "screeps-reflect")
    .settings(libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value)
    .dependsOn(facade)
lazy val screeps = project
    .settings(common)
    .settings(name := "screeps")
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(facade, macros)

lazy val upload = project
    .settings(common)
    .settings(
        libraryDependencies += "io.spray" %% "spray-json" % "1.3.5",
        libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.4.2",
    )

lazy val root = project.in(file("."))
    .aggregate(facade, macros, screeps, upload)
