val Http4sVersion = "0.20.3"
val CirceVersion = "0.11.1"
val ScalatestVersion = "3.0.8"
val ScalaMockVersion = "4.1.0"
val LogbackVersion = "1.2.3"
val DoobieVersion = "0.7.0"
val H2Version = "1.4.199"

lazy val root = (project in file("."))
  .settings(
    organization := "com.jinhyuk",
    name := "http4s-doobie-circe-todo",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,

      "org.tpolecat"    %% "doobie-core"         % DoobieVersion,
      "org.tpolecat"    %% "doobie-hikari"       % DoobieVersion,

      "mysql"           % "mysql-connector-java" % "8.0.16",

      "io.circe"        %% "circe-generic"       % CirceVersion,
      "io.circe"        %% "circe-generic-extras"% CirceVersion,

      "org.scalactic"   %% "scalactic"           % ScalatestVersion, 
      "org.scalatest"   %% "scalatest"           % ScalatestVersion % Test,
      "org.scalamock"   %% "scalamock"           % ScalaMockVersion % Test,

      "com.h2database"  %  "h2"                  % H2Version        % Test,

      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
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
