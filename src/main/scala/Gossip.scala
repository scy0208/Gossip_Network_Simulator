package main.scala

import akka.actor.Actor.Receive
import akka.actor._
import scala.concurrent.duration._
import scala.util.Random

import scala.collection.mutable.ArrayBuffer

/**
 * Created by chunyangshen on 9/22/15.
 */

class GossipMain(numOfNode:Int, maxDup: Int, topo:String) extends Actor{


  var actorpool=ArrayBuffer[ActorRef]()
  var starttime:Long=0
  var numOfReceived=0


  for(i<-0 until numOfNode) {
    val actor = context.system.actorOf(Props(classOf[GossipNode],self, maxDup), name = "actor" + i)
    actorpool += actor
  }
  for(i<-0 until numOfNode) {
    val neighbors=new Topology(topo).calNeighbor(i,numOfNode)
    for(neighbor<-neighbors) actorpool(i)!Neighbor(actorpool(neighbor))
    //neighbors.foreach(neighbor=>actorpool(i)!Neighbor(actorpool(neighbor)))
  }

  self!Start
  import context.dispatcher
  //context.system.scheduler.scheduleOnce(30 seconds,self,Terminate)

  override def receive: Actor.Receive = {
    case Start =>{
      starttime=System.currentTimeMillis()
      actorpool(0)!Gossip
    }
    case Received =>{
      numOfReceived+=1;
      if(numOfReceived==actorpool.size) {
        println(topo+" topology took " + (System.currentTimeMillis - starttime) + "ms to converge")
        context stop self
        context.system.shutdown
      }
    }
    case Terminate =>{
      println("The system doesn't converge in 30 seconds, Receive rate %d/%d".format(numOfReceived,numOfNode))
      context stop self
      context.system.shutdown
    }

  }
}



class GossipNode(main: ActorRef, maxDup: Int) extends Actor {
  var neighbors=ArrayBuffer[ActorRef]()
  var goss_dupli=0;

  override def receive: Actor.Receive = {
    case Neighbor(neighbor:ActorRef) =>{
      //println("registed neighbor"+neighbor.toString())
      neighbors+=neighbor

    }
    case Gossip =>{

      goss_dupli+=1;
      if(goss_dupli==1) {
        main!Received
        //println("got gossip")
      }
      if(this.maxDup!=0&&goss_dupli==this.maxDup) self!Terminate
      val size=neighbors.size
      val num = Random.nextInt(size)
      import context.dispatcher
      context.system.scheduler.scheduleOnce(0 milliseconds,neighbors(num),Gossip)
    }
    case Terminate =>{
      context.stop(self)
    }
  }
}
