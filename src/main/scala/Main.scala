package sudoku

import zio._

object Main extends ZIOAppDefault {

  type Board = Vector[Vector[Option[Int]]]

  def prettyPrint(sudoku: Board): String = {
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

  def validate(sudoku: Board, x: Int, y: Int, value: Int): Boolean = {
    val row = sudoku(y)
    val rowProperty = !row.contains(Some(value))

    val column = sudoku.map(r => r(x))
    val columnProperty = !column.contains(Some(value))

    val boxX = x / 3
    val boxY = y / 3
    val box = for {
      yb <- (boxY * 3) until (boxY * 3 + 3)
      xb <- (boxX * 3) until (boxX * 3 + 3)
    } yield sudoku(yb)(xb)
    val boxProperty = !box.contains(Some(value))

    rowProperty && columnProperty && boxProperty
  }

  /** Solves the sudoku puzzle with backtracking.
    *
    * @param sudoku
    * @param x
    * @param y
    */
  def solve(sudoku: Board, x: Int = 0, y: Int = 0): Unit = {
    (y >= 9) match {
      case true => println(prettyPrint(sudoku))
      case false =>
        (x >= 9) match {
          case true => solve(sudoku, 0, y + 1)
          case false =>
            (sudoku(y)(x).isDefined) match {
              case true =>
                solve(
                  sudoku,
                  x + 1,
                  y
                )
              case false =>
                (1 to 9)
                  .filter(value => validate(sudoku, x, y, value))
                  .foreach { value =>
                    val updatedRow = sudoku(y).updated(x, Some(value))
                    val updatedSudoku = sudoku.updated(y, updatedRow)
                    solve(updatedSudoku, x + 1, y)
                  }
            }
        }
    }
  }

  val sudokuToSolve =
      Vector(
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

  def run: ZIO[Any, Throwable, Unit] =
    for {
      _ <- Console.print("Enter the path to the JSON file containing the Sudoku problem:")
      path <- Console.readLine
      _ <-  Console.printLine(s"You entered: $path")
      // Add your Sudoku solver logic here, utilizing ZIO and interacting with the ZIO Console
    } yield ()
}
