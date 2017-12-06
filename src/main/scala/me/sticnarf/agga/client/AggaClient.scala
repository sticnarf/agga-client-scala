package me.sticnarf.agga.client

import akka.actor.{ActorPath, ActorSystem, Props}
import akka.cluster.client.{ClusterClient, ClusterClientSettings}
import me.sticnarf.agga.client.actors.{Listener, Manager}
import me.sticnarf.agga.messages.Connect

import scala.util.Random

object AggaClient extends App {
  val system = ActorSystem()
  val initialContacts = Set(
    ActorPath.fromString("akka.tcp://agga@localhost:2552/system/receptionist"),
    ActorPath.fromString("akka.tcp://agga@localhost:2551/system/receptionist")
  )
  val receptionist = system.actorOf(ClusterClient.props(
    ClusterClientSettings(system).withInitialContacts(initialContacts)))

  val key = (1 to 16).map(_ => Random.nextPrintableChar()).mkString
  implicit val manager = system.actorOf(Props[Manager])
  receptionist ! ClusterClient.Send("/user/navigator", Connect(key), localAffinity = true)

  val listener = system.actorOf(Props(classOf[Listener], manager))
}
