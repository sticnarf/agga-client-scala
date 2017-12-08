package me.sticnarf.agga.client.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import me.sticnarf.agga.messages._

import scala.collection.mutable
import scala.util.Random

class Manager(val key: String) extends Actor with ActorLogging {
  val serverIds = mutable.ArrayBuffer[String]()
  val servers = mutable.HashMap[String, ActorRef]()

  val aggregators = mutable.HashMap[Int, ActorRef]()

  override def receive = {
    case ServerList(list) =>
      log.info("ServerList: {}", list)
      for (serverInfo <- list) {
        val server = context.system.actorSelection(s"${serverInfo.address}/user/postman")
        server ! Connect(key)
      }

    case Ack(serverId) =>
      log.info("Ack from server: {}", serverId)
      serverIds += serverId
      servers.put(serverId, sender())

    case (conn: Int, aggregator: ActorRef) =>
      // Register aggregator
      aggregators.put(conn, aggregator)

    case p: ClientSegment =>
      val idx = Random.nextInt(serverIds.size)
      val serverId = serverIds(idx)
      val server = servers(serverId)
      log.info("Send {} bytes to server {}", p.data.size(), serverId)
      server ! p.withClientKey(key)

    case s@ServerSegment(conn, _, _) =>
      aggregators(conn) ! s
  }
}
