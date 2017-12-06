package me.sticnarf.agga.client.messages

import akka.util.ByteString

case class TcpData(conn: Int, data: ByteString)
