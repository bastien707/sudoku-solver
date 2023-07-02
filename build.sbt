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
      "org.scalameta" %% "munit" 			     % "0.7.29"
    ),
  )
