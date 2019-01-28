# Intermediate Scala I

## Basics I: Implicits 



## Implicit type converters

Show in Bor!

## Basics II: Induction

A way of proofing that a property P(n) holds for every natural number n, i.e. for n = 0, 1, 2, 3, etc.

- base case (0)
- induction step (n+1)

## Basics III: Typeclasses

'A typeclass associates functionality with a type'

e.g. cats.Semigroup

A semigroup for some given type A has a single operation (combine), which takes two values of type A, and returns a value of type A. 

```scala
package cats.kernel

trait Semigroup[A] {
  def combine(x: A, y: A): A
}

// plus some more gory looking stuff
```

Since *Semigroup* is a strange name for that let's name it "Combinable" instead.

```scala
trait Combinable[A] {
  def combine(x: A, y: A): A
}

implicit val combinableInt = new Combinable[Int] {
    def combine(x: Int, y: Int): Int = x + y
} 
```

How about Option[Int]? 

"If I have a *CombinableA*, Scala should produce for me a *CombinableOptionA*.

```scala
implict def optionCombinable[A](implicit combinableA: Combinable[A]) = 
    new Combinable[Option[A]] {
        def combine (oa1: Option[A], oa2: Option[A]): Option[A] = for {
            a1 <- oa1
            a2 <- oa2
        } yield combinableA.combine(a1, a2)
    }

```

### The cool part: Typeclass induction

The question on everyone's mind right now: 

Can we leverage **implicit resolution** to do **typeclass induction**?

Yes

1. A custom-made list

```scala

case class Si()
case class Ge()
case class Pb()

type EOL = () // base case

// encode [Int, Char, String] as 
(Ge, (Si, (Pb, EOL))) // (head, tail) becomes induction step
```

Let's call this `(_, (_, (_, EOL))` tuple-list.

2. A typeclass: **Located[A]**

```scala
trait Located[A] { val location: String }

implicit val locatedSi = new Located[Silicon] { val location = "agv" }
implicit val locatedGe = new Located[Germanium] { val location = "plan" }
implicit val locatedPb = new Located[Lead] { val location = "plc" }
```

3. Induction w/ **Located[A]**

Goal: A list of location for _any_ tuple-list

```scala

Located[(Si, (Ge, (Pb, EOL))].located // should print: "agv, plan, plc"

```

How? **Typeclass induction**!

1. base case

```scala
trait Located[A] { val location: Int }

implicit val base: Located[EOL] = new Located[EOL] {
  val location = ""
}
```

2. induction step

```scala
trait Located[A] { val location: Int }

// notice: this is a implicit *method*
// if I have a Located H and a Located T
// produce a Located for the tuple of H and T
implicit def inductionStep[H, T] (implicit locatedHead: Located[H], locatedTail: Located[T]): Located[(H, T)] =
  new Located[(H,T)]  {
    val location = s"${locatedHead.location}, ${locatedTail.location}"
  }

```

And it's done! 

```scala

// (Implicitly checks if an implicit value of type T is available and return it) 
// def implicitly[T](implicit e: T): T = e

implicitly[Located[(Si, (Ge, (Pb, EOL))]].location
// "agv, plan, plc, " 

implicitly[Located[(Si, (Ge, (Pb, (Pb, (Ge, (Si, EOL))))))]].location
// "agv, plan, plc, plc, plan, agv, " 
```

Close enough!

Final question: How can we remove the trailing comma?

TODO add graph on how this works
