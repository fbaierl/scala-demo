import cats.implicits._

object HelloCats {

  def main(args: Array[String]): Unit = {



    val m1 = Map("agent1" -> 1, "agent2" -> 2)
    val m2 = Map("agent1" -> 1, "agent3" -> 3)

    println ("Hello " |+| "World!")
    println (1 |+| 2)
    println (m1 |+| m2)


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

}
