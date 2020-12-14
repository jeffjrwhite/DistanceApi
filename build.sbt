import sbt.project
import sbt.Keys.{libraryDependencies, _}

fork in Test := true
test in assembly := {}

val Http4sVersion = "0.20.23"
val CirceVersion = "0.11.1"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"
val doobieVersion = "0.9.0"

lazy val DataProvisioningAPI = (project in file("."))
  .settings(
    organization := "com.none2clever",
    name := "distanceapi",
    version := "1.0.1.003",
    scalaVersion := "2.12.7",
    mainClass in assembly := Some("cucumber.api.cli.Main"),
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.2.1",
      "joda-time" % "joda-time" % "2.10.1",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test",
      "com.zaxxer" % "HikariCP" % "2.7.9",
      "net.liftweb" %% "lift-json" % "3.3.0",
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-core"          % CirceVersion,
      "io.circe"        %% "circe-config"        % "0.6.1",
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "io.circe"        %% "circe-java8"         % CirceVersion,
      "io.circe"        %% "circe-literal"       % CirceVersion,
      "org.specs2"      %% "specs2-core"         % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "org.webjars" % "webjars-locator" % "0.34",
      "org.webjars" % "swagger-ui"      % "3.17.3",
      "io.jsonwebtoken" % "jjwt" % "0.9.1",
      "commons-codec" % "commons-codec" % "1.15",
      "org.tpolecat" %% "doobie-core"     % doobieVersion,
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "org.tpolecat" %% "doobie-specs2"   % doobieVersion,
      "com.chuusai" %% "shapeless" % "2.3.3"
    ).map(_.
      exclude("com.yoox.tesla.dsl.212", "tesla-core_2.12")),
    libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-text" % "1.6",
      "org.postgresql" % "postgresql" % "42.2.5",
      "com.typesafe.slick" %% "slick" % "3.3.2",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
      "com.microsoft.sqlserver" % "mssql-jdbc" % "7.0.0.jre8"
),
addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0")
).settings(commonsettings: _*).enablePlugins(LauncherJarPlugin)

lazy val commonsettings = Seq(
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case x => MergeStrategy.first
  },
  scalaVersion := "2.12.7",
  fork in run := true,
  javaOptions in run += "-Dhttp.port=<port>",
  autoAPIMappings := true,
  unmanagedBase := baseDirectory.value / "libs",
  unmanagedJars in Compile += file("libs")
)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification"
)

guardrailTasks in Compile := List(
  ScalaServer(
    specPath = (Compile / resourceDirectory).value / "api.yaml",
    pkg = "com.none2clever.dapi.endpoints",
    framework = "http4s",
    tracing = false
  )
)