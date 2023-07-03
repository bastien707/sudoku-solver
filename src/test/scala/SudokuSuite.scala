import sudoku.Main

class SudokuSuite extends munit.FunSuite {

  val sudoku = Vector(
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

  val wrongSizeSudoku = Vector(
    Vector(Some(6), None, None, Some(1), Some(9), Some(5), None, None, None),
    Vector(None, Some(9), Some(8), None, None, None, None, Some(6), None),
    Vector(Some(8), None, None, None, Some(6), None, None, None, Some(3)),
    Vector(Some(4), None, None, Some(8), None, Some(3), None, None, Some(1)),
    Vector(Some(7), None, None, None, Some(2), None, None, None, Some(6)),
    Vector(None, None, None, None, Some(2), Some(8), None),
    Vector(None, None, None, Some(4), Some(1), Some(9), None, None, Some(5)),
    Vector(None, None, None, None, Some(8), None, None, Some(7), Some(9))
  )

  val noSolutionSudoku = Vector(
    Vector(Some(5), Some(3), None, None, Some(7), None, None, None, None),
    Vector(Some(6), None, None, Some(1), Some(9), Some(5), None, None, None),
    Vector(None, Some(9), Some(8), None, None, None, None, Some(6), None),
    Vector(Some(8), None, None, None, Some(6), None, None, None, Some(3)),
    Vector(Some(4), None, None, Some(8), None, Some(3), None, None, Some(1)),
    Vector(Some(7), None, None, None, Some(2), None, None, None, Some(6)),
    Vector(None, Some(6), None, None, None, None, Some(2), Some(8), None),
    Vector(None, None, None, Some(4), Some(1), Some(9), None, None, Some(5)),
    Vector(None, None, None, None, Some(8), None, None, Some(7), Some(8))
  )

  val solvedSudoku = Vector(
    Vector(Some(5), Some(3), Some(4), Some(6), Some(7), Some(8), Some(9), Some(1), Some(2)),
    Vector(Some(6), Some(7), Some(2), Some(1), Some(9), Some(5), Some(3), Some(4), Some(8)),
    Vector(Some(1), Some(9), Some(8), Some(3), Some(4), Some(2), Some(5), Some(6), Some(7)),
    Vector(Some(8), Some(5), Some(9), Some(7), Some(6), Some(1), Some(4), Some(2), Some(3)),
    Vector(Some(4), Some(2), Some(6), Some(8), Some(5), Some(3), Some(7), Some(9), Some(1)),
    Vector(Some(7), Some(1), Some(3), Some(9), Some(2), Some(4), Some(8), Some(5), Some(6)),
    Vector(Some(9), Some(6), Some(1), Some(5), Some(3), Some(7), Some(2), Some(8), Some(4)),
    Vector(Some(2), Some(8), Some(7), Some(4), Some(1), Some(9), Some(6), Some(3), Some(5)),
    Vector(Some(3), Some(4), Some(5), Some(2), Some(8), Some(6), Some(1), Some(7), Some(9))
  )

  test("prettyPrint should print a sudoku") {
    val expectedOutput =
      "+-------+-------+-------+\n" +
        "| 5 3 - | - 7 - | - - - |\n" +
        "| 6 - - | 1 9 5 | - - - |\n" +
        "| - 9 8 | - - - | - 6 - |\n" +
        "+-------+-------+-------+\n" +
        "| 8 - - | - 6 - | - - 3 |\n" +
        "| 4 - - | 8 - 3 | - - 1 |\n" +
        "| 7 - - | - 2 - | - - 6 |\n" +
        "+-------+-------+-------+\n" +
        "| - 6 - | - - - | 2 8 - |\n" +
        "| - - - | 4 1 9 | - - 5 |\n" +
        "| - - - | - 8 - | - 7 9 |\n" +
        "+-------+-------+-------+"

    val result = Main.prettyPrint(sudoku)
    assertEquals(result, expectedOutput)
  }

  test("validate should check if a value can be placed in a cell") {
    val result5 = Main.validate(sudoku, 2, 0, 4) // can place 4 at col 0, row 2
    assertEquals(result5, true)
  }

  test("validate should check if a value cannot be placed in a cell") {
    val result = Main.validate(sudoku,0, 0, 6) // can place 6 at col 0, row 0 because in same col
    assertEquals(result, false)
    val result2 = Main.validate(sudoku, 0, 0, 5) // cannot place 5 col 0, row 0 because in same row/col/square
    assertEquals(result2, false)
    val result3 = Main.validate(sudoku, 0, 2, 8) // cannot place 8 at col 0, row 2 because in same row/col/square
    assertEquals(result3, false)
    val result4 = Main.validate(sudoku, 0, 2, 9) // cannot place 9 at col 0, row 2 because in same square
    assertEquals(result4, false)
  }

  test("getPossibleValues should return an error if the sudoku is invalid") {
    val wrongResult = Main.getPossibleValues(wrongSizeSudoku)

    wrongResult match {
      case Left(errorMessage) =>
        assert(errorMessage == "Invalid Sudoku dimensions")
      case Right(values) => 
        assert(false, "Should not have returned values")
    }
  }

  test("getPossibleValues should return a list of possible values for each cell") {
    val rightResult = Main.getPossibleValues(sudoku)

    val cell1 = (4, 4, 5 :: Nil) // first cell is at col 4, row 4, and can be 5
    val cell2 = (6, 5, 7 :: Nil) // second cell is at col 6, row 5, and can be 7

    rightResult match {
      case Left(errorMessage) =>
        assert(errorMessage == "Invalid Sudoku dimensions")
      case Right(values) =>
        assertEquals(values(0), cell1)
        assertEquals(values(1), cell2)
    }
  }

  test("solve should return the solved sudoku") {
    Main.getPossibleValues(sudoku) match {
      case Left(error) =>
        assert(error == "Invalid Sudoku dimensions")
      case Right(possibilities) =>
        Main.solve(possibilities, sudoku) match {
          case Left(error) =>
            assert(error == "No solution found")
          case Right(solved) =>
            assertEquals(solved, solvedSudoku)
        }
    }
  }

  test("solve should return no solution found") {
    Main.getPossibleValues(sudoku) match {
      case Left(error) =>
        assert(error == "Invalid Sudoku dimensions")
      case Right(possibilities) =>
        Main.solve(possibilities, noSolutionSudoku) match {
          case Left(error) =>
            assert(error == "No solution found")
          case Right(solved) =>
            assert(false, "Should not have returned a solution")
        }
      }
  }

  test("parseBoardFromFile should return Left when file not found") {
    val path = "thisFileDoesNotExists.txt"
    val result = Main.parseBoardFromFile(path)
    result match {
      case Left(error) =>
        assert(error == "File not found: " + path)
      case Right(sudoku) =>
        assert(false, "Should not have returned a sudoku")
    }
  }

  test("parseBoardFromFile should return Left when file is invalid") {
    val path = "src/main/resources/sudoku3.txt"
    val result = Main.parseBoardFromFile(path)
    result match {
      case Left(error) =>
        assert(error == "Error parsing file: Invalid number format in " + path)
      case Right(sudoku) =>
        assert(false, "Should not have returned a sudoku")
    }
  }

  test("parseBoardFromFile should return Right when file is valid") {
    val path = "src/main/resources/sudoku1.txt"
    val result = Main.parseBoardFromFile(path)
    result match {
      case Left(error) =>
        assert(false, "Should not have returned an error")
      case Right(sudoku) =>
        assertEquals(sudoku, sudoku)
    }
  }
}
