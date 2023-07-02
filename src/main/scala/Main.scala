package sudoku

import zio._
import scala.io.Source

object Main extends ZIOAppDefault {

  type Board = Vector[Vector[Option[Int]]]
  
  def parseBoardFromFile(path: String): Board = {
    val lines = Source.fromFile(path).getLines().toList
    lines.map { line =>
      line.split(" ").map {
        case "." => None
        case num => Some(num.toInt)
      }.toVector
    }.toVector
  }

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
  
  def solve(cells: List[(Int, Int, List[Int])], sudoku: Board): Either[String, Board] = {
    cells match {
      case Nil => Right(sudoku)
      case (row, col, possibilities) :: remainingCells =>
        val filteredPossibilities = possibilities.filter(validate(sudoku, col, row, _))
        if (filteredPossibilities.isEmpty) {
          Left("No solution found")
        } else {
          val solutions = for {
            nextValue <- filteredPossibilities
            updatedSudoku = sudoku.updated(row, sudoku(row).updated(col, Some(nextValue)))
            solution <- solve(remainingCells, updatedSudoku) match {
              case Left(_) => None
              case Right(s) => Some(s)
            }
          } yield solution
          solutions.headOption.toRight("No solution found")
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
        val allValues = List(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val remainingValues = allValues.filter(validate(sudoku, col, row, _))
        (row, col, remainingValues.toList.sorted)
      }.toList.sortBy(_._3.length)

      Right(possibleValues)
    }
  }
  
  def run: ZIO[Any, Throwable, Unit] =
     for {
      _ <- Console.printLine("Enter the number of the sudoku to solve (1 or 2):")
      number <- Console.readLine
      board = parseBoardFromFile(s"src/main/resources/sudoku$number.txt")
      _ <- {
        Console.printLine(prettyPrint(board)+ "\n\nSolving...\n")
      }
      _ <- getPossibleValues(board) match {
        case Left(error) =>
          Console.printLine(s"Error: $error")
        case Right(possibilities) =>
          solve(possibilities, board) match {
            case Left(error) =>
              Console.printLine(s"Error: $error")
            case Right(solved) =>
              Console.printLine(prettyPrint(solved))
          }
      }
    } yield()
}
