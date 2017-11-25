import model._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
object Main {
  val db = Database.forURL(
    s"jdbc:postgres://gelgoteifnrxgi:6dad6061aee8baf4517d6a4222faa1aa9c6cb22bdbbd463059ea99fb9ed58bb3@ec2-176-34-110-252.eu-west-1.compute.amazonaws.com:5432/d4pnv1e7erhus5"
  )
}
