package sudoku

import zio._
import zio.Console
import zio.json._

object Main extends ZIOAppDefault {

  def run: ZIO[Any, Throwable, Unit] =
    for {
      _ <- Console.print("Enter the path to the JSON file containing the Sudoku problem:")
      path <- Console.readLine
      _ <- Console.printLine(s"You entered: $path")
      sudokuGrid <- ZIO.fromEither(readSudokuGridFromJson(path))
      _ <- Console.printLine(s"Read Sudoku grid: ${sudokuGrid.cells}")
      // Add your Sudoku solver logic here, utilizing ZIO and interacting with the ZIO Console
    } yield ()

  private def readSudokuGridFromJson(path: String): Either[String, SudokuGrid] =
    for {
      json <- scala.io.Source.fromFile(path).mkString.trim.asRight
      sudokuGrid <- ZIO
        .fromEither(json.fromJson[SudokuGrid])
        .leftMap(_.getMessage)
    } yield sudokuGrid

  case class SudokuGrid(cells: Vector[Vector[Option[Int]]])

  object SudokuGrid {
    implicit val codec: JsonCodec[SudokuGrid] = Derivation.deriveCodec[SudokuGrid]
  }
}
