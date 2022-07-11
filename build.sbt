val LogbackVersion = "1.2.11"
val EpimetheusVersion = "0.5.0-M2"
val SparkVersion = "3.3.0"
val CatsVersion = "2.7.0"
val CatsEffectVersion = "3.3.12"
val Http4sVersion = "1.0.0-M30"
val CirceVersion = "0.14.2"

lazy val `spark-job` = project
  .settings(
    version := "0.1.0",
    scalaVersion := "2.12.14",
    scalacOptions ++= Seq(
      "-feature",
      "UTF-8",
      "-language:higherKinds",
      "-language:postfixOps"
    ),
    libraryDependencies ++= Seq(
      "ch.qos.logback"     % "logback-classic"      % LogbackVersion,
      "org.apache.spark"  %% "spark-core"           % SparkVersion,
      "org.apache.spark"  %% "spark-sql"            % SparkVersion,
      "org.typelevel"     %% "cats-core"            % CatsVersion,
      "org.typelevel"     %% "cats-effect"          % CatsEffectVersion,
      "org.http4s"        %% "http4s-blaze-client"  % Http4sVersion,
      "org.http4s"        %% "http4s-circe"         % Http4sVersion,
      "io.circe"          %% "circe-core"           % CirceVersion,
      "io.circe"          %% "circe-parser"         % CirceVersion
    )
  )

lazy val `simple-rest-api` = project
  .settings(
    version := "0.1.0",
    scalaVersion := "2.13.8",
    scalacOptions ++= Seq(
      "-feature",
      "UTF-8",
      "-language:higherKinds",
      "-language:postfixOps"
    ),
    libraryDependencies ++= Seq(
      "ch.qos.logback"     % "logback-classic"      % LogbackVersion,
      "io.chrisdavenport" %% "epimetheus"           % EpimetheusVersion,
      "org.typelevel"     %% "cats-core"            % CatsVersion,
      "org.typelevel"     %% "cats-effect"          % CatsEffectVersion,
      "org.http4s"        %% "http4s-blaze-server"  % Http4sVersion,
      "org.http4s"        %% "http4s-dsl"           % Http4sVersion,
      "org.http4s"        %% "http4s-circe"         % Http4sVersion,
      "io.circe"          %% "circe-core"           % CirceVersion,
      "io.circe"          %% "circe-parser"         % CirceVersion
    )
  )