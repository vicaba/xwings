package edu.url.lasalle.wotgraph.application

package object exceptions {

  class ClientFormatException(val msg: String) extends IllegalArgumentException(msg)

  class ServiceUnavailableException(msg: String = "Service Unavailable") extends RuntimeException(msg)

  trait LogicException extends Exception {
    val msg: String = ""
  }

  class CoherenceException(override val msg: String) extends LogicException

  trait DatabaseException extends RuntimeException {
    val msg: String = ""
    val database: String = ""
  }

  trait ReadOperationException extends DatabaseException

  trait WriteOperationException extends DatabaseException

  class SaveException(override val msg: String) extends WriteOperationException

  class UpdateException(override val msg: String) extends WriteOperationException

  class DeleteException(override val msg: String) extends WriteOperationException

  class ReadException(override val msg: String) extends ReadOperationException

}
