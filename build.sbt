scalaVersion := "3.3.6"

val zioVersion    = "2.1.19"
val http4sVersion = "0.23.30"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"              % zioVersion,
  "dev.zio" %% "zio-streams"      % zioVersion,
  "dev.zio" %% "zio-interop-cats" % "23.1.0.5",
  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-server"       % http4sVersion,
  "com.comcast" %% "ip4s-core"          % "3.7.0"
)
