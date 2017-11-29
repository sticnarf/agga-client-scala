package me.sticnarf.agga.client.actors

import akka.actor.Actor
import me.sticnarf.agga.messages.shared.ServerList

class Manager extends Actor {
  override def receive = {
    case list: ServerList =>
      println(s"${list.servers}")
  }
}
