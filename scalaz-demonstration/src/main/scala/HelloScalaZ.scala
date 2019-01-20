import scalaz._
import scalaz.Applicative._

import scala.util.Try


object HelloScalaZ {


  def mayFail: \/[String, Int] = \/-(1)
  def classicFail: Either[String, Int] = Right(2)

  def main(args: Array[String]): Unit = {


    case class Foo(x: Int, y: Int)


    val o1: Option[Int] = Some(2)
    val o2: Option[Int] = Some(3)

    (o1 |@| o2) (Foo)


    val t = Try("2".toInt).toEither


    println ( )

    mayFail match {
      case -\/(a) =>
      case \/-(b) =>
      case _      =>
    }

    val r =
      for {
        x <- mayFail
      } yield x

    println(mayFail map (_ + 3))

  }





}
