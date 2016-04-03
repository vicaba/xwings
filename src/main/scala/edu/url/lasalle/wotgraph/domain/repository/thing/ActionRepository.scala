package edu.url.lasalle.wotgraph.domain.repository.thing

import java.util.UUID

import edu.url.lasalle.wotgraph.domain.thing.Action

import scala.concurrent.Future

trait ActionRepository {


  def getActionsForThingIds(ids: Set[UUID]): Future[List[Set[Action]]]

}
