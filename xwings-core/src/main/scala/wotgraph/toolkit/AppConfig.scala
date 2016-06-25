package wotgraph.toolkit

import com.typesafe.config.ConfigFactory


object AppConfig {
  val defaultConf = ConfigFactory.load("application.conf").withFallback(ConfigFactory.load("reference.conf"))
}
