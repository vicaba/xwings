package edu.url.lasalle.wotgraph.application

package object exceptions {

  class ClientFormatException(val msg: String) extends IllegalArgumentException(msg)

  class ServiceUnavailableException(msg: String = "Service Unavailable") extends RuntimeException(msg)

  trait DatabaseException extends RuntimeException {
    val msg: String = ""
    val database: String = ""
  }

  trait ReadOperationException extends DatabaseException

  trait WriteOperationException extends DatabaseException

  class PartialUpdateException(override val msg: String, rollback: () => Unit = () => {})
    extends RuntimeException(msg) with WriteOperationException

  class SaveException(override val msg: String) extends WriteOperationException

  class UpdateException(override val msg: String) extends WriteOperationException

  class DeleteException(override val msg: String) extends WriteOperationException

  class ReadException(override val msg: String) extends ReadOperationException

}
