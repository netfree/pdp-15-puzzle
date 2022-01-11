package domain;

import domain.State;

import java.io.Serializable;

public class DistributedSearchResult implements Serializable {

    private Integer min;
    private State state;

    public DistributedSearchResult(Integer min, State state){
        this.min = min;
        this.state = state;
    }

    public Integer getMin(){return min;}

    public State getState() { return state; }

    public void setMin(Integer min){this.min = min;}

    public void setState(State state) {this.state = state;}

    @Override
    public String toString(){
        return "Integer,State = " + min + "," + state;
    }

}
