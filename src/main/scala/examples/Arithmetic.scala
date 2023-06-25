package examples

import parsley.Parsley
import parsley.character.{char, satisfy}
import parsley.combinator.many
import parsley.debugger.util.Collectors
import parsley.expr.{precedence, InfixL, Ops}
import parsley.genericbridges.{ParserBridge1, ParserBridge2}

object Arithmetic extends Runnable {
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
        .foldLeft1(BigInt(0))((acc, c) => acc * 10 + c.asDigit)
    )

  lazy val expr: Parsley[Arith] =
    precedence[Arith](
      int,
      char('(') ~> expr <~ char(')')
    )(
      Ops(InfixL)(
        char('*') #> Mul.apply,
        char('/') #> Div.apply
      ),
      Ops(InfixL)(
        char('+') #> Add.apply,
        char('-') #> Sub.apply
      )
    )

  lazy val prog: Parsley[List[Arith]] =
    many(many(satisfy("\r\n".contains(_))) ~> expr)

  override def run(): Unit = {
    Collectors.names(this)
    println(
      prog.parse(
        "1+2*3-4/2"
      )
    )
  }
}
