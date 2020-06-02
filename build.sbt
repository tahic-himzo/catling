import Dependencies._

lazy val root = (project in file("."))
  .settings(
    inThisBuild(
      List(
        organization := "com.tahichimzo",
        scalaVersion := "2.12.8",
        version := "0.1.0"
      )),
    name := "catling",
    scalacOptions ++= Seq(
        "-deprecation",
        "-feature",
        "-unchecked",
        "-language:higherKinds",
        "-Yno-adapted-args",
        "-Ywarn-unused",
        "-Ywarn-macros:after",
        "-Xfatal-warnings",
        "-Xlint",
        "-Xmacro-settings:materialize-derivations",
        "-Ypartial-unification"
      ),
    libraryDependencies ++= allDeps,
    scalafmtOnCompile := true
  )
