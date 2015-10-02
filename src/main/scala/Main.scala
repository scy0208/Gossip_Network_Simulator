package main.scala

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

/**
 * Created by chunyangshen on 9/23/15.
 */
object Gossip_Pushsum {
  def main(args: Array[String]): Unit = {

    val algorithm= args(0)
    val numOfNode=args(1).toInt
    val topology=args(2)
    val actorsystem = ActorSystem("actorsystem", ConfigFactory.load(ConfigFactory.parseString("""
  akka {
    log-dead-letters = off
  }
                                                                                              """)))

    algorithm match {
      case "gossip" =>{
        val maxDup=args(3).toInt
        val gossip = actorsystem.actorOf(Props(classOf[GossipMain], numOfNode,maxDup,topology))
      }
      case "pushsum" =>{
        val convTime=args(3).toInt
        val pushsum = actorsystem.actorOf(Props(classOf[PushsumMain], numOfNode,convTime,topology))
      }
    }
  }
}
