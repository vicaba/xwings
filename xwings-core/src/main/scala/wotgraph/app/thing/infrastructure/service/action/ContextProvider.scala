package wotgraph.app.thing.infrastructure.service.action

import scaldi.Module
import wotgraph.app.thing.application.service.ActionContext

import scala.concurrent.ExecutionContext.Implicits.global

object AvailableContexts {
  val HttpContext = "b4dd4ad5-71ef-4792-bb6c-7bbffce31cde"
}

object ContextProvider {

  val injector = new Module {

    bind[ActionContext[_]] identifiedBy AvailableContexts.HttpContext to HttpContext()

  }

}
