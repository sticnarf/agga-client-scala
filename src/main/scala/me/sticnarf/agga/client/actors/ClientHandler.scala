package me.sticnarf.agga.client.actors

import java.nio.charset.Charset

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.io.Tcp
import akka.util.ByteString
import me.sticnarf.agga.client.messages.TcpData

class ClientHandler(val conn: Int, val manager: ActorRef) extends Actor with ActorLogging {

  import Tcp._

  override def receive: Receive = {
    case Received(data) =>
      log.info("Received {} bytes, sender: {}", data.length, sender())
      manager ! TcpData(conn, data)

    case PeerClosed => context stop self
  }
}
