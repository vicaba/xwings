package edu.url.lasalle.wotgraph.application

package object exceptions {

  class ClientFormatException(val msg: String) extends IllegalArgumentException(msg)

  class ServiceUnavailableException(msg: String = "Service Unavailable") extends RuntimeException(msg)

}
