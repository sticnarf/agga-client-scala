package me.sticnarf.agga.client.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.io.Tcp.PeerClosed
import me.sticnarf.agga.messages._

import scala.collection.mutable

class Manager(val key: String) extends Actor with ActorLogging {
  val serverIds = mutable.ArrayBuffer[String]()
  val servers = mutable.HashMap[String, ActorRef]()

  val aggregators = mutable.HashMap[Int, ActorRef]()

  object sampleServer extends (() => ActorRef) {
    private var idx = 0;

    def apply: ActorRef = {
      val serverId = serverIds(idx % serverIds.size)
      idx += 1
      servers(serverId)
    }
  }

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
      log.info("Send {} bytes to server", p.data.size())
      sampleServer() ! p.withClientKey(key)

    case s@ServerSegment(conn, _, _) =>
      aggregators(conn) ! s

    case (PeerClosed, conn: Int) =>
      sampleServer() ! ClientSegment(conn, -1, com.google.protobuf.ByteString.EMPTY, key)
      aggregators -= conn
  }
}
