import cats.effect.IO

object IOMonad {


  val f: Int => Int => Int = x => y => y * x
  val g: Int => Int = x => x + 1

  def calcuations(x: Int, y: Int): Int = {

    println ("Let's calculate stuff!")

    val Δ = (f(x) andThen g) (y)

    println ("Got a result: " + Δ)

    Δ
  }


  def pureCalcuations(x: Int, y: Int): IO[Int] = {
    for {

      _           <- IO { println ("Let's calculate stuff!") }

      result = (f(x) andThen g) (y)

      _           <- IO { println ("The result is: " + result) }

    } yield result
  }
}
