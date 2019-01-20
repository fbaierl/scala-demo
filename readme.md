# INTERESTING LIBRARIES

- [cats](https://github.com/typelevel/cats) [docu](https://typelevel.org/cats/)
- mouse https://github.com/typelevel/mouse

https://www.youtube.com/watch?v=4QIgEMvUfIE

# THE GOOD 

## functions

### currying

```scala
def add(x: Int)(y: Int): Int = x + y
def multiply(x: Int)(y: int): Int = x * y
// two functions for function compositions
// right to left
val add10Multiply10Compose: Int => Int = multiply(10) _ compose add(10)
// left to right 
val add10Multiply10 = add(10) _ andThen multiply(10)
```

- currying: each function a single argument; enables partial application
- partial application: provide a subset of the parameters to get a new function
- function composition: glueing multiple functions together

A real world example:

```scala
type WebRequest = HttpRequest => HttpResponse
val deSerialisePerson: HttpRequest => Person = ???
val createCustomer: Person => Customer = ???
val saveCustomer: Customer => Customer = ???
val serialiseCustomer: Customer => HttpResponse =  ???

val registerCustomer: WebRequest = 
  deSerialisePerson andThen
  createCustomer andThen
  saveCustomer andThen
  serialiseCustomer
  
type WebRequest = HttpRequest => HttpResponse   
```
But: `saveCustomer`needs a `DatabaseConnection`, how do we fit that in there?

Partial application in bootstrap process of app.

```scala
def saveCustomer(db: DatabaseConnection)(c: Customer) = ???
val saveCustomer: Customer => Customer = saveCustomer(dbConnection)
```
Or use classes, or implicit parameters!

### total functions

A total function is a function that is defined for all possible values of its input. That is, it terminates and returns a value.

&rarr; Use containers (Option, Either) as return value

### pure functions

- return value is the same for the same arguments 
- evaluation has no side effects

### parametric functions

## sum types

```scala
sealed trait Pet
case class Cat(name: String) extends Pet
case class Fish(name: String, color: Color) extends Pet
case class Squid(name: String, age: Int) extends Pet

// destructured by pattern matching
def sayHi(p: Pet): String = 
  p match {
    case Cat(n)      => "Meow " + n + "!"
    case Fish(n, _)  => "Hello fishy " + n + "."    
    case Squid(n, _) => "Hi " + n + "."    
  }

// destructuring of lists
def sum(ns: List[Int]): Int =
  ns match {
    case Nil     => 0
    case n :: ns => n + sum(ns)
  }
  
```
#### exhaustiveness checking
```scala
def sayHi(p: Pet): String = 
  p match {
    case Cat(n)      => "Meow " + n + "!"
    case Fish(n, _)  => "Hello fishy " + n + "."    
  } 
```

will result in;
```
<console>:14: warning: match may not be exhaustive.
It would fail on the following input: Squid(_, _)
         p match {
         ^
```
## typeclasses

Type classes are a powerful and flexible concept that adds **ad-hoc polymorphism** to Scala. Type class is a class (group) of types, which **satisfy some contract defined in a trait** with addition that such functionality (trait and implementation) **can be added without any changes to the original code**. 

E.g. functors, monoid, monad

Type classes are governed by **laws**.

### semigroup

general structure to define things that can be _combined_

```scala
trait SemiGroup[A] {
  def combine(x:A, y:A): A
}
```

## monoid

General structure to define things that can be _combined_ and has a _default_ element

```scala
trait Monoid[A] extends Semigroup[A] {
  def empty: A
  def combine(x:A, y:A): A
}
```
Example:

```scala
> import cats._, data._, implicits._
> Monoid[String].combine("hi","there")
// "hithere"
> "hi" |+| "there"
```

## functor

general structure to represent something that can be mapped over (Lists, Options, Eithers, Futures)

```scala
trait Functor[F[_]] {
  def map[A,B](fa: F[A)(f: A => B): F[B]
}
```
Example:

```scala
> Functor[List].lift((x:Int) => x + 1)
// res0: List[Int] => List[Int]
> res0(List(1,2))
// res1: List[Int] = List(2,3)
```

## monads

Monads are meant for _sequencing computations_

### applicatives

```scala
/**
  * construct Foo with optional parameters
  */
case class Foo(x: Int, y: Int)

val o1: Option[Int] = Some(2)
val o2: Option[Int] = Some(3)

// bad "java-style"
// - var 
// - need to look inside Monad manually (get) - prone to failure
// - ugly & complicated
var foo1: Option[Foo] = None
if(o1.isDefined && o2.isDefined) {
  foo1 = Some(Foo(o1.get, o2.get))
}

// okay scala
// for - comprehension; essentially strips away the monad to work on inner value
// chain needs to be same kind of monad
// for default case, Foo is default also
val foo2 = for {
  x <- o1
  y <- o2
} yield Foo(x,y)

// ((ΦωΦ))
val foo3 = (o1 |@| o2) map Foo
val foo4 = (o1, o2) mapN Foo
```

## semigroupal

https://github.com/typelevel/cats/blob/master/core/src/main/scala/cats/Semigroupal.scala

## immutability by default

## partially applied functions

## getters / setters

## pattern matching on a tuple function result 

## implicits

https://stackoverflow.com/questions/5598085/where-does-scala-look-for-implicits

### implicit type converters
### implicit parameters

## imperobabilty w/ Clojure

# THE "BAD"

## syntactic diabetes
https://www.slideshare.net/Bozho/scala-the-good-the-bad-and-the-very-ugly

```
opt.foreach
```

## flat-out cryptic methods

### list functions
### ... and the horror doesnt stop there 
(Set, List, Seq, ListLike, etc.)

## Now this is just getting weird

Task: create a `Set` with the values of a `List` 

```
scala> List(1,2,3).toSet()
> res0: Boolean = false
```

calling `toSet()` should be a compile-time error, since `toSet` does not take arguments.
`def toSet[B >: A]: immutable.Set[B] = to[immutable.Set].asInstanceOf[immutable.Set[B]]`

The compiler knows there is no such thing as an empty `apply` method on `Set`, so it assumes you wanted to write:

It's a feature: "adapting argument lists"!

```
scala> List(1,2,3).toSet.apply(())
```
```
/** Tests if some element is contained in this set.
 *
 *  This method is equivalent to `contains`. It allows sets to be interpreted as predicates.
 *  @param elem the element to test for membership.
 *  @return  `true` if `elem` is contained in this set, `false` otherwise.
 */
  def apply(elem: A): Boolean = this contains elem
```

But why isn't this a compile error? 
`toSet[B >: A]: Set[B]`

Compiler finds a common supertype of `Unit` and `Int`: `AnyVal`!

```
scala> List(1,2,3).toSet[AnyVal].apply(())
```

How to actually do it:

```
scala> List(1,2,3).toSet
res1: scala.collection.immutable.Set[Int] = Set(1, 2, 3)
```

# Questions 

```scala
def ? = ???
```

== 

```java
public void imagineAQuestionMarkHere() {
  throw new NotImplementedException();
}
```