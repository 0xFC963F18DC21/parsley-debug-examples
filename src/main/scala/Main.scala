import examples._

object Main {
  def main(args: Array[String]): Unit = {
    // Run all examples.
    // Feel free to comment examples out to only run a select few.
    val examples: List[Runnable] = List(
      Arithmetic
//      ArithmeticDebugged
//      Contextful
    )

    for (example <- examples) {
      example.run()
    }
  }
}
