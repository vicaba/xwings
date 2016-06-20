package wotgraph.app.sensedv.domain.repository

import org.scalactic.{Every, Or}
import wotgraph.app.error.StorageError
import wotgraph.app.sensedv.domain.Sensed
import wotgraph.app.user.domain.entity.User

import scala.concurrent.Future


trait SensedRepository {

  def create(sensed: Sensed):  Future[Sensed Or Every[StorageError]]


}
