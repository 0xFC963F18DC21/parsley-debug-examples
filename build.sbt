Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  Seq(
    scalaVersion     := "2.13.10",
    version          := "1.0.0",
    organization     := "com.github.0xfc963f18dc21",
    organizationName := "0xfc963f18dc21",
    javacOptions    ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions   ++= Seq("-deprecation", "-unchecked", "-feature")
  )
)

lazy val root = (project in file("."))
  .settings(
    name                := "parsley-debug-examples",
    libraryDependencies += "com.github.j-mie6" %% "parsley"       % "4.2-324a29e-SNAPSHOT",
    libraryDependencies += "com.github.j-mie6" %% "parsley-debug" % "4.2-324a29e-SNAPSHOT",
    libraryDependencies += "com.github.0xfc963f18dc21" %% "parsley-debug-jvmui" % "0.1.0-SNAPSHOT"
  )
