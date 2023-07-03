## sbt project compiled with Scala 3

### Usage

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

For more information on the sbt-dotty plugin, see the
[scala3-example-project](https://github.com/scala/scala3-example-project/blob/main/README.md).

# Purpose 
The goal of this project is to create a sudoku solver in Scala that will works by interacting with the user to get the path of the file containing the sudoku, print in the console the sudoku completed and created a file with the solution. We were required to follow the functionnal programming principles, write some tests, use a reccursive approach, and add some error handling.

# How to run

To run the project you need to have sbt installed on your computer. You can download it here : https://www.scala-sbt.org/download.html. After that you just need to clone the project and run the command sbt run in the root of the project.

Before running it's recommended to clean the project with the command sbt clean.
```
sbt clean
```

To compile the project you can use the command sbt compile.
```
sbt compile
```

To run the project you can use the command sbt run.
```
sbt run
```

To run the tests you can use the command sbt test.
```
sbt test
```

### For VSCode users
If you have any import issue and are working with VSCode it's recommended to build imports with Metals. You can do this by pressing Ctrl+Shift+P (Shift+Cmd+P) and search for Metals: Import build.

# Libraries choice

We decided to use the FunSuite library to write our tests because it's a simple library to use. We also used the scala.io library to read the file containing the sudoku nothing more.

# Data structure

We decided to use a Vector of Vector of Option[Int] to represent our sudoku. We choose this data structure because it's immutable and we can easily access to the value of a cell by using the apply() function. We have hesitated between Vector, List and Array but we choose Vector because it's also faster than List and Array and more memory efficient than Array.
There are also a set of methods to manipulate the data structure like map, filter, etc which are very useful to solve the sudoku.
We first thought to deserialize a json file to get the sudoku but we decided to use a text file because it's easier to read even if it's less user friendly than json to write a sudoku. It's a choice we made because we wanted to focus on the algorithm and not on the file format.

# Algorithm and functions 

To build the solver we only needed 3 functions quite shorts thanks to Scala syntax. We also added a prettyPrint() function in order to better see the sudoku in the console. The strength of our solver algorithm is that he compute the possibilities of each empty cells and begin to solve with the one that has the least instead of taking it randomly. This can reduce highly the complexity of the code by avoiding useless computation.

## The parseBoardFromFile() function

The parseBoardFromFile function reads a Sudoku board from a file. It returns an Either where the Right value contains the parsed board as a two-dimensional vector of optional integers, and the Left value represents any encountered errors during the parsing process. The function handles exceptions for file not found, invalid number format, and general exceptions, returning appropriate error messages in the Left case.

## The prettyPrint() function

This function take a Board in parameter and return a string containing the content of this data structure as Sudoku grid. We use the function grouped() to split our grid by block of 3 because the grid is a 9x9 size. After that, we have 3 block of 3 vectors so we split it again in 3 to have groups of 3 optionnals, we display each or put a - if there is no value. We put a | separator between the group of 3 optionals and +-----+ between the group of vectors. In the function mkString we also specify that the separator is supposed to be present as a prefix and as a suffix by setting the argument 3 times. 

### The validate() function 

This function take the grid, the column, the row and the value to put in the sudoku, and send back a Boolean to tell if the move is authorized or not. We are checking if the number passed in params is not in the row column or block of the position sent. For the rows, we just take the vector and check the value. For the columns we just apply a map to get all the number present on the same column. About finding the number presents in the same box, the process is a little more complicated. We get the quotient of the division by 3 to have starting point of box after remultiply by 3, and we use a for comprehension to gather all values. Finally we just sent the response of the row col and box to know if the number is valid. 

### The getPossibilities() function

After having a function to check the validity of an action, we needed to get a list of all the the coordinates of the empty cell and the list of the possible numbers. With a for comprehension we get all the empty cells and we check wit the validate() function which numbers are possible. Finally we sort the list by length of the possiblities list. 

### The solve() function 
Our solve() function take the grid, and the list of cells ordered with their possibilities and return a filled board or None if there's no solution. We apply a pattern matching on the list of possibilities, if the list is a Nil, that means we finish the work so we send the Board. If no we filter the possibilties computed at before the call the function to check keep the one that are still available, if this filter list is empty it means there is no solution so we send none. If there is some possibilites, we use a for comprehenion to make a reccursive call with the updated grid and the remaning values of the cells list. Finally we send the first grid of the solutions list, which is supposed to be unique, or empty because a sudoku has only one solution. Moreover, we use the Either type to handle the error if there is no solution.

# Testing

We wrote some tests to check if our functions are working properly. We tested the prettyPrint function to make sure it display the sudoku correctly. We tested the validate function to check if it return the right boolean when we try to put a number in a cell, there is tests to check that it returns true when the number is not in the row, column or block and false when it's not the case. We also tested the getPossibilities function to check if it return the right list of possibilities for a cell but also if it returns and error if the sudoku is invalid.  We tested the solve function to check if it return the right board when we give it a sudoku. Finally, we tested the parseBoardFromFile function to check if it return the right board when we give it a file containing a sudoku but also if it return an error. There are 3 Left tests to check if the function return an error when the file is not found, when the sudoku is invalid and when the number format is invalid.

# Error handling

We decided to use the Either type to handle errors. We use the Left type to return an error message and the Right type to return the result of the function. We use this type in the getPossibilities function to return an error if the sudoku is invalid. We also use it in the solve function to return an error if there is no solution. Option is also used in our code to handle the empty cells which is better than using null because it's more safe and it's a functional programming principle.