import sbt._

object Dependencies {

  val catsEffectVersion = "2.1.3"
  val fs2Version        = "2.2.1"
  lazy val coreDeps = Seq(
    "org.typelevel" %% "cats-effect" % catsEffectVersion,
    "co.fs2"        %% "fs2-core"    % fs2Version
  )

  val circeVersion           = "0.13.0"
  val circeDerivationVersion = "0.13.0-M4"
  lazy val circeDeps = Seq(
    "com.softwaremill.sttp.client" %% "circe"            % sttpVersion,
    "io.circe"                     %% "circe-core"       % circeVersion,
    "io.circe"                     %% "circe-parser"     % circeVersion,
    "io.circe"                     %% "circe-derivation" % circeDerivationVersion,
    "io.circe"                     %% "circe-fs2"        % circeVersion
  )

  val http4sVersion = "0.21.4"
  lazy val http4sDeps = Seq(
    "org.http4s" %% "http4s-core"         % http4sVersion,
    "org.http4s" %% "http4s-dsl"          % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-circe"        % http4sVersion
  )

  val doobieVersion   = "0.9.0"
  val postgresVersion = "42.2.12"
  lazy val sqlDeps = Seq(
    "org.tpolecat"   %% "doobie-core"      % doobieVersion,
    "org.tpolecat"   %% "doobie-scalatest" % doobieVersion,
    "org.postgresql" % "postgresql"        % postgresVersion
  )

  val sttpVersion = "2.1.1"
  lazy val sttpDeps = Seq(
    "com.softwaremill.sttp.client" %% "core"                           % sttpVersion,
    "com.softwaremill.sttp.client" %% "async-http-client-backend-cats" % sttpVersion
  )

  val openCsvVersion = "5.2"
  lazy val csvDeps = Seq(
    "com.opencsv" % "opencsv" % openCsvVersion
  )

  val scalaTestVersion = "3.1.1"
  lazy val testDeps = Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test
  )

  lazy val allDeps = coreDeps ++ circeDeps ++ http4sDeps ++ sqlDeps ++ sttpDeps ++ csvDeps ++ testDeps

}
