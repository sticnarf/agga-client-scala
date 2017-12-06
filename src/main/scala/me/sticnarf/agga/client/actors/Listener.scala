package me.sticnarf.agga.client.actors

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.{IO, Tcp}

class Listener(val manager: ActorRef) extends Actor with ActorLogging {

  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", 8080))

  var counter = 1

  override def receive: Receive = {
    case b@Bound(localAddress) =>
      log.info("Local address: {}", localAddress)
      context.parent ! b

    case CommandFailed(_: Bind) =>
      context stop self

    case c@Connected(remote, local) =>
      log.info("Remote: {}, Local: {}", remote, local)
      val conn = counter
      counter += 1
      val connection = sender()
      val handler = context.actorOf(Props(classOf[ClientHandler], conn, manager))
      connection ! Register(handler)
  }
}
