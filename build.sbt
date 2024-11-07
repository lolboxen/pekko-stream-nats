ThisBuild / organization := "com.lolboxen"
ThisBuild / version := "0.1.0"
ThisBuild / versionScheme := Some("semver-spec")

ThisBuild / scalaVersion := "3.3.4"

val pekkoVer = "1.1.2"

lazy val root = (project in file("."))
  .settings(
    name := "pekko-stream-nats"
  )

ThisBuild / libraryDependencies ++= Seq(
  "io.nats" % "jnats" % "2.19.1",
  "org.apache.pekko" %% "pekko-actor" % pekkoVer,
  "org.apache.pekko" %% "pekko-stream" % pekkoVer,
  "org.slf4j" % "slf4j-api" % "2.0.12",
  "org.apache.pekko" %% "pekko-stream-testkit" % pekkoVer % Test,
  "org.scalamock" %% "scalamock" % "6.0.0" % Test,
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)

ThisBuild / publishTo := Some("GitHub lolboxen Apache Maven Packages" at "https://maven.pkg.github.com/lolboxen/pekko-stream-nats")
ThisBuild / publishMavenStyle := true
ThisBuild / credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  "lolboxen",
  System.getenv("GITHUB_TOKEN")
)
