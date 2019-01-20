/**
  * http://eed3si9n.com/herding-cats/Monad.html
  *
  * Example to better understand Monads.
  *
  * Let’s say that [Pierre] keeps his balance if the number of birds on the left side of the pole and on the right side of the pole is within three.
  * So if there’s one bird on the right side and four birds on the left side, he’s okay.
  * But if a fifth bird lands on the left side, then he loses his balance and takes a dive.
  */
object pole {

  import cats._
  import cats.data._
  import cats.implicits._

  type Birds = Int

  case class PierreFellException(right: Birds, left: Birds) extends Exception {
    override def getMessage: String =
      s"""
         |Oh noes! Pierre couldn't keep his balance and fell of the line.
         |Birds on the left: $left
         |Birds on the right: $right
       """.stripMargin
  }

  case class NonFPPole(left: Birds, right: Birds) {
    def landLeft(n: Birds): NonFPPole =
      if (math.abs((left + n) - right) < 4) copy(left = left + n)
      else throw PierreFellException(left + n, right)
    def landRight(n: Birds): NonFPPole =
      if (math.abs(left - (right + n)) < 4) copy(right = right + n)
      else throw PierreFellException(left, right + n)
  }

  case class Pole(left: Birds, right: Birds){
    def landLeft(n: Birds): Option[Pole] =
      if (math.abs((left + n) - right) < 4) copy(left = left + n).some
      else none[Pole]
    def landRight(n: Birds): Option[Pole] =
      if (math.abs(left - (right + n)) < 4) copy(right = right + n).some
      else none[Pole]
  }

  case class ReportingPole(left: Birds, right: Birds){
    def landLeft(n: Birds): PierreFellException Either ReportingPole =
      if (math.abs((left + n) - right) < 4) copy(left = left + n).asRight
      else PierreFellException(left + n, right).asLeft
    def landRight(n: Birds): PierreFellException Either ReportingPole =
      if (math.abs(left - (right + n)) < 4) copy(right = right + n).asRight
      else PierreFellException(left, right + n).asLeft
  }

  def demoNonFP(): Unit = {
    val rlr = NonFPPole(0,0).landRight(2).landLeft(2).landRight(2)
    println (rlr)
    val rrl = NonFPPole(0,0).landRight(2).landRight(2).landLeft(2)
    println (rrl)
  }

  def demoFP(): Unit = {
    // pure uplifts Pole to an applicative functor
    //
    // applicative functor is a structure intermediate between functors and monads,
    // in that they allow sequencing of functorial computations (functor is a map between categories)
    val rlr = Monad[Option].pure(Pole(0, 0)) >>= {_.landRight(2)} >>= {_.landLeft(2)} >>= {_.landRight(2)}
    println (rlr)
    val rrl = Monad[Option].pure(Pole(0,0)) >>= { _.landRight(2) } >>= { _.landRight(2) } >>= {_.landLeft(2)}
    println (rrl)

    val lll = Option(Pole(0,0)) flatMap {_.landLeft(2)} flatMap {_.landLeft(2)} flatMap {_.landLeft(2)}
    println (lll)
  }

  def demoFP2(): Unit = {
    val lll = ReportingPole(0, 0).asRight flatMap (_.landLeft(2)) flatMap (_.landLeft(2)) flatMap (_.landLeft(2))
    println (lll)
  }

}

