import cats.Semigroup
import cats.implicits._

object HelloCats {

  def main(args: Array[String]): Unit = {
//    val m1 = Map("agent1" -> 1, "agent2" -> 2)
//    val m2 = Map("agent1" -> 1, "agent3" -> 3)
//
//    println ("Hello " |+| "World!")
//    println (1 |+| 2)
//    println (m1 |+| m2)


    // demo_>>=()

    // pole.demoNonFP()
    // pole demoFP2()
    // demoFmap()

    // demoAp()
//
//    IOMonad.calcuations(2,3)
//
//    val program = IOMonad.pureCalcuations(2,3)
//
//    // 'end-of-the-world' (where unpure stuff lives)
//    program.unsafeRunSync()

    // typeInduction.run()

    val x = Semigroup[Int => Int].combine(x => x + 1, x => x * 10)// .apply(6)

    val y = Semigroup[Int => Int].combine(_ + 1, _ * 10).apply(1)

    val z = Semigroup[Int => Int].combine(_ + 1, _ + 2).apply(1)


    val f1: Int => Int = x => x + 1
    val f2: Int => Int = x => x * 10
    val f3: Int => Int = f1 andThen f2
    println(f3(6))

    val list = List(1, 2, 3, 4, 5)
    val (left, right) = list.splitAt(2)
    val sumLeft = left.foldLeft(0)(_ |+| _)
    val sumRight = right.foldLeft(0)(_ |+| _)
    val result = sumLeft |+| sumRight

    println(result)
//
//    def printNumber(implicit x: Int): Unit = println(x)
//
//    implicit val x: Int = 10
//
//    printNumber
//    printNumber(x)
//
//    typeInduction.run()
  }

  implicit def optionSemigroup[A](implicit s: Semigroup[A]): Semigroup[Option[A]] =
  new Semigroup[Option[A]] {
    def combine (oa1: Option[A], oa2: Option[A]): Option[A] = for {
      a1 <- oa1
      a2 <- oa2
    } yield s.combine(a1, a2)
  }

  private def demoFmap(): Unit = {

    val plusOne: Int => Int = _ + 1
    val plusTwo: Int => Int = _ + 1

    val o1: Option[Int] = Some(2)
    val l1: List[Int] = List(1,2,3)
    val x = o1 fmap plusOne
    val xl = l1 fmap plusOne
    val plusThree = plusOne andThen plusTwo
    println (x)
    println (xl)
  }

  private def demo_>>=(): Unit = {

    val l1 = List(1,2,3)
    val l2 = List(4,5)

    val res = l1 >>= (x => l2 map (_ * x))

    println(res)
  }

  private def demoApplicative(): Unit = {

    /**
      * construct Foo with optional parameters
      */
    case class Foo(x: Int, y: Int)

    val o1: Option[Int] = Some(2)
    val o2: Option[Int] = Some(3)

    // bad "java-style"
    var foo1: Option[Foo] = None
    if(o1.isDefined && o2.isDefined) {
      foo1 = Some(Foo(o1.get, o2.get))
    }

    // okay scala
    val foo2 = for {
      x <- o1
      y <- o2
    } yield Foo(x,y)

    // ((ΦωΦ))
    val foo3 = (o1 |@| o2) map Foo
    val foo4 = (o1, o2) mapN Foo

    println(foo1)
    println(foo2)
    println(foo3)
    println(foo4)

  }


  private def demoAp(): Unit = {

    val o1: Option[Int] = Some(2)
    val o2: Option[Int => Int] = Some(_ + 2)

    println (o2 <*> o1)

    val l1: List[Int] = List(1,2,3)
    val l2: List[Int => Int] = List(_ + 1, _ + 2)

    println (l2 <*> l1)

  }
}
