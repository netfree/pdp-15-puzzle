package sequential;
import domain.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

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
    public static List<State> ida_star(State initialState, int nr_threads) throws ExecutionException, InterruptedException {
        int bound = initialState.getH();
        List<State> path = new ArrayList<>();
        path.add(initialState);
        while(true){
            SearchResult searchResult = threaded_search(path, bound, nr_threads);
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

    public static SearchResult threaded_search(List<State> path, int bound, int nr_threads) throws ExecutionException, InterruptedException {
        State state = path.get(path.size() - 1);
        int f = state.getG() + state.getH();
        if(f > bound)
            return new SearchResult(false, f, null);
        if(state.isGoalState())
            return new SearchResult(true, -1, path);
        int min = Integer.MAX_VALUE;

        List<State> executedByCurrentThread = new ArrayList<>();
        List<State> candidates = state.getListOfCandidates();

        // remove all invalid candidates
        candidates.removeIf(path::contains);

        // reserve one candidate to be executed in the current thread
        if(candidates.size() > 0){
            int last_candidate_idx = candidates.size() - 1;
            executedByCurrentThread.add(candidates.get(last_candidate_idx));
            candidates.remove(last_candidate_idx);
        }

        int nr_candidates = candidates.size();

        List<FutureTask<SearchResult>> futureTaskList = new ArrayList<>();

        for(int i = 0; i < nr_candidates; i ++){
            State candidate = candidates.get(i);
            if(i < nr_threads) {
                List<State> newPath = new ArrayList<>(path);
                newPath.add(candidate);
                int q = nr_threads / nr_candidates - 1;
                if(i < nr_threads % nr_candidates)
                    q += 1;
                q = Math.max(q, 0);

                int finalQ = q;
                FutureTask<SearchResult> futureTask = new FutureTask<>(() -> threaded_search(newPath, bound, finalQ));
                futureTaskList.add(futureTask);
                Thread t = new Thread(futureTask);
                t.start();
            } else executedByCurrentThread.add(candidate);
        }

        for(State candidate: executedByCurrentThread){
                path.add(candidate);
                SearchResult searchResult = search(path, bound);
                if(searchResult.found)
                    return searchResult;
                if(searchResult.t < min)
                    min = searchResult.t;
                path.remove(path.size() - 1);
        }

        for(FutureTask<SearchResult> futureTask : futureTaskList){
            SearchResult searchResult = futureTask.get();
            if(searchResult.found)
                return searchResult;
            if(searchResult.t < min)
                min = searchResult.t;
        }

        return new SearchResult(false, min, null);
    }

}
