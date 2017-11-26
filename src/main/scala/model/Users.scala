package model

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

case class Users(name: String, password: String, admin: Boolean)

class UsersTable(tag: Tag) extends Table[Users](tag, "Users") {
  val name = column[String]("name", O.PrimaryKey)
  val password = column[String]("password")
  val admin = column[Boolean]("admin")

  def * = (name, password, admin) <> (Users.apply _ tupled, Users.unapply)
}

object UsersTable {
  val table = TableQuery[UsersTable]
}

class UsersRepository(db: Database) {
  val usersTableQuery = TableQuery[UsersTable]

  def create(user: Users): Future[Users] =
    db.run(usersTableQuery returning usersTableQuery += user)

  def delete(username: String): Future[Int] = {
    val tasksTableQuery = TableQuery[TasksTable]
    Await.result(db.run(tasksTableQuery.filter(_.owner === username).delete),
                 Duration.Inf)
    db.run(usersTableQuery.filter(_.name === username).delete)
  }
}
