package sequential;
import domain.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class SearchResult {
    boolean found;
    int t;
    List<State> stack;

    public SearchResult(boolean found, int t, List<State> stack) {
        this.found = found;
        this.t = t;
        this.stack = stack;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public List<State> getStack() {
        return stack;
    }

    public void setStack(List<State> stack) {
        this.stack = stack;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "found=" + found +
                ", t=" + t +
                '}';
    }
}

public class SequentialIDAStar {
    public static List<State> ida_star(State initialState){
        int bound = initialState.getH();
        List<State> path = new ArrayList<>();
        path.add(initialState);
        while(true){
            SearchResult searchResult = search(path, bound);
            System.out.println(searchResult.found + " " + searchResult.t);
            if(searchResult.found)
                return path;
            if(searchResult.t == Integer.MAX_VALUE)
                return null;
            bound = searchResult.t;
        }
    }

    public static SearchResult search(List<State> path, int bound){
        State state = path.get(path.size() - 1);

        int f = state.getG() + state.getH();
        if(f > bound)
            return new SearchResult(false, f, null);
        if(state.isGoalState())
            return new SearchResult(true, -1, path);
        int min = Integer.MAX_VALUE;
        for(State succ: state.getListOfCandidates()){
            if(!path.contains(succ)){
                path.add(succ);
                SearchResult searchResult = search(path, bound);
                if(searchResult.found)
                    return searchResult;
                if(searchResult.t < min)
                    min = searchResult.t;
                path.remove(path.size() - 1);
            }
        }
        return new SearchResult(false, min, null);
    }
}
