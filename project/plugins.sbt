//addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.3")
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.21")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")
addSbtPlugin("com.twilio" % "sbt-guardrail" % "0.50.0")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.7")
resolvers += "Artifactory" at "http://artifactory.yoox.net/artifactory/dpet-repo/"
resolvers += Resolver.url("bintray-sbt-plugins", url("http://dl.bintray.com/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)