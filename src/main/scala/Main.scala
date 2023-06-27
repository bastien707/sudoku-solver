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

object Main extends App {

  implicit val decoder: Decoder[SudokuGrid] = (c: HCursor) => {
    c.get[Array[Array[Int]]]("grid").map(SudokuGrid.apply)
  }

  def readFile(path: String): ZIO[Blocking, Throwable, String] =
    for {
      file <- ZIO.effect(Path(path))
      byteStream <- ZStream.fromFile(file, List(StandardOpenOption.READ))
      byteArray <- byteStream.runCollect
      content = new String(byteArray.toArray, "UTF-8")
    } yield content

  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    (for {
      _ <- putStrLn("Enter the path to the JSON file containing the Sudoku problem:")
      path <- getStrLn
      _ <- putStrLn(s"You entered: $path")

      // Read the file using the `readFile` function
      content <- readFile(path)

      // Parse the JSON content into a SudokuGrid object
      result = for {
        json <- parse(content)
        sudokuGrid <- json.as[SudokuGrid]
      } yield sudokuGrid

      // Process the Sudoku grid further (e.g., solving the puzzle)
      _ <- result match {
        case Right(sudokuGrid) =>
          val grid = sudokuGrid.grid
          // Implement your Sudoku solver logic here
          putStrLn("Sudoku grid successfully parsed. Implement your solver logic here.")
        case Left(error) =>
          putStrLn(s"Error parsing JSON: $error")
      }
    } yield ExitCode.success).catchAll(error => putStrLn(s"Execution failed with error: $error") *> ZIO.succeed(ExitCode.failure))
}
