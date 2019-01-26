object typeInduction {

  trait Located[A] { val location: String }

  type EOL = Unit

  case class Si()
  case class Ge()
  case class Pb()

  implicit val locatedSi: Located[Si] = new Located[Si] { val location = "agv" }
  implicit val locatedGe: Located[Ge] = new Located[Ge] { val location = "plan" }
  implicit val locatedPb: Located[Pb] = new Located[Pb] { val location = "plc" }

  // base
  implicit val base: Located[EOL] = new Located[EOL] {
    val location = ""
  }

  // notice: this is a implicit *method*
  implicit def inductionStep[H, T] (implicit locatedHead: Located[H], locatedTail: Located[T]): Located[(H, T)] =
    new Located[(H,T)]  {
      val location = s"${locatedHead.location}, ${locatedTail.location}"
    }

  def run(): Unit = {
    println(implicitly[Located[(Si, (Ge, (Pb, EOL)))]].location)
    println(implicitly[Located[(Si, (Ge, (Pb, (Pb, (Ge, (Si, EOL))))))]].location)
  }
}
