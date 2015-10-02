
package main.scala

import akka.actor.Actor.Receive
import akka.actor.{Props, ActorRef, Actor}
import scala.concurrent.duration._

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class PushsumMain(numOfNode:Int, topo:String) extends Actor {
  var actorpool=ArrayBuffer[ActorRef]()
  var starttime:Long=0
  var numOfReceived=0


  for(i<-0 until numOfNode) {
    val actor = context.system.actorOf(Props(classOf[PushsumNode],self,i.toDouble,1.toDouble), name = "actor" + i)
    actorpool += actor
  }
  for(i<-0 until numOfNode) {
    val neighbors=new Topology(topo).calNeighbor(i,numOfNode)         //strategy pattern
    for(neighbor<-neighbors) actorpool(i)!Neighbor(actorpool(neighbor))
    //neighbors.foreach(neighbor=>actorpool(i)!Neighbor(actorpool(neighbor)))
  }

  self!Start

  override def receive: Actor.Receive = {

    case Start =>{
      starttime=System.currentTimeMillis()
      actorpool(0)!Data(0,0)
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
     // context stop self
      //context.system.shutdown
    }

  }

}

class PushsumNode(main: ActorRef, var s: Double, var  w: Double) extends Actor {
  var neighbors=ArrayBuffer[ActorRef]()
  val MIN = 0.000000000001
  var lastRecord:Double=0
  var convergeTime=0
  override def receive: Receive = {
    case Neighbor(neighbor:ActorRef) =>{
      //println("registed neighbor"+neighbor.toString())
      neighbors+=neighbor

    }
    case Data(x: Double,y: Double)=>{
      //println("%s receive s/w value is %f, %f; now the local s/w value are %f, %f".format(self,x,y,this.s,this.w))

      this.s=this.s+x
      this.w=this.w+y
      //println("%s  local s/w value are %f, %f".format(self,this.s,this.w))
      if(Math.abs(s/w-lastRecord)<MIN) {
        convergeTime += 1
        //main ! Received
        if(convergeTime==10) {
          println("%s converged at value s=%f w=%f s/w=%f converged time=%d error=%f".format(self,this.s,this.w, s/w,convergeTime,(s/w-lastRecord)/MIN ))
          main ! Received
          //self ! Terminate
        }
      }

      lastRecord=s/w
      val num = Random.nextInt(neighbors.size)
      //println("send s/w value are %f, %f".format(this.s/2,this.w/2))
      import context.dispatcher
      context.system.scheduler.scheduleOnce(0 milliseconds, neighbors(num), Data(this.s/2, this.w/2))
      this.s = this.s/2
      this.w =this.w/2


    }
    case Terminate =>{
      main!Received
      context.stop(self)
    }

  }
}

