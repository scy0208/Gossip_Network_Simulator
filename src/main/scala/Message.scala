package main.scala


import akka.actor.ActorRef

case class Neighbor (neighbor : ActorRef)
case object Gossip
case object Received
case object Terminate
case object Finish_Networking
case object Start
case class Data(s: Double,w: Double)
case class Result(result: Double)