package main.scala.network;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;
import java.util.Random;

/**
 * Created by chunyangshen on 9/22/15.
 */

public class Topology {
    private Topo_Strategy strategy;

    Topology (String topo) {
        switch(topo) {
            case "full":
                strategy=new Full_Strategy();
                break;
            case "line":
                strategy=new Line_Strategy();
                break;
            case "3d":
                strategy=new ThreeD_Strategy();
                break;
            case "imp3d":
                strategy= new ImpThreeD_Strategy();
                break;
        }

    }
    public List<Integer> calNeighbor(int curr, int total) {
        return strategy.calNeighbor(curr,total);
    }



}



interface Topo_Strategy {
    public List<Integer> calNeighbor(int curr, int total);
}

class Line_Strategy implements Topo_Strategy {

    @Override
    public List<Integer> calNeighbor(int curr, int total) {
        List<Integer> neighbor = new ArrayList<Integer>();
        if(curr>=0) neighbor.add(curr-1);
        if(curr<total) neighbor.add(curr+1);
        return neighbor;
    }

}
class Full_Strategy implements Topo_Strategy {
    @Override
    public List<Integer> calNeighbor(int curr, int total) {
        List<Integer> neighbor = new ArrayList<Integer>();
        for(int i=0;i<total;i++) {
            if(i!=curr) {
                neighbor.add(i);
            }
        }
        return neighbor;
    }
}
class ThreeD_Strategy implements Topo_Strategy {

    int getId(int i, int j, int k, int length) {
        return i+j*length+k*length*length;
    }

    @Override
    public List<Integer> calNeighbor(int curr, int total) {
        List<Integer> neighbor = new ArrayList<Integer>();
        int length=(int)Math.round(Math.cbrt(total));
        int i,j,k;
        k=curr/(length*length);
        j=curr%(length*length)/length;
        i=curr%(length*length)%length;

        if(i-1>=0) neighbor.add(getId(i-1,j,k,length));
        if(i+1<length) neighbor.add(getId(i+1,j,k,length));
        if(j-1>=0) neighbor.add(getId(i,j-1,k,length));
        if(j+1<length) neighbor.add(getId(i,j+1,k,length));
        if(k-1>=0) neighbor.add(getId(i,j,k-1,length));
        if(k+1<length) neighbor.add(getId(i,j,k+1,length));


        return neighbor;
    }
}

class ImpThreeD_Strategy implements Topo_Strategy {

    int getId(int i, int j, int length) {
        return i+j*length;
    }
    @Override
    public List<Integer> calNeighbor(int curr, int total) {
        List<Integer> neighbor = new ArrayList<Integer>();
        int length=(int)Math.round(Math.sqrt(total));
        int i,j;
        j=curr/length;
        i=curr%length;

        if(i-1>=0) neighbor.add(getId(i-1,j,length));
        if(i+1<length) neighbor.add(getId(i+1,j,length));
        if(j-1>=0) neighbor.add(getId(i,j-1,length));
        if(j+1<length) neighbor.add(getId(i,j+1,length));

        Random rand= new Random();
        boolean unique = false;
        int rand_=rand.nextInt(total);
        while(!unique){
            unique=true;
            for(Integer num:neighbor) {
                if(rand_==num) {
                    unique=false;
                    rand_=rand.nextInt(total);
                }

            }
        }

        neighbor.add(rand_);

        return neighbor;
    }
}


