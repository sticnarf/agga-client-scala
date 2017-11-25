package me.sticnarf.agga

import akka.actor.{Actor, ActorPath, ActorSystem, Props}
import akka.cluster.client.{ClusterClient, ClusterClientSettings}
import me.sticnarf.agga.messages.{Bye, Greeting}

class GreetActor extends Actor {
  println("Initialize GreetActor")

  override def receive = {
    case Greeting(name) =>
      println(s"$name greets!")
    case Bye(name) =>
      println(s"Bye, $name!")
      context.stop(self)
  }


  override def preStart(): Unit = {
    println("Start GreetActor")
  }

  override def postStop(): Unit = {
    println("Stop GreetActor")
    context.system.terminate()
  }
}

object Agga extends App {
  val system = ActorSystem()
  val initialContacts = Set(
    ActorPath.fromString("akka.tcp://agga@127.0.0.1:2552/system/receptionist"),
    ActorPath.fromString("akka.tcp://agga@127.0.0.1:2551/system/receptionist")
  )
  val receptionist = system.actorOf(ClusterClient.props(
    ClusterClientSettings(system).withInitialContacts(initialContacts)))

  //  val remote = system.actorSelection("akka.tcp://agga@127.0.0.1:2552/user/greet")
  implicit val actor = system.actorOf(Props[GreetActor])
  receptionist ! ClusterClient.Send("/user/workers", Greeting("Yilin"), localAffinity = true)
  receptionist ! ClusterClient.Send("/user/workers", Greeting("Foobar"), localAffinity = true)
  receptionist ! ClusterClient.Send("/user/workers", Bye("Yilin"), localAffinity = true)
  //
  //  remote ! Greeting("Yilin")
  //  remote ! Greeting("Foobar")
  //  remote ! Bye("Yilin")
}
