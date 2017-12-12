package me.sticnarf.agga.client

import java.net.InetSocketAddress
import java.util.UUID

import com.typesafe.config.ConfigFactory

object AggaConfig {
  private val config = ConfigFactory.load().getConfig("agga")
  private val tcpListenConfig = config.getConfig("tcp-listen")

  val listenAddress = new InetSocketAddress(tcpListenConfig.getString("hostname"), tcpListenConfig.getInt("port"))
  val key = config.getString("client-key")
}
