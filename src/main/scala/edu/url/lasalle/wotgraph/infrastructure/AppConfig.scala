package edu.url.lasalle.wotgraph.infrastructure

import com.typesafe.config.ConfigFactory


object AppConfig {
  val defaultConf = ConfigFactory.load("reference.conf")
}
