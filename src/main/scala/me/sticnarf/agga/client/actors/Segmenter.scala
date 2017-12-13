package me.sticnarf.agga.client.actors

import akka.actor.Actor
import akka.io.Tcp.PeerClosed
import akka.util.ByteString
import me.sticnarf.agga.messages.ClientSegment

class Segmenter(val conn: Int) extends Actor {
  val manager = context.actorSelection("/user/manager")

  val SEGMENT_SIZE = 3900

  var currentSeq = 0

  override def receive: Receive = {
    case data: ByteString => {
      data.grouped(SEGMENT_SIZE).foreach { bytes =>
        val seq = currentSeq
        currentSeq += 1
        manager ! ClientSegment(conn, seq, com.google.protobuf.ByteString.copyFrom(bytes.asByteBuffer))
      }
    }

    case c@PeerClosed =>
      manager ! (c, conn)
      context stop self
  }
}
