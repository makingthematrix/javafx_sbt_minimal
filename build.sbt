name := "javafx_sbt_minimal"

version := "0.1"
scalaVersion := "2.13.7"

// Determine OS version of JavaFX binaries
lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
}

// Add dependency on JavaFX libraries, OS dependent
lazy val javaFXModules = Seq("base", "controls", "graphics")
libraryDependencies ++= javaFXModules.map(m =>
  "org.openjfx" % s"javafx-$m" % "16" classifier osName
)

/*
Taken from https://github.com/kubukoz/steve

Create reflect-config.json and resource-config.json based on by running nativeImageAgentOutputDir :
https://github.com/scalameta/sbt-native-image#nativeimagerunagent
 */
lazy val nativeImage =
  project
    .in(file("."))
    .enablePlugins(NativeImagePlugin)
    .settings(
      Compile / mainClass := Some("minimalexample.Main"),
      nativeImageInstalled := true,
      nativeImageOptions ++= Seq(
        s"-H:ReflectionConfigurationFiles=${(Compile / resourceDirectory).value / "reflect-config.json"}",
        s"-H:ResourceConfigurationFiles=${(Compile / resourceDirectory).value / "resource-config.json"}",
        "-H:+ReportExceptionStackTraces",
        "--no-fallback",
        "--allow-incomplete-classpath",
        "--report-unsupported-elements-at-runtime",
        "--verbose",
        "-H:+JNI"
      ),
      nativeImageAgentMerge := true
    )

fork := true

