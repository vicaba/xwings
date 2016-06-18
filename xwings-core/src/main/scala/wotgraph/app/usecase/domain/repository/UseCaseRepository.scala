package wotgraph.app.usecase.domain.repository

import org.scalactic.{Every, Or}
import wotgraph.app.error.StorageError
import wotgraph.app.usecase.domain.entity.UseCase

import scala.concurrent.Future


trait UseCaseRepository {

  def createUseCase(useCase: UseCase): Future[UseCase Or Every[StorageError]]

}
