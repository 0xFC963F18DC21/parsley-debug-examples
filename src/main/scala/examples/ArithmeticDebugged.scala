package examples

import parsley.Parsley
import parsley.character.{char, satisfy}
import parsley.combinator.many
import parsley.debug.DebugCombinators
import parsley.debugger.util.Collectors
import parsley.expr.{InfixL, Ops, precedence}
import parsley.genericbridges.{ParserBridge1, ParserBridge2}

object ArithmeticDebugged extends Runnable {
  sealed trait Arith

  case class PosInt(b: BigInt) extends Arith
  object PosInt extends ParserBridge1[BigInt, PosInt]

  case class Add(l: Arith, r: Arith) extends Arith
  object Add extends ParserBridge2[Arith, Arith, Add]

  case class Sub(l: Arith, r: Arith) extends Arith
  object Sub extends ParserBridge2[Arith, Arith, Sub]

  case class Mul(l: Arith, r: Arith) extends Arith
  object Mul extends ParserBridge2[Arith, Arith, Mul]

  case class Div(l: Arith, r: Arith) extends Arith
  object Div extends ParserBridge2[Arith, Arith, Div]

  val int: Parsley[Arith] =
    PosInt(
      satisfy(_.isDigit)
        .debug("digit")
        .foldLeft1(BigInt(0))((acc, c) => acc * 10 + c.asDigit)
        .debug("int")
    ).debug("PosInt")

  lazy val expr: Parsley[Arith] =
    precedence[Arith](
      int,
      (char('(') ~> expr <~ char(')')).debug("bracketed")
    )(
      Ops(InfixL)(
        char('*').debug("Mul") #> Mul.apply,
        char('/').debug("Div") #> Div.apply
      ),
      Ops(InfixL)(
        char('+').debug("Add") #> Add.apply,
        char('-').debug("Sub") #> Sub.apply
      )
    ).debug("expr")

  lazy val prog: Parsley[List[Arith]] =
    many(many(satisfy("\r\n".contains(_))) ~> expr).debug("prog")

  override def run(): Unit = {
    Collectors.names(this)
    println(
      prog.parse(
        "1+2*3-4/2"
      )
    )
  }
}
