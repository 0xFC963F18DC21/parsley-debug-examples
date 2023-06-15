package examples

import parsley.{Failure, Parsley, Success}
import parsley.Parsley._
import parsley.character.char
import parsley.combinator.{choice, ifP, many}
import parsley.errors.combinator.fail
import parsley.registers._

object Contextful extends Runnable {
  // Contextful / context-sensitive grammars are a rather big pain to write parsers for in general.
  // Let us debug a simple context-sensitive grammar.
  //
  // This grammar accomplishes nothing useful at all, and is composed of nothing but paired
  // delimiters:
  // - Parentheses    ( )
  // - Brackets       [ ]
  // - Braces         { }
  // - Angle Brackets < >
  //
  // In this grammar, an input should correctly parse iff all of the individual delimiter types
  // have matching pairs.
  //
  // For example:
  // - These parse: "()", "[]", "<>", "[]"
  // - These also parse: "(())", "([])", "<[{(())}]>"
  // - These do not parse: "(", "()]", "{)"
  //
  // Now, unlike most grammars with multiple paired delimiters, we will allow the interweaving of
  // different delimiters. This means sequences such as "([)]" should also parse successfully.
  // However, we still can not imbalance the delimiters negatively (more closing delimiters of a
  // pair than opening delimiters) at any point in time.
  //
  // Another feature we will borrow is the concept of "super-brackets", where we have a symbol that
  // can close an arbitrary amount of delimiters. We will use "|" as our super-bracket, which in
  // this grammar will close all of the most recent delimiter opened.
  //
  // For example:
  // - These parse: "((((((((|", "([(|]"
  // - These do not parse: "|", "()|", "|)", "[[(|)"

  // Here is a broken version:
  object Broken {
    def removeFirst[A, B >: A](xs: List[A], pred: B => Boolean): Option[List[A]] = xs match {
      case Nil                => None
      case y :: ys if pred(y) => Some(ys)
      case y :: ys            => removeFirst(ys, pred).map(y :: _)
    }

    val parMatcher: Parsley[Unit] = List[Char]().makeReg { reg =>
      many(
        choice(
          char('(') *> reg.modify('(' :: _),
          char('[') *> reg.modify('[' :: _),
          char('{') *> reg.modify('{' :: _),
          char('<') *> reg.modify('<' :: _),
          char(')') *> reg.get.flatMap(l =>
            removeFirst(l, (d: Char) => d == '(') match {
              case Some(xs) => reg.put(xs)
              case None     => fail("No more opening parentheses.")
            }
          ),
          char(']') *> reg.get.flatMap(l =>
            removeFirst(l, (d: Char) => d == '[') match {
              case Some(xs) => reg.put(xs)
              case None     => fail("No more opening brackets.")
            }
          ),
          char('}') *> reg.get.flatMap(l =>
            removeFirst(l, (d: Char) => d == '{') match {
              case Some(xs) => reg.put(xs)
              case None     => fail("No more opening braces.")
            }
          ),
          char('>') *> reg.get.flatMap(l =>
            removeFirst(l, (d: Char) => d == '<') match {
              case Some(xs) => reg.put(xs)
              case None     => fail("No more opening angle brackets.")
            }
          ),
          char('|') *> reg.get.flatMap {
            case Nil     => fail("This super-bracket closes nothing at all.")
            case x :: xs => reg.put(xs.filter(_ != x))
          }
        )
      ) *> ifP(reg.get.map(_.isEmpty), pure(()), fail("Unmatched delimiters in input."))
    }
  }

  // ***** Make your fixed parser below this comment after debugging! *****
  object Fixed {
    val parMatcher: Parsley[Unit] = Parsley.empty
  }

  override def run(): Unit = {
    // As this is harder, we will have tests to pass and tests to fail:
    val passingCases: List[String] = List(
      "()",
      "[]",
      "<>",
      "[]",
      "([)]",
      "(())",
      "([])",
      "<[{(())}]>",
      "((((((((|",
      "([(|]"
    )

    val failingCases: List[String] = List(
      "(",
      "()]",
      "{)",
      "|",
      "()|",
      "|)",
      "[[(|)"
    )

    for (inp <- passingCases) {
      println(s"BROKEN (should pass): $inp -> ${Broken.parMatcher.parse(inp)}")
      println(s" FIXED (should pass): $inp -> ${Fixed.parMatcher.parse(inp)}")
    }

    for (inp <- failingCases) {
      println(s"BROKEN (should fail): $inp -> ${Broken.parMatcher.parse(inp)}")
      println(s" FIXED (should fail): $inp -> ${Fixed.parMatcher.parse(inp)}")
    }
  }
}
