package model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

case class Users(name: String, password: String, admin: Boolean)

class UsersTable(tag: Tag) extends Table[Users](tag, "Users") {
  val name = column[String]("name", O.PrimaryKey)
  val password = column[String]("password")
  val admin = column[Boolean]("admin")

  def * = (name, password, admin) <> (Users.apply _ tupled, Users.unapply)
}
