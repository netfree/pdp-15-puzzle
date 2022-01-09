package mpi;

import java.io.Serializable;

public class Pair<E1,E2> implements Serializable {

    private E1 el1;
    private E2 el2;

    public Pair(E1 el1, E2 el2){
        this.el1 = el1;
        this.el2 = el2;
    }

    public E1 getEl1(){return el1;}

    public E2 getEl2() { return el2; }

    public void setEl1(E1 el1){this.el1=el1;}

    public void setEl2(E2 el2) {this.el2 = el2;}

    @Override
    public String toString(){
        return "E1,E2 = " + el1 + "," + el2;
    }

}
