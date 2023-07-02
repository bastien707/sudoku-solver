val scala3Version         = "3.3.0"
val zioVersion            = "2.0.13"
val zioJsonVersion        = "0.5.0"


lazy val root = (project in file("."))
  .settings(
    name           := "sudoku-resolver",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "dev.zio"       %% "zio"                             % zioVersion,
      "dev.zio"       %% "zio-json"                        % zioJsonVersion,
      "dev.zio"       %% "zio-streams" % "1.0.12",
      "com.softwaremill.sttp.client3" %% "core" % "3.8.15",
      "com.softwaremill.sttp.client3" %% "circe" % "3.8.15",
      "io.circe"      %% "circe-core" % "0.14.5",
      "io.circe"      %% "circe-parser" % "0.14.5",
      "org.scalameta" %% "munit" 			     % "0.7.29"
    ),
  )
