import de.dheinrich.sandbox.macroparser._

object Parser {

  //import CMacroParser._

  //parse(rep(cline | conditional), example)

  import ExpressionParser._
  val formula = parse(parser, "5*3*2 - 4*3").get


  val simple = Expression simplyfy formula

  val tests = Seq(
    ("+3", 3),
    ("-5", -5),
    ("!5", 0),
    ("!0", 1),
    ("~13", ~13),

    ("3 * 5", 15),
    ("23 / 4", 5),
    ("23 % 5", 3),
    ("23 + 5", 28),
    ("23 - 5", 18),
    ("1 << 3", 8),
    ("16 >> 2", 4),
    ("23 > 5", 1),
    ("23 > 24", 0),
    ("23 > 23", 0),
    ("23 < 5", 0),
    ("23 < 24", 1),
    ("23 < 23", 0),

    ("23 >= 5", 1),
    ("23 >= 24", 0),
    ("23 >= 5", 1),

    ("23 <= 5", 0),
    ("23 <= 24", 1),
    ("23 <= 23", 1),

    ("23 == 24", 0),
    ("23 == 23", 1),

    ("23 != 24", 1),
    ("23 != 23", 0),

    ("23 & 5", 23 & 5),
    ("23 ^ 5", 23 ^ 5),
    ("23 | 5", 23 | 5),
    ("23 || 0", 1),
    ("0|| 0", 0),
    ("23 && 5", 1),
    ("23 && 0", 0),
    ("2 + 3 * 4", 14),
    ("(2 + 3) * 4", 20),
    ("2 + - 5", -3),
    ("((((2))))", 2)
  )






  val errors = for ((s, i) <- tests) yield {
    val r = parse(parser, s)
    if (r.successful) {
      val result = r.get.value
      if (result != i)
        println( s"""The parse result for "$s" is $result and not $i""")
    }
  }


  println(s"from ${tests.size} tests ${errors.size} failed!")

  println("end")
}




