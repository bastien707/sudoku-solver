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

  private def solve(cells: List[(Int, Int, List[Int])], sudoku: Board): Option[Board] = {
    cells match {
      case Nil => Some(sudoku)
      case (row, col, possibilities) :: remainingCells =>
        if (possibilities.isEmpty) {
          None
        } else {
          val filteredPossibilities = possibilities.filter(validate(sudoku, col, row, _))
          filteredPossibilities.foreach { nextValue =>
            val updatedSudoku = sudoku.updated(row, sudoku(row).updated(col, Some(nextValue)))
            val solution = solve(remainingCells, updatedSudoku)
            if (solution.isDefined) {
              return solution
            }
          }
          None
        }
    }
  }

  def getPossibleValues(sudoku: Board): Either[String, List[(Int, Int, List[Int])]] = {
    val validDimensions = sudoku.length == 9 && sudoku.forall(_.length == 9)
    if (!validDimensions) {
      Left("Invalid Sudoku dimensions")
    } else {
      val cells = for {
        row <- 0 until 9
        col <- 0 until 9
        if sudoku(row)(col).isEmpty
      } yield (row, col)

      val possibleValues = cells.map { case (row, col) =>
        val allValues = Set(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val remainingValues = allValues.filter(validate(sudoku, col, row, _))
        (row, col, remainingValues.toList.sorted)
      }.toList.sortBy(_._3.length)

      Right(possibleValues)
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

  val sudokuToSolveBis =
    Vector(
      Vector(None, None, Some(5), None, Some(4), None, Some(7), None, None),
      Vector(None, Some(7), Some(4), Some(8), Some(3), Some(2), Some(1), Some(5), None),
      Vector(Some(6), Some(8), None, None, Some(1), None, None, Some(9), Some(2)),
      Vector(None, Some(9), None, Some(3), Some(6), Some(1), None, Some(7), None),
      Vector(Some(7), Some(4), Some(1), Some(2), None, Some(5), Some(6), Some(8), Some(3)),
      Vector(None, Some(5), None, Some(7), Some(8), Some(4), None, Some(1), None),
      Vector(Some(5), Some(3), None, None, Some(7), None, None, Some(2), Some(1)),
      Vector(None, Some(1), Some(7), Some(9), Some(2), Some(8), Some(3), Some(6), None),
      Vector(None, None, Some(2), None, Some(5), None, Some(9), None, None)
    )

  def run: ZIO[Any, Throwable, Unit] =
    for {
      _ <- {
        getPossibleValues(sudokuToSolveBis) match {
          case Left(error) =>
            Console.printLine(s"Error: $error")
          case Right(possibilities) =>
            solve(possibilities, sudokuToSolveBis) match {
              case Some(solution) =>
                Console.print(prettyPrint(solution))
              case None =>
                Console.printLine("No solution found.")
            }
        }
      }
      path <- Console.readLine
      _ <- Console.printLine(s"You entered: $path")
      // Add your Sudoku solver logic here, utilizing ZIO and interacting with the ZIO Console
    } yield ()
}
