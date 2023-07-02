package sudoku

import io.circe.parser._
import io.circe.{Decoder, HCursor}
import zio._
import zio.blocking._
import zio.console._
import zio.nio.file._
import zio.nio.file.StandardOpenOption._
import zio.stream._

case class SudokuGrid(grid: Array[Array[Int]])

  def run: ZIO[Any, Throwable, Unit] =
    for {
      _ <- Console.print("Enter the path to the JSON file containing the Sudoku problem:")
      path <- Console.readLine
      _ <- Console.printLine(s"You entered: $path")
      sudokuGrid <- ZIO.fromEither(readSudokuGridFromJson(path))
      _ <- Console.printLine(s"Read Sudoku grid: ${sudokuGrid.cells}")
      // Add your Sudoku solver logic here, utilizing ZIO and interacting with the ZIO Console
    } yield ()

  implicit val decoder: Decoder[SudokuGrid] = (c: HCursor) => {
    c.get[Array[Array[Int]]]("grid").map(SudokuGrid.apply)
  }
}
