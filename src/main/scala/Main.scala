package sudoku

import zio._
import scala.io.Source

object Main extends ZIOAppDefault {

  type Board = Vector[Vector[Option[Int]]]
  
  def parseBoardFromFile(path: String): Either[String, Board] = {
    try {
      val lines = Source.fromFile(path).getLines().toList
      val ret = lines.map { line =>
        line.split(" ").map {
          case "." => None
          case num => Some(num.toInt)
        }.toVector
      }.toVector
      Right(ret)
    } catch {
      case _: java.io.FileNotFoundException =>
        Left(s"File not found: $path")
      case ex: java.lang.NumberFormatException =>
        Left(s"Error parsing file: Invalid number format in $path")
      case ex: Throwable =>
        Left(s"Error parsing file: ${ex.getMessage}")
    }
  }

  def parseBoardToFile(path: String, board: Board): Either[String, Unit] = {
    try {
      val lines = board.map { row =>
        row.map {
          case None => "."
          case Some(num) => num.toString
        }.mkString(" ")
      }
      val file = new java.io.PrintWriter(path)
      try {
        lines.foreach(file.println)
      } finally {
        file.close()
      }
      Right(())
    } catch {
      case ex: Throwable =>
        Left(s"Error writing file: ${ex.getMessage}")
    }
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
      _ <- parseBoardFromFile(s"src/main/resources/sudoku$number.txt") match {
        case Left(error) =>
          Console.printLine(s"Error while parsing : $error")
        case Right(board) => 
          println(prettyPrint(board)+ "\n\nSolving...\n")
          getPossibleValues(board) match {
            case Left(error) =>
              Console.printLine(s"Error: $error")
            case Right(possibilities) =>
              solve(possibilities, board) match {
                case Left(error) =>
                  Console.printLine(s"Error: $error")
                case Right(solved) =>
                  parseBoardToFile(s"src/main/resources/sudoku$number-solved.txt", solved)
                  Console.printLine(prettyPrint(solved))
          }
        }
      }
    } yield()
}
