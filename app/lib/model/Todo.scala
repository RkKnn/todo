package lib.model

import ixias.model._
import java.time.LocalDateTime

import Todo._
case class Todo(
    id: Option[Id],
    categoryId: Category.Id,
    title: String,
    body: String,
    state: Int,
    updatedAt: LocalDateTime = NOW,
    createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

object Todo {
  val Id = the[Identity[Id]]
  type Id = Long @@ Todo
  type WithNoId = Entity.WithNoId[Id, Todo]
  type EmbeddedId = Entity.EmbeddedId[Id, Todo]

  def apply(categoryId: Category.Id, title: String, body: String, state: Int): WithNoId = {
    new Entity.WithNoId(new Todo(
      None, categoryId, title, body, state
    ))
  }

  type CategoryRef = Map[Todo, Category]
  def createCategoryRef(todo: Seq[Todo.EmbeddedId], categories: Seq[Category.EmbeddedId]): CategoryRef = {
    val categoryIdMap: Map[Category.Id, Category.EmbeddedId] = (for {
      category <- categories
    } yield (category.id -> category)).toMap

    val categoryMap: Map[Todo, Category] = (for {
      value <- todo
      category <- categoryIdMap.get(value.v.categoryId)
    } yield (value.v -> category.v)).toMap

    categoryMap
  }

  // type CategoryRef = Map[Todo, Seq[Category]]
  // def createCategoryRef(todo: Seq[Todo.EmbeddedId], categories: Seq[Category.EmbeddedId]): CategoryRef = {
  //   val categoryIdMap: Map[Category.Id, Seq[Category.EmbeddedId]] = categories.groupBy(_.id)

  //   val categoryMap: Map[Todo, Seq[Category]] = (for {
  //     value <- todo
  //     category <- categoryIdMap.get(value.v.categoryId)
  //   } yield (value.v -> category.map(_.v))).toMap

  //   categoryMap.withDefaultValue(Seq.empty)
  // }
}