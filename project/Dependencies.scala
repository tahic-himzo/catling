import sbt._

object Dependencies {
  val circeFs2Version   = "0.13.0"
  val circeVersion      = "0.13.0"
  val catsEffectVersion = "2.1.3"
  val sttpVersion       = "2.1.1"
  val fs2Version        = "2.2.1"

  lazy val appDeps = Seq(
    "org.typelevel"                %% "cats-effect"                    % catsEffectVersion,
    "com.softwaremill.sttp.client" %% "core"                           % sttpVersion,
    "com.softwaremill.sttp.client" %% "async-http-client-backend-cats" % sttpVersion,
    "com.softwaremill.sttp.client" %% "circe"                          % sttpVersion,
    "io.circe"                     %% "circe-core"                     % circeVersion,
    "io.circe"                     %% "circe-parser"                   % circeVersion,
    "io.circe"                     %% "circe-fs2"                      % circeFs2Version,
    "co.fs2"                       %% "fs2-core"                       % fs2Version
  )
}
