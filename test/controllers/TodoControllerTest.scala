package controllers

import models.Todo
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.mvc.Results
import play.api.test.Helpers._
import play.api.test._
import repositories.TodoRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TodoControllerTest extends PlaySpec
  with Results
  with MockitoSugar {

  trait Subject {
    val repository = mock[TodoRepository]
    val controller = new TodoController(repository)
  }

  "TodoController#index" should {
    "list all TODOs" in new Subject {
      when(repository.getAllTodos).thenReturn(Future(List(Todo(1L, "My Todo", 10, completed = false))))

      val result = controller.index.apply(FakeRequest(GET, "http://localhost/todos"))

      status(result) mustEqual OK

      val json = contentAsJson(result)
      (json \ 0 \ "title").as[String] mustEqual "Simon says: My Todo"
      (json \ 0 \ "order").as[Int] mustEqual 10
      (json \ 0 \ "completed").as[Boolean] mustBe false
      (json \ 0 \ "url").as[String] mustEqual "http://localhost/todos/1"
    }
  }

  "TodoController#get" should {
    "get existing TODO" in new Subject {
      when(repository.getTodo(10)).thenReturn(Future(Some(Todo(1L, "My Todo", 10, completed = false))))

      val result = controller.get(10).apply(FakeRequest(GET, "http://localhost/todos/10"))

      status(result) mustEqual OK

      val json = contentAsJson(result)
      (json \ "title").as[String] mustEqual "Simon says: My Todo"
      (json \ "order").as[Int] mustEqual 10
      (json \ "completed").as[Boolean] mustBe false
      (json \ "url").as[String] mustEqual "http://localhost/todos/1"
    }

    "ignore inexistent TODOs" in new Subject {
      when(repository.getTodo(10)).thenReturn(Future(None))

      val result = controller.get(10).apply(FakeRequest("GET", "http://localhost/todos/10"))

      status(result) mustEqual NOT_FOUND
    }
  }

  "TodoController#add" should {
    "add TODO with title" in new Subject {
      when(repository.addTodo("My Todo", completed = false, 0)).thenReturn(Future(Some(Todo(1L, "My Todo", 10, completed = false))))

      val fakeRequest = FakeRequest(POST, "http://localhost/todos", FakeHeaders(),
        Json.parse(
          """
            |{
            |  "title": "My Todo"
            |}
          """.stripMargin))

      val result = controller.add.apply(fakeRequest)

      status(result) mustEqual OK

      val json = contentAsJson(result)
      (json \ "title").as[String] mustEqual "Simon says: My Todo"
      (json \ "order").as[Int] mustEqual 10
      (json \ "completed").as[Boolean] mustBe false
      (json \ "url").as[String] mustEqual "http://localhost/todos/1"
    }
  }

  "TodoController#removeAll" should {
    "remove all TODOs" in new Subject {
      when(repository.removeAllTodos()).thenReturn(Future(true))

      val result = controller.removeAll().apply(FakeRequest(DELETE, "http://localhost/todos"))

      status(result) mustEqual OK
    }
  }

  "TodoController#remove" should {
    "remove single TODO" in new Subject {
      when(repository.removeTodo(1L)).thenReturn(Future(1))

      val result = controller.remove(1L).apply(FakeRequest(DELETE, "http://localhost/todos/1"))

      status(result) mustEqual OK
    }
  }

  "TodoController#update" should {
    "update title" in new Subject {
      when(repository.updateTodo(1L, Some("new title"), None, None)).thenReturn(Future(Some(Todo(1L, "new title", 10, false))))

      val fakeRequest = FakeRequest(PATCH, "http://localhost/todos/1", FakeHeaders(),
        Json.parse(
          """
            |{
            |  "title": "new title"
            |}
          """.stripMargin))

      val result = controller.update(1L).apply(fakeRequest)

      status(result) mustEqual OK
      (contentAsJson(result) \ "title").as[String] mustEqual "Simon says: new title"
    }

    "mark completion" in new Subject {
      when(repository.updateTodo(1L, None, Some(true), None)).thenReturn(Future(Some(Todo(1L, "My Todo", 10, true))))

      val fakeRequest = FakeRequest(PATCH, "http://localhost/todos/1", FakeHeaders(),
        Json.parse(
          """
            |{
            |  "completed": true
            |}
          """.stripMargin))

      val result = controller.update(1L).apply(fakeRequest)

      status(result) mustEqual OK
      (contentAsJson(result) \ "completed").as[Boolean] mustBe true
    }

    "update order" in new Subject {
      when(repository.updateTodo(1L, None, None, Some(5))).thenReturn(Future(Some(Todo(1L, "My Todo", 5, false))))

      val fakeRequest = FakeRequest(PATCH, "http://localhost/todos/1", FakeHeaders(),
        Json.parse(
          """
            |{
            |  "order": 5
            |}
          """.stripMargin))

      val result = controller.update(1L).apply(fakeRequest)

      status(result) mustEqual OK
      (contentAsJson(result) \ "order").as[Int] mustEqual 5
    }
  }
}
