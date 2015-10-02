package main.scala

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Topology {
  var strategy: Topo_Strategy = null
  def this(topo:String) {
    this()
    this.strategy={
      topo match {
        case "line"=>new Line_Strategy
        case "full"=>new Full_Strategy
        case "3d"=>new ThreeD_Strategy
        case "imp3d"=>new ImpThreeD_Strategy
      }
    }
  }
  def calNeighbor(curr: Int, total: Int): ArrayBuffer[Int] = {
    return strategy.calNeighbor(curr, total)
  }
}

trait Topo_Strategy {
  def calNeighbor(curr: Int, total: Int): ArrayBuffer[Int]
}


class Line_Strategy extends Topo_Strategy {
  def calNeighbor(curr: Int, total: Int):ArrayBuffer[Int]= {
    var neighbor =ArrayBuffer[Int]()
    if (curr-1 >= 0) neighbor+=(curr-1)
    if (curr+1 < total) neighbor+=(curr+1)
    return neighbor
  }
}

class Full_Strategy extends Topo_Strategy {
  def calNeighbor(curr: Int, total: Int):ArrayBuffer[Int] = {
    var neighbor =ArrayBuffer[Int]()
      var i = 0
      while (i < total) {
        {
          if (i != curr) {
            neighbor+=i
          }
          i+=1
        }
      }
    return neighbor
  }
}

class ThreeD_Strategy extends Topo_Strategy {
  def getId(i: Int, j: Int, k: Int, length: Int): Int = {
    return i + j * length + k * length * length
  }

  def calNeighbor(curr: Int, total: Int):ArrayBuffer[Int] = {
    var neighbor =ArrayBuffer[Int]()
    val length: Int = Math.cbrt(total).round.toInt
    var i: Int = 0
    var j: Int = 0
    var k: Int = 0
    k = curr / (length * length)
    j = curr % (length * length) / length
    i = curr % (length * length) % length
    if (i - 1 >= 0) neighbor += (getId(i - 1, j, k, length))
    if (i + 1 < length && getId(i + 1, j, k, length)<total ) neighbor+=(getId(i + 1, j, k, length))
    if (j - 1 >= 0) neighbor+=(getId(i, j - 1, k, length))
    if (j + 1 < length && getId(i , j+1, k, length)<total) neighbor+=(getId(i, j + 1, k, length))
    if (k - 1 >= 0) neighbor+=(getId(i, j, k - 1, length))
    if (k + 1 < length && getId(i, j, k+1, length)<total) neighbor+=(getId(i, j, k + 1, length))
    return neighbor
  }
}

class ImpThreeD_Strategy extends Topo_Strategy {
  def getId(i: Int, j: Int, length: Int): Int = {
    return i + j * length
  }

  def calNeighbor(curr: Int, total: Int):ArrayBuffer[Int]  = {
    var neighbor =ArrayBuffer[Int]()
    val length: Int = Math.sqrt(total).round.toInt
    var i: Int = 0
    var j: Int = 0
    j = curr / length
    i = curr % length
    if (i - 1 >= 0) neighbor+=(getId(i - 1, j, length))
    if (i + 1 < length && getId(i + 1, j, length)<total) neighbor+=(getId(i + 1, j, length))
    if (j - 1 >= 0) neighbor+=(getId(i, j - 1, length))
    if (j + 1 < length && getId(i, j+1, length)<total) neighbor+=(getId(i, j + 1, length))
    //val rand: Random = new Random
    var unique: Boolean = false
    var rand_ : Int = Random.nextInt(total)
    while (!unique) {
      unique = true
      for (num <- neighbor) {
        if (rand_ == num) {
          unique = false
          rand_ = Random.nextInt(total)
        }
      }
    }
    neighbor+=(rand_)
    return neighbor
  }
}
