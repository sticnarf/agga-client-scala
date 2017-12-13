package me.sticnarf.agga.client.actors

import akka.actor.{Actor, ActorRef}
import me.sticnarf.agga.messages.ServerSegment

import scala.collection.mutable

class Aggregator(val clientHandler: ActorRef) extends Actor {
  implicit val ord = PacketOrdering
  val queue = mutable.PriorityQueue[ServerSegment]()

  var currentSeq = 0

  override def receive: Receive = {
    case s@ServerSegment(_, seq, data) =>
      if (seq == -1 && data.isEmpty) {
        clientHandler ! "close"
        context stop self
      }
      else if (seq == currentSeq) {
        clientHandler ! akka.util.ByteString(data.asReadOnlyByteBuffer())
        currentSeq += 1

        while (queue.nonEmpty && queue.head.seq == currentSeq) {
          val p = queue.dequeue()
          clientHandler ! akka.util.ByteString(p.data.asReadOnlyByteBuffer())
          currentSeq += 1
        }
      } else {
        queue.enqueue(s)
      }
  }

  object PacketOrdering extends Ordering[ServerSegment] {
    override def compare(x: ServerSegment, y: ServerSegment): Int = {
      (y.seq - x.seq) compare 0
    }
  }

}
