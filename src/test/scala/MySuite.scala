import sudoku.Main

// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html
class MySuite extends munit.FunSuite {

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

  test("prettyPrint") {

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

    val nonExpectedOutput =
      "+-------+-------+-------+\n" +
        "| 5 3 7 | - 7 - | - - - |\n" +
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

    val result2 = Main.prettyPrint(sudoku)
    assertNotEquals(result2, nonExpectedOutput)
  }

  test("validate") {
    val result = Main.validate(sudoku,0, 0, 6) // can place 6 at col 0, row 0 because in same col
    assertEquals(result, false)
    val result2 = Main.validate(sudoku, 0, 0, 5) // cannot place 5 col 0, row 0 because in same row/col/square
    assertEquals(result2, false)
    val result3 = Main.validate(sudoku, 0, 2, 8) // cannot place 8 at col 0, row 2 because in same row/col/square
    assertEquals(result3, false)
    val result4 = Main.validate(sudoku, 0, 2, 9) // cannot place 9 at col 0, row 2 because in same square
    assertEquals(result4, false)
    val result5 = Main.validate(sudoku, 2, 0, 4) // can place 4 at col 0, row 2
    assertEquals(result5, true)
  }

  test("getPossibleValues") {
    val wrongResult = Main.getPossibleValues(wrongSizeSudoku)

    wrongResult match {
      case Left(errorMessage) =>
        assert(errorMessage == "Invalid Sudoku dimensions")
      case Right(values) =>
        fail("Expected Left with error message, but got Right with values.")
    }

    val rightResult = Main.getPossibleValues(sudoku)

    val cell1 = (4, 4, 5 :: Nil) // first cell is at col 4, row 4, and can be 5
    val cell2 = (6, 5, 7 :: Nil) // second cell is at col 6, row 5, and can be 7

    rightResult match {
      case Left(errorMessage) =>
        fail("Expected Right with values, but got Left with error message.")
      case Right(values) =>
        assertEquals(values(0), cell1)
        assertEquals(values(1), cell2)
    }
  }
}
