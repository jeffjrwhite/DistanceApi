val Http4sVersion = "0.20.23"
val CirceVersion = "0.11.1"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"
lazy val doobieVersion = "0.5.4"

lazy val root = (project in file("."))
  .settings(
    organization := "com.ynap",
    name := "dpetapi",
    version := "1.0.1",
    scalaVersion := "2.12.11",
    libraryDependencies ++= Seq(
      "org.tpolecat"          %% "doobie-core"            % doobieVersion,
      "org.tpolecat"          %% "doobie-h2"              % doobieVersion,
      "org.tpolecat"          %% "doobie-hikari"          % doobieVersion,
      "org.tpolecat"          %% "doobie-specs2"          % doobieVersion,
      "io.circe"              %% "circe-core"             % CirceVersion,
      "io.circe"              %% "circe-config"           % "0.6.1",
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "io.circe"        %% "circe-java8"         % CirceVersion,
      "org.specs2"      %% "specs2-core"         % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0")
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
  "-Xfatal-warnings",
)

guardrailTasks in Compile := List(
  ScalaServer(
    specPath = (Compile / resourceDirectory).value / "api.yaml",
    pkg = "com.ynap.dpetapi.endpoints",
    framework = "http4s",
    tracing = false
  )
)

fork in Test := true
autoAPIMappings := true
unmanagedBase := baseDirectory.value / "libs"
unmanagedJars in Compile += file("libs")
publishTo := Some("Artifactory Realm" at "http://artifactory.yoox.net/artifactory/dpet-repo")
credentials += Credentials("Artifactory Realm", "artifactory.yoox.net", "svc_dataprovisioning", "AP7qcVPRkTLXDyeXmNpVdZ4Mykz")
resolvers += "Artifactory" at "http://artifactory.yoox.net/artifactory/tesla-repo/"
resolvers += "Artifactory" at "http://artifactory.yoox.net/artifactory/dpet-repo/"