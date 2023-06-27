package sudoku

import zio._

object Main extends ZIOAppDefault {

  type SudokuGrid = Vector[Vector[Option[Int]]]
  val initialGrid: SudokuGrid = Vector( 
    Vector(Some(5), Some(3), None, None, Some(7), None, None, None, None), 
    Vector(Some(6), None, None, Some(1), Some(9), Some(5), None, None, None), 
    Vector(None, Some(9), Some(8), None, None, None, None, Some(6), None), 
    Vector(Some(8), None, None, None, Some(6), None, None, None, Some(3)), 
    Vector(Some(4), None, None, Some(8), None, Some(3), None, None, Some(1)), 
    Vector(Some(7), None, None, None, Some(2), None, None, None, Some(6)), 
    Vector(None, Some(6), None, None, None, None, Some(2), Some(8), None), 
    Vector(None, None, None, Some(4), Some(1), Some(9), None, None, Some(5)), 
    Vector(None, None, None, None, Some(8), None, None, Some(7), Some(9))
  )

  def prettyPrint(sudoku: SudokuGrid): String = {
    sudoku.grouped(3).map { bigGroup =>
      bigGroup.map { row =>
        row.grouped(3).map { smallGroup =>
          smallGroup.map(cell => 
            cell.getOrElse("-")
          ).mkString(" ", " ", " ")
        }.mkString("|", "|", "|")
      }.mkString("\n")
    }.mkString("+-------+-------+-------+\n", "\n+-------+-------+-------+\n", "\n+-------+-------+-------+")
  }

  def run: ZIO[Any, Throwable, Unit] =
    for {
      _ <- Console.print("Enter the path to the JSON file containing the Sudoku problem:")
      path <- Console.readLine
      _ <-  Console.printLine(s"You entered: $path")
      // Add your Sudoku solver logic here, utilizing ZIO and interacting with the ZIO Console
    } yield ()
}