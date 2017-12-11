package me.sticnarf.agga.client.actors

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging, Props}
import akka.io.{IO, Tcp}
import me.sticnarf.agga.client.AggaConfig

class Listener() extends Actor with ActorLogging {
  val manager = context.actorSelection("/user/manager")

  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, AggaConfig.listenAddress)

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
      val handler = context.actorOf(Props(classOf[ClientHandler], conn, connection))
      val aggregator = context.actorOf(Props(classOf[Aggregator], handler))
      connection ! Register(handler)
      manager ! (conn, aggregator)
  }
}
