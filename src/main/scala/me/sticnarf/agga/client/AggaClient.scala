package me.sticnarf.agga.client

import akka.actor.{ActorSystem, Props}
import akka.cluster.client.{ClusterClient, ClusterClientSettings}
import me.sticnarf.agga.client.actors.{Listener, Manager}
import me.sticnarf.agga.messages.FetchServerList

object AggaClient extends App {
  val system = ActorSystem()
  val receptionist = system.actorOf(ClusterClient.props(ClusterClientSettings(system)))

  implicit val manager = system.actorOf(Props(classOf[Manager], AggaConfig.key), "manager")
  receptionist ! ClusterClient.Send("/user/navigator", FetchServerList(AggaConfig.key), localAffinity = true)

  val listener = system.actorOf(Props(classOf[Listener]))
}
