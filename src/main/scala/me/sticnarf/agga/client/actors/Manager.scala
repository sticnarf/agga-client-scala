package me.sticnarf.agga.client.actors

import akka.actor.{Actor, ActorLogging, ActorSelection}
import me.sticnarf.agga.client.messages.TcpData
import me.sticnarf.agga.messages.{Packet, ServerList}

import scala.collection.mutable

class Manager extends Actor with ActorLogging {
  val servers = mutable.Queue[ActorSelection]()

  override def receive = {
    case list: ServerList =>
      for (serverInfo <- list.servers) {
        val server = context.system.actorSelection(s"${serverInfo.address}/user/redirector")
        servers += server
      }
      log.info("ServerList: {}", list)

    case TcpData(conn, data) => {
      val server = servers.dequeue()
      servers.enqueue(server)
      server ! Packet(conn, 0, com.google.protobuf.ByteString.copyFrom(data.asByteBuffer))
    }
  }
}
