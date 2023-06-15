package examples

import parsley.Parsley
import parsley.Parsley._
import parsley.character._
import parsley.expr.chain

object Numbers extends Runnable {
  // A subtle issue with this parser is how some numbers just do not parse as they should.
  // It may take you either a few seconds or a few minutes to figure it out by looking at it,
  // especially if this is used in a much larger parser.
  // So, what is the problem with this parser? Try using the debugger!
  // Note that in your fix, the parser doesn't have to have the same types, per-se.
  // We just want a working (mathematical) integer parser.
  val digit: Parsley[Int] = satisfy(_.isDigit).map(_.asDigit)

  def digits(): Parsley[Int] = {
    var acc = 0
    lazy val ds: Parsley[Int] = digit.flatMap(d => { acc = acc * 10 + d; ds }) <|> fresh(acc)
    fresh { acc = 0 } *> ds
  }

  val brokenIntegerParser: Parsley[Int] =
    chain.prefix(
      char('-') #> ((_: Int) * -1),
      digits()
    )

  // ***** Implement your fixed parser here. Feel free to change the type. *****
  val fixedIntegerParser: Parsley[Any] = Parsley.empty

  override def run(): Unit = {
    // Input cases, these are expected to return the exact value of the shown number.
    val cases: List[String] = List(
      "123",
      "-123",
      "12345689",
      "-123456789",
      "9876543210",
      "-9876543210"
    )

    // Running the parser itself.
    for (inp <- cases) {
      println(s"BROKEN: $inp -> ${brokenIntegerParser.parse(inp)}")
      println(s" FIXED: $inp -> ${fixedIntegerParser.parse(inp)}")
    }
  }
}
