package actions

import model._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Await
import scala.concurrent.duration._

class UserActions(currentUser: String, db: Database) {
  val usersTableQuery = TableQuery[UsersTable]

  def showAll(): Unit = {
    Await
      .result(db.run(usersTableQuery.result), Duration.Inf)
      .map(a => a.name)
      .foreach(println)
  }

  def showAdmins(): Unit = {
    Await
      .result(db.run(usersTableQuery.filter(_.admin === true).result),
              Duration.Inf)
      .map(a => a.name)
      .foreach(println)
  }

  def showUsers(): Unit = {
    Await
      .result(db.run(usersTableQuery.filter(_.admin === false).result),
              Duration.Inf)
      .map(a => a.name)
      .foreach(println)
  }

  def createAdmin(): Unit = {
    val usersRepository = new UsersRepository(db)
    val findAdmin: Option[Users] = Await.result(
      db.run(usersTableQuery.filter(_.name === currentUser).result.headOption),
      Duration.Inf)
    val defaultAdminValue = Users("","",admin = false)
    if (findAdmin.isEmpty)
      println("Поточний користувач не знайдений (???)")
    else if (findAdmin.getOrElse(defaultAdminValue).admin) {
      println("Введіть ім'я нового адміністратора:")
      val name = io.StdIn.readLine()
      println("Введіть пароль нового адміністратора:")
      val pass = io.StdIn.readLine()
      val findAllNamed = Await.result(
        db.run(usersTableQuery.filter(_.name === name).result),
        Duration.Inf)
      val ifNotUsed = findAllNamed.isEmpty
      if (ifNotUsed)
        Await.result(usersRepository.create(Users(name, pass, admin = true)),
                     Duration.Inf)
      else
        println("Користувач з таким іменем вже існує")
    } else
      println("Поточний користувач не може створювати користувачів")
  }

  def createUser(): Unit = {
    val usersRepository = new UsersRepository(db)
    val findAdmin: Option[Users] = Await.result(
      db.run(usersTableQuery.filter(_.name === currentUser).result.headOption),
      Duration.Inf)
    val defaultAdminValue = Users("","",admin = false)
    if (findAdmin.isEmpty)
      println("Поточний користувач не знайдений (???)")
    else if (findAdmin.getOrElse(defaultAdminValue).admin) {
      println("Введіть ім'я нового користувача:")
      val name = io.StdIn.readLine()
      println("Введіть пароль нового користувача:")
      val pass = io.StdIn.readLine()
      val findAllNamed = Await.result(
        db.run(usersTableQuery.filter(_.name === name).result),
        Duration.Inf)
      val ifNotUsed = findAllNamed.isEmpty
      if (ifNotUsed)
        Await.result(usersRepository.create(Users(name, pass, admin = false)),
                     Duration.Inf)
      else
        println("Користувач з таким іменем вже існує")
    } else
      println("Поточний користувач не може створювати користувачів")
  }

  def deleteUser(): Unit = {
    val usersRepository = new UsersRepository(db)
    val findAdmin: Option[Users] = Await.result(
      db.run(usersTableQuery.filter(_.name === currentUser).result.headOption),
      Duration.Inf)
    val defaultAdminValue = Users("","",admin = false)
    if (findAdmin.isEmpty)
      println("Поточний користувач не знайдений (???)")
    else if (findAdmin.getOrElse(defaultAdminValue).admin) {
      println("Введіть ім'я користувача для видалення:")
      val name = io.StdIn.readLine()
      if (name == currentUser)
        println("Не можна видаляти поточного користувача")
      else {
        val findAllNamed = Await.result(
          db.run(usersTableQuery.filter(_.name === name).result),
          Duration.Inf)
        val ifNotUsed = findAllNamed.isEmpty
        if (ifNotUsed)
          println("Користувача з таким іменем не існує")
        else
          Await.result(usersRepository.delete(name), Duration.Inf)
      }
    } else
      println("Поточний користувач не може видаляти користувачів")
  }
}
