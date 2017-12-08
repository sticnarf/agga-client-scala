package me.sticnarf.agga.client.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.Tcp
import akka.util.ByteString

class ClientHandler(val conn: Int, val remote: ActorRef) extends Actor with ActorLogging {
  val segmenter = context.actorOf(Props(classOf[Segmenter], conn))

  import Tcp._

  override def receive: Receive = {
    case Received(data) =>
      log.info("Received {} bytes, sender: {}", data.length, sender())
      segmenter ! data

    case data: ByteString =>
      log.info("Write {} bytes", data.length)
      remote ! Write(data)

    case PeerClosed => context stop self
  }
}
