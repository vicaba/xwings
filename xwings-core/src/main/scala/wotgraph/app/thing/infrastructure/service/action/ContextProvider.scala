package wotgraph.app.thing.infrastructure.service.action

import java.util.UUID

import scaldi.Module
import wotgraph.app.thing.application.service.action.{ActionContext, UUIDCanBeIdentifier}
import wotgraph.app.thing.infrastructure.service.action.contexts.http.HttpContext

import scala.concurrent.ExecutionContext.Implicits.global

object AvailableContexts {
  val HttpContext = UUID.fromString("b4dd4ad5-71ef-4792-bb6c-7bbffce31cde")
  val WriteToDatabaseContext = UUID.fromString("528515e2-e7ce-44ee-ac1f-ce490581607b")
  val ReadFromDatabaseContext = UUID.fromString("15761b62-ed87-4d86-84f4-12750ca4ff52")
}

object ContextProvider {

  val injector = new Module {

    import UUIDCanBeIdentifier._


    bind[ActionContext[_]] identifiedBy AvailableContexts.HttpContext to HttpContext()

  }

}
