package model

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

case class Tasks(id: Option[Long],
                 text: String,
                 completed: Boolean,
                 owner: String)

class TasksTable(tag: Tag) extends Table[Tasks](tag, "Tasks") {
  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val text = column[String]("text")
  val completed = column[Boolean]("completed")
  val owner = column[String]("owner")

  val ownerFk = foreignKey("owner_fk", owner, TableQuery[UsersTable])(_.name)

  def * =
    (id.?, text, completed, owner) <> (Tasks.apply _ tupled, Tasks.unapply)
}

object TasksTable {
  val table = TableQuery[TasksTable]
}

class TasksRepository(db: Database) {
  val tasksTableQuery = TableQuery[TasksTable]

  def create(task: Tasks): Future[Tasks] =
    db.run(tasksTableQuery returning tasksTableQuery += task)

  def toggleChecked(id: Long, currentUser: String): Unit = {
    val findById = Await.result(db.run(
                                  tasksTableQuery
                                    .filter(_.id === id)
                                    .filter(_.owner === currentUser)
                                    .result
                                    .headOption),
                                Duration.Inf)
    if (findById.isEmpty)
      println("Не знайдено справу з таким ID")
    else {
      val defaultTaskValue = Tasks(None,"Default",completed = false,currentUser)
      val oldValue = findById.getOrElse(defaultTaskValue)
      val newTask =
        Tasks(oldValue.id, oldValue.text, !oldValue.completed, oldValue.owner)
      Await.result(db.run(tasksTableQuery.filter(_.id === id).update(newTask)),
                   Duration.Inf)
    }
  }

  def delete(id: Long, currentUser: String): Future[Int] = {
    db.run(
      tasksTableQuery
        .filter(_.id === id)
        .filter(_.owner === currentUser)
        .delete)
  }
}
