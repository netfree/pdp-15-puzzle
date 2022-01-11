package domain;

import domain.State;

import java.util.List;

public class ThreadedSearchResult {
    public boolean found;
    public int t;
    public List<State> path;

    public ThreadedSearchResult(boolean found, int t, List<State> stack) {
        this.found = found;
        this.t = t;
        this.path = stack;
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

    public List<State> getPath() {
        return path;
    }

    public void setPath(List<State> path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "found=" + found +
                ", t=" + t +
                '}';
    }
}
