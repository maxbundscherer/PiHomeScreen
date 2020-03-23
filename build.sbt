name := "PiHomeScreen"
version := "0.1"
scalaVersion := "2.12.9"

// Add dependency on ScalaFX library
libraryDependencies += "org.scalafx" %% "scalafx" % "12.0.2-R18"

// Determine OS version of JavaFX binaries
lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
}

// Add dependency on JavaFX libraries, OS dependent
//lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
lazy val javaFXModules = Seq("base", "controls", "fxml")
libraryDependencies ++= javaFXModules.map( m =>
  "org.openjfx" % s"javafx-$m" % "12.0.2" classifier osName
)

// Scalafxml
libraryDependencies += "org.scalafx" %% "scalafxml-core-sfx8" % "0.5"

// HTTP Client
libraryDependencies += "com.softwaremill.sttp.client" %% "core" % "2.0.6"

// JSON
val circeVersion = "0.13.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

//resolvers += Resolver.sonatypeRepo("releases")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

// JAR Assembly
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

// Config Factory
libraryDependencies += "com.typesafe" % "config" % "1.4.0"

// Weather API
libraryDependencies += "com.snowplowanalytics" %% "scala-weather" % "0.5.0"

// Alias
addCommandAlias("generate-jar", ";clean;assembly")