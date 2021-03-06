package views

import models.Todo
import play.api.libs.json.Json
import play.api.mvc.Request

object TodoView {
  implicit val todoWriter = Json.writes[TodoView]
  implicit val todoReader = Json.reads[TodoView]

  def fromModel(todo: Todo)(implicit request: Request[_]): TodoView = new TodoView(
    //title = todo.title,
    title = wrap(todo.title),
    order = todo.order,
    completed = todo.completed,
    url = s"${if (request.secure) "https" else "http"}://${request.host}/todos/${todo.id.toString}"
  )

  def wrap(s: String): String = s"""Simon says: \u201c${s}\u201d"""
}

case class TodoView(title: String, order: Int, completed: Boolean, url: String)
