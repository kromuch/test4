package model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

case class Tasks(id: Option[Long], text: String, completed: Boolean, owner:String)

class TasksTable(tag: Tag) extends Table[Tasks](tag, "Tasks") {
  val id = column[Long]("id",O.PrimaryKey,O.AutoInc)
  val text = column[String]("text")
  val completed = column[Boolean]("completed")
  val owner = column[String]("owner")

  val ownerFk = foreignKey("owner_fk",owner,TableQuery[UsersTable])(_.name)

  def * = (id.?,text,completed,owner) <> (Tasks.apply _ tupled, Tasks.unapply)
}
