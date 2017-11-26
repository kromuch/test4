package actions

import model._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Await
import scala.concurrent.duration._

class TaskActions(currentUser: String, db: Database) {
  val tasksTableQuery = TableQuery[TasksTable]

  def parseList(task: Tasks): String = {
    val completed = if (task.completed) "так" else "ні"
    "ID: " + task.id.getOrElse(0) + ", Власник: " + task.owner +
      ", Виконано: " + completed + ", Текст справи: " + task.text
  }

  def showAll(): Unit = {
    Await
      .result(db.run(tasksTableQuery.filter(_.owner === currentUser).result),
              Duration.Inf)
      .map(a => parseList(a))
      .foreach(println)
  }

  def showCompleted(): Unit = {
    Await
      .result(db.run(
                tasksTableQuery
                  .filter(_.owner === currentUser)
                  .filter(_.completed === true)
                  .result),
              Duration.Inf)
      .map(a => parseList(a))
      .foreach(println)
  }

  def showUncompleted(): Unit = {
    Await
      .result(db.run(
                tasksTableQuery
                  .filter(_.owner === currentUser)
                  .filter(_.completed === false)
                  .result),
              Duration.Inf)
      .map(a => parseList(a))
      .foreach(println)
  }

  def createTask(): Unit = {
    println("Введіть текст завдання:")
    val text = io.StdIn.readLine()
    val tasksRepository = new TasksRepository(db)
    Await.result(
      tasksRepository.create(Tasks(None, text, completed = false, currentUser)),
      Duration.Inf)
  }

  def toggleStatus(): Unit = {
    println("Введіть ID завдання для зміни статусу виконання:")
    val id = io.StdIn.readLong()
    val tasksRepository = new TasksRepository(db)
    tasksRepository.toggleChecked(id, currentUser)
  }

  def deleteTask(): Unit = {
    println("Введіть ID завдання для видалення:")
    val id = io.StdIn.readLong()
    val tasksRepository = new TasksRepository(db)
    Await.result(tasksRepository.delete(id, currentUser), Duration.Inf)
  }
}
