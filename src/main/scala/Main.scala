import model._
import scala.concurrent.Await
import scala.concurrent.duration._
import slick.jdbc.PostgresProfile.api._

object Main {
  val db = Database.forConfig("TODO")
  val usersRepository = new UsersRepository(db)
  val tasksRepository = new TasksRepository(db)
  def main(args: Array[String]): Unit = {
    init()
    fillDb()
    //val menu = new Menu(db)
    //menu.start()
  }

  def init(): Unit = {
    Await.result(db.run(UsersTable.table.schema.create), Duration.Inf)
    Await.result(db.run(TasksTable.table.schema.create), Duration.Inf)
  }
  def fillDb(): Unit = {
    Await.result(usersRepository.create(Users(java.time.LocalDateTime.now().toString, "data", admin = true)),
                 Duration.Inf)
    Thread.sleep(1000)
    fillDb()
  }
}
