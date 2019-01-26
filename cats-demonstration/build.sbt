name := "cats-demonstration"

version := "0.1"

scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds",
  "-Ypartial-unification")

libraryDependencies += "org.typelevel" %% "cats-core" % "1.5.0"
libraryDependencies += "org.typelevel" %% "cats-kernel" % "1.5.0"
libraryDependencies += "org.typelevel" %% "cats-macros" % "1.5.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "1.2.0" withSources() withJavadoc()
libraryDependencies += "org.typelevel" %% "mouse" % "0.20"
libraryDependencies += "org.typelevel" %% "cats-effect-laws" % "1.2.0" % "test"