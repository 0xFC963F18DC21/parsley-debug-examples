package examples

import parsley.Parsley
import parsley.Parsley._
import parsley.character._
import parsley.combinator.many
import parsley.expr.chain
import parsley.token.Lexer
import parsley.token.descriptions.{LexicalDesc, NameDesc, SpaceDesc}
import parsley.token.predicate.Basic

import scala.language.implicitConversions

object Concatenative extends Runnable {
  // Here is a simple parser for a Forth-like language, where programs are formed by writing
  // "words" next to each other in reading order. A program can be empty.
  //
  // Words can be formed of any ASCII non-whitespace non-control character, but cannot contain any
  // colons (':'), semicolons (';') or any of the parentheses ('(' and ')').
  //
  // A program is formed of a sequence of words or definitions.
  //
  // Definitions are defined as follows:
  // - A definition starts with a colon.
  // - The colon is then succeeded by a word name.
  // - The body of the word definition then follows, which is a sequence of words.
  // - The definition ends with a semicolon.
  //
  // Comments can be inserted anywhere in a program, beginning with an opening parenthesis ('(') and
  // finishing with a closing parenthesis (')'). They should not be stored in a parsed program, and
  // can be nested.
  //
  // Words, definitions and comments must be whitespace-separated.
  //
  // More formally, the grammar is as follows:
  //
  // <comment> = "(" , ? anything including comments ? , ")" ;
  // <space>   = ? whitespace ASCII characters ? * ;
  // <wchar>   = ? any non-whitespace, non-control ASCII character ?
  //             excluding ( "(" | ")" | ":" | ";" ) ;
  // <word>    = <wchar> + ;
  // <def>     = ":" , <space> , ( <word> , <space> ) + , ";" ;
  // <prog>    = ( <word> , ( <space> , <word> ) * ) ? ;

  // You can use Collectors to also collect the internals of lexers!
  val lexer: Lexer = {
    val language: LexicalDesc = LexicalDesc.plain.copy(
      spaceDesc = SpaceDesc.plain.copy(
        commentStart = "(",
        commentEnd = ")",
        space = Basic(_.isWhitespace)
      ),
      nameDesc = NameDesc.plain.copy(
        identifierLetter = Basic { c =>
          !":;()".contains(c) && (c.toInt < 127) && !c.isWhitespace
        }
      )
    )

    new Lexer(language)
  }

  object Implicits {
    implicit def tokenLift(c: Char): Parsley[Unit] =
      lexer.lexeme.symbol(c)
  }

  import this.Implicits._

  // The program tokens.
  sealed trait Tokens
  case class Word(word: String) extends Tokens
  case class Def(name: Word, words: List[Word]) extends Tokens

  case class Prog(prog: List[Tokens])

  // Here is the broken parser.
  val wordB: Parsley[Word] = lexer.lexeme.names.identifier.map(Word)

  def wordsB(acc: List[Word] = Nil): Parsley[List[Word]] =
    wordB.flatMap(w => wordsB(w :: acc)) <|> pure(acc)

  val defnB: Parsley[Def] = ':' *> (wordB <~> wordsB()).map(Def.tupled) <* ';'

  val tokB: Parsley[Tokens] = attempt(wordB) <|> attempt(defnB)

  def progB(acc: List[Tokens] = Nil): Parsley[Prog] =
    tokB.flatMap(t => progB(t :: acc)) <|> pure(acc).map(Prog)

  // ***** Make your fixed parser below this comment after debugging! *****
  val fixed: Parsley[Prog] = Parsley.empty

  override def run(): Unit = {
    // Input cases.
    val cases: List[String] = List(
      "",
      "foo",
      "foo bar",
      ": foo ( x -- y ) bar ;",
      ": a ( b -- c d ) e f ; g a",
      ": empty ;",
      "x y f bi : drop2 ( x x -- ) drop drop ; drop2"
    )

    // Running the parser itself.
    for (inp <- cases) {
      println(s"BROKEN: $inp -> ${progB().parse(inp)}")
      println(s" FIXED: $inp -> ${fixed.parse(inp)}")
    }
  }
}
