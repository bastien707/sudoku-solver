package sudoku

import io.circe.parser._
import io.circe.{Decoder, HCursor}
import zio._
import zio.ZIO
import zio.Console._

case class SudokuGrid(grid: Array[Array[Int]])

object Main extends ZIOAppDefault {

  implicit val decoder: Decoder[SudokuGrid] = (c: HCursor) => {
    c.get[Array[Array[Int]]]("grid").map(SudokuGrid.apply)
  }

  def run: ZIO[ZEnv, Throwable, Unit] =
    for {
      _    <- putStrLn("Enter the path to the JSON file containing the Sudoku problem:")
      path <- getStrLn
      grid <- readJsonFile(path)
      _    <- processSudokuGrid(grid)
    } yield ()

  def readJsonFile(path: String): ZIO[Blocking, Throwable, SudokuGrid] =
    for {
      content <- effectBlocking(scala.io.Source.fromFile(path).mkString)
      json    <- ZIO.fromEither(parse(content))
      grid    <- ZIO.fromEither(json.as[SudokuGrid])
    } yield grid

  def processSudokuGrid(grid: SudokuGrid): ZIO[Console, Nothing, Unit] =
    putStrLn(s"Received Sudoku Grid: ${grid.grid.deep.mkString("\n")}")

}

