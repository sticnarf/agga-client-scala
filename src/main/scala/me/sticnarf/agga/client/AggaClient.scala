package me.sticnarf.agga.client

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.cluster.client.{ClusterClient, ClusterClientSettings}
import me.sticnarf.agga.client.actors.{Listener, Manager}
import me.sticnarf.agga.messages.FetchServerList

object AggaClient extends App {
  val system = ActorSystem()
  //  val initialContacts = Set(
  //    ActorPath.fromString("akka.tcp://agga@yilin-pc:2552/system/receptionist")
  //  )
  val receptionist = system.actorOf(ClusterClient.props(ClusterClientSettings(system)))

  val key = UUID.randomUUID().toString
  implicit val manager = system.actorOf(Props(classOf[Manager], key), "manager")
  receptionist ! ClusterClient.Send("/user/navigator", FetchServerList(key), localAffinity = true)

  val listener = system.actorOf(Props(classOf[Listener]))
}
