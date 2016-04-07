package edu.url.lasalle.wotgraph.application

package object exceptions {

  class ClientFormatException(val msg: String) extends IllegalArgumentException(msg)

  class ServiceUnavailableException(msg: String = "Service Unavailable") extends RuntimeException(msg)

  class PartialUpdateException(msg: String, rollback: () => Unit = () => {}) extends RuntimeException(msg)

  trait WriteOperationException

  class SaveException(msg: String) extends RuntimeException(msg) with WriteOperationException

  class UpdateException(msg: String) extends RuntimeException(msg) with WriteOperationException

}
