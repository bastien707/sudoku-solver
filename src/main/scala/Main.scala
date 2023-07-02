package sudoku

import io.circe.parser._
import io.circe._
import zio._
import zio.nio.file.{Files, Path}
import zio.blocking.Blocking
import zio.stream._
import zio.stream.ZStream.Pull.Effect

case class SudokuGrid(grid: Array[Array[Int]])

object Main extends App {

  implicit val decoder: Decoder[SudokuGrid] = (c: HCursor) => {
    c.get[Array[Array[Int]]]("grid").map(SudokuGrid.apply)
  }

  def run(args: List[String]): ZIO[Any, Nothing, ExitCode] =
    (for {
      _     <- putStrLn("Enter the path to the JSON file containing the Sudoku problem:")
      path  <- getStrLn
      grid  <- readJsonFile(path)
      _     <- printGrid(grid)
    } yield ExitCode.success).catchAll(error => putStrLn(s"Execution failed with error: $error") *> ZIO.succeed(ExitCode.failure))

  def readJsonFile(path: String): ZIO[Any, Throwable, SudokuGrid] =
    for {
      file   <- ZIO.effect(Path(path))
      bytes  <- Files.readAllBytes(file)
      json   <- ZIO.fromEither(parse(new String(bytes.toArray)))
      grid   <- ZIO.fromEither(json.as[SudokuGrid])
    } yield grid

  def printGrid(grid: SudokuGrid): ZIO[Console, Nothing, Unit] =
    ZIO.effectTotal(grid.grid.foreach(row => println(row.mkString(" "))))
  
}
