import actions._
import model._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.jdbc.PostgresProfile.api._


class Menu(db: Database) {

  val tasksTableQuery = TableQuery[TasksTable]
  val usersTableQuery = TableQuery[UsersTable]

  def start(): Unit = {
    def legend(): Unit = println("""|Оберіть, будь ласка, дію:
                                    |  1. Login
                                    |  2. Вихід""".stripMargin)
    legend()
    Iterator
      .continually(io.StdIn.readLine)
      .takeWhile(_ !=
        "2")
      .foreach {
        case "1" =>
          login()
          legend()
        case _ =>
          println("Невідома команда")
          legend()
      }
  }

  def login(): Unit = {
    println("Введіть логін:")
    val name = io.StdIn.readLine()
    println("Введіть пароль:")
    val pass = io.StdIn.readLine()
    //Знайдемо користувача з таким логіном і таким паролем, якщо не порожній результат - вуаля!
    val findUser = Await.result(db.run(
                                  usersTableQuery
                                    .filter(_.name === name)
                                    .filter(_.password === pass)
                                    .result
                                    .headOption),
                                Duration.Inf)
    if (findUser.isEmpty) {
      println("Неправильний логін та/або пароль")
    } else {
      println("Ви успішно увійшли!")
      actions(name)
    }
  }

  def actions(currentUser: String): Unit = {
    def legend(): Unit =
      println(("""|Оберіть, будь ласка, область взаємодії:
                                      |  1 - Список справ
                                      |  2 - Користувачі
                                      |  3 - Вихід з профілю """ + currentUser).stripMargin)
    legend()
    Iterator
      .continually(io.StdIn.readLine)
      .takeWhile(_ !=
        "3")
      .foreach {
        case "1" =>
          tasksActions(currentUser)
          legend()
        case "2" =>
          usersActions(currentUser)
          legend()
        case _ =>
          println("Невідома команда")
          legend()
      }
  }

  def tasksActions(currentUser: String): Unit = {
    val tAction = new TaskActions(currentUser, db)
    def legend(): Unit =
      println("""|Оберіть, будь ласка, дію над справами:
                  |  1 - Вивести всі
                  |  2 - Вивести виконані
                  |  3 - Вивести не виконані
                  |  4 - Додати справу
                  |  5 - Змінити статус за ID
                  |  6 - Видалити за ID
                  |  7 - Попереднє меню""".stripMargin)
    legend()
    Iterator
      .continually(io.StdIn.readLine)
      .takeWhile(_ !=
        "7")
      .foreach {
        case "1" =>
          tAction.showAll()
          legend()
        case "2" =>
          tAction.showCompleted()
          legend()
        case "3" =>
          tAction.showUncompleted()
          legend()
        case "4" =>
          tAction.createTask()
          legend()
        case "5" =>
          tAction.toggleStatus()
          legend()
        case "6" =>
          tAction.deleteTask()
          legend()
        case _ =>
          println("Невідома команда")
          legend()
      }
  }

  def usersActions(currentUser: String): Unit = {
    val uAction = new UserActions(currentUser, db)
    def legend(): Unit =
      println("""|Оберіть, будь ласка, дію над користувачами:
                 |  1 - Показати всіх
                 |  2 - Показати адміністраторів
                 |  3 - Показати не адміністраторів
                 |  4 - Додати користувача (лише для адміністраторів)
                 |  5 - Додати адміністратора (лише для адміністраторів)
                 |  6 - Видалити за іменем (лише для адміністраторів)
                 |  7 - Попереднє меню""".stripMargin)
    legend()
    Iterator
      .continually(io.StdIn.readLine)
      .takeWhile(_ !=
        "7")
      .foreach {
        case "1" =>
          uAction.showAll()
          legend()
        case "2" =>
          uAction.showAdmins()
          legend()
        case "3" =>
          uAction.showUsers()
          legend()
        case "4" =>
          uAction.createUser()
          legend()
        case "5" =>
          uAction.createAdmin()
          legend()
        case "6" =>
          uAction.deleteUser()
          legend()
        case _ =>
          println("Невідома команда")
          legend()
      }
  }
}
