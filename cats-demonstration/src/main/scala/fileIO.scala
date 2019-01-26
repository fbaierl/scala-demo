import java.io.{File, _}

import cats.effect.{IO, Resource}
import cats.syntax._
import cats.implicits._

object fileIO {

  /*
  We consider opening an stream to be a side-effect action, so we have to encapsulate those actions in their own IO instances.
  For this, we will make use of cats-effect Resource, that allows to orderly create, use and then release resources. See this code:
   */
  def inputStream(f: File): Resource[IO, FileInputStream] =
    Resource.make {
      IO(new FileInputStream(f))                         // build
    } { inStream =>
      IO(inStream.close()).handleErrorWith(_ => IO.unit) // release
    }

  def outputStream(f: File): Resource[IO, FileOutputStream] =
    Resource.make {
      IO(new FileOutputStream(f))                         // build
    } { outStream =>
      IO(outStream.close()).handleErrorWith(_ => IO.unit) // release
    }

  // inputOutputStreams encapsulates both resources in a single Resource instance that will
  // be available once the creation of both streams has been successful, and only in that case.
  def inputOutputStreams(in: File, out: File): Resource[IO, (InputStream, OutputStream)] =
    // Resource instances can be combined in for-comprehensions as they implement flatMap
    for {
      inStream  <- inputStream(in)
      outStream <- outputStream(out)
    } yield (inStream, outStream)

  def transmit(origin: InputStream, destination: OutputStream, buffer: Array[Byte], acc: Long): IO[Long] =
    for {
      amount <- IO(origin.read(buffer, 0, buffer.size))
      count  <- if(amount > -1) IO(destination.write(buffer, 0, amount)) >> transmit(origin, destination, buffer, acc + amount)
                else IO.pure(acc) // End of read stream reached (by java.io.InputStream contract), nothing to write
    } yield count // Returns the actual amount of bytes transmitted

  // define a loop that at each iteration reads data from the input stream into a buffer,
  // and then writes the buffer contents into the output stream.
  def transfer(origin: InputStream, destination: OutputStream): IO[Long] =
    for {
      buffer <- IO(new Array[Byte](1024 * 10)) // Allocated only when the IO is evaluated
      total  <- transmit(origin, destination, buffer, 0L)
    } yield total

  // copies a file from origin to destination
  def copy(origin: File, destination: File): IO[Long] =
    inputOutputStreams(origin, destination).use { case (in, out) =>
      transfer(in, out)
    }



}
