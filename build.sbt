Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  Seq(
    scalaVersion                     := "2.13.10",
    version                          := "1.0.0",
    organization                     := "com.github.0xfc963f18dc21",
    organizationName                 := "0xfc963f18dc21",
    javacOptions                    ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions                   ++= Seq("-deprecation", "-unchecked", "-feature"),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _*) => MergeStrategy.discard
      case _                        => MergeStrategy.first
    }
  )
)

lazy val root = (project in file("."))
  .settings(
    name                := "parsley-debug-examples",
    libraryDependencies += "com.github.j-mie6" %% "parsley"       % "4.2-d5c4329-SNAPSHOT",
    libraryDependencies += "com.github.j-mie6" %% "parsley-debug" % "4.2-d5c4329-SNAPSHOT",
    libraryDependencies += "com.github.0xfc963f18dc21" %% "parsley-debug-conui" % "0.1.0-SNAPSHOT",
    libraryDependencies += "com.github.0xfc963f18dc21" %% "parsley-debug-jvmui" % "0.1.0-SNAPSHOT",
    assembly / assemblyJarName := "fxgui-example.jar",
    assembly / mainClass       := Some("ReportExamples")
  )
