import org.scalatest.{FlatSpec, Matchers, _}

class CatsIoTest extends FlatSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {

  override def beforeAll(): Unit = {
    println("Setup:beforeAll Create Fixtures")
  }

  override def afterAll(): Unit = {
    println("Setup:afterAll")
  }

  override def beforeEach {
    println("Setup:beforeEach")
  }

  override def afterEach {
    println("Teardown:afterEach")
  }

  "A CATS IO Resource object will be created that" should "copy a file" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(true)
    println("CATS IO Resource...")

    import java.io._

    import cats.effect._
    import cats.effect.concurrent.Semaphore
    import cats.syntax.all._

    import scala.concurrent.ExecutionContext
    implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    def inputStream(f: File, guard: Semaphore[IO]): Resource[IO, FileInputStream] =
      Resource.make {
        IO(new FileInputStream(f))
      } { inStream =>
        guard.withPermit {
          IO(inStream.close()).handleErrorWith(_ => IO.unit)
        }
      }

    def outputStream(f: File, guard: Semaphore[IO]): Resource[IO, FileOutputStream] =
      Resource.make {
        IO(new FileOutputStream(f))
      } { outStream =>
        guard.withPermit {
          IO(outStream.close()).handleErrorWith(_ => IO.unit)
        }
      }

    def inputOutputStreams(in: File, out: File, guard: Semaphore[IO]): Resource[IO, (InputStream, OutputStream)] =
      for {
        inStream  <- inputStream(in, guard)
        outStream <- outputStream(out, guard)
      } yield (inStream, outStream)

    def copy(origin: File, destination: File)(implicit concurrent: Concurrent[IO]): IO[Long] = {
      for {
        guard <- Semaphore[IO](1)
        count <- inputOutputStreams(origin, destination, guard).use { case (in, out) =>
          guard.withPermit(transfer(in, out))
        }
      } yield count
    }

    def transmit(origin: InputStream, destination: OutputStream, buffer: Array[Byte], acc: Long): IO[Long] =
      for {
        amount <- IO(origin.read(buffer, 0, buffer.size))
        _ = println(s"$amount characters read...")
        count <- if (amount > -1) IO(destination.write(buffer, 0, amount)) >> transmit(origin, destination, buffer, acc + amount)
                  else IO.pure(acc) // End of read stream reached (by java.io.InputStream contract), nothing to write
        _ = println(s"$count characters written...")
      } yield count // Returns the actual amount of bytes transmitted

    def transfer(origin: InputStream, destination: OutputStream): IO[Long] =
      for {
        buffer <- IO(new Array[Byte](1024 * 10)) // Allocated only when the IO is evaluated
        total  <- transmit(origin, destination, buffer, 0L)
        _ = println(s"$total characters transferred...")
      } yield total

    val source = new File("C:/Users/whitej/Documents/BitBucket/distanceapi/src/main/resources/api.yaml")
    val destination = new File("C:/Users/whitej/Documents/BitBucket/distanceapi/src/test/resources/copied.yaml")
    val ioLength = copy(source, destination)
    val length = ioLength.unsafeRunSync()
    println(s"Number of characters copied : $length")
    assert(source.length() == length, s"Length copied ($length) does not equal file size (${source.length()})")
  }

}