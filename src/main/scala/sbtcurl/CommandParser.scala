package sbtcurl

import scala.util.parsing.combinator.RegexParsers

object CommandParser extends RegexParsers {
  lazy val nonblankLiteral: Parser[String] = """\S+""".r
  lazy val singleQuoted: Parser[String] = "'" ~> "[^']*".r <~ "'"
  lazy val doubleQuoted: Parser[String] = "\"" ~> "[^\"]*".r <~ "\""
  lazy val token = singleQuoted | doubleQuoted | nonblankLiteral
  lazy val command: CommandParser.Parser[List[String]] = token.+

  def parse(cmd: String): Either[String, List[String]] = parseAll(command, cmd) match {
    case Success(res, _) => Right(res)
    case NoSuccess(msg, _) => Left(msg)
  }

}
