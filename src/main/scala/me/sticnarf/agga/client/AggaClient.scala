package me.sticnarf.agga.client

import akka.actor.{ActorSystem, Props}
import akka.cluster.client.{ClusterClient, ClusterClientSettings}
import me.sticnarf.agga.client.actors.{Listener, Manager}
import me.sticnarf.agga.messages.FetchServerList

object AggaClient extends App {
  val system = ActorSystem()
  val receptionist = system.actorSelection("akka.tcp://agga@127.0.0.1:7000/user/receptionist")

  implicit val manager = system.actorOf(Props(classOf[Manager], AggaConfig.key), "manager")
  receptionist ! FetchServerList(AggaConfig.key)

  val listener = system.actorOf(Props(classOf[Listener]))
}
