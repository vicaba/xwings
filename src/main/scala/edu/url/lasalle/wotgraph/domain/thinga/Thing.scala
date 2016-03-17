package domain.thinga

import java.util.UUID

import scala.reflect.ClassTag

import scala.reflect.runtime.universe._


case class Thing(
             val id: UUID = UUID.randomUUID(),
             val name: String
           )

case class ThingWithAction(thing: Thing, action: ActionDefinition[_], children: List[ThingWithAction] = Nil) {
  def execute(contextProvider: ActionContextProvider): String = {
    action.execute(thing, contextProvider.provideWithContextFor(action.contextType))
  }
}

trait ActionDefinition[Context] {

  val contextType: TypeTag[Context]

  val callback: (Thing, Context) => String

  def execute(thing: Thing, context: Any): String = callback(thing, context.asInstanceOf[Context])

}

case class HTTPRequestAction() extends ActionDefinition[Int] {
  override val contextType: TypeTag[Int] = implicitly[TypeTag[Int]]
  override val callback: (Thing, Int) => String = (t, s) => s + "a"
}

case class ContextProviderException() extends Exception("ContextProviderException")

trait ActionContextProvider {

  /**
    * Converts context keys to context Types
    */
  val contextNameToTypeConversion: PartialFunction[String, Type]

  /**
    * Converts context Types to Values
    */
  val typeToContextConversion: PartialFunction[Type, Any]
  lazy val contextNameToContextConversion = contextNameToTypeConversion andThen provideWithContextFromType

  /**
    * Provides with a context value from a context key
    * @param contextName the context name or key that produces the desired context value
    * @return a context
    */
  def provideWithContextFor(contextName: String): Any = {
    if (!contextNameToContextConversion.isDefinedAt(contextName)) throw new ContextProviderException()
    contextNameToContextConversion(contextName)
  }

  /**
    * Provides with a context of the desired type
    * @param ev the evidence of type T
    * @tparam T the type of the context
    * @return a context
    */
  def provideWithContextFromType[T](ev: Type): T = {
    if (!typeToContextConversion.isDefinedAt(ev)) throw new ContextProviderException()
    typeToContextConversion(ev).asInstanceOf[T]
  }

  def provideWithContextFor[T](implicit ev: TypeTag[T]): T =
    provideWithContextFromType(ev.tpe).asInstanceOf[T]
}

object DefaultContextProvider extends ActionContextProvider {

  override val contextNameToTypeConversion: PartialFunction[String, Type] = {
    case "Int" => typeOf[Int]
  }

  override val typeToContextConversion: PartialFunction[Type, Any] = {
    case ev if ev =:= typeOf[Int] => 2
    case ev if ev =:= typeOf[String] => "2"
  }

}

object Main {
  def main(args: Array[String]) {
    val t = Thing(name = "TheThing")
    val ta = ThingWithAction(t, HTTPRequestAction())
    println(ta.execute(DefaultContextProvider))
  }
}


