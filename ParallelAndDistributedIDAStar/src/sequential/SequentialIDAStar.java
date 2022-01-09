package sequential;
import domain.State;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

class SearchResult {
    boolean found;
    int t;
    List<State> path;

    public SearchResult(boolean found, int t, List<State> stack) {
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

public class SequentialIDAStar {
    public static List<State> ida_star(State initialState, int nr_threads) throws ExecutionException, InterruptedException {
        int bound = initialState.getH();
        List<State> path = new ArrayList<>();
        path.add(initialState);
        while(true){
            SearchResult searchResult = threaded_search(path, bound, new AtomicInteger(nr_threads + 1));
            //System.out.println(searchResult.found + " " + searchResult.t);
            if(searchResult.found)
                return path;
            if(searchResult.t == Integer.MAX_VALUE)
                return null;
            bound = searchResult.t;
            //System.out.println("used " + atomicInteger.get());
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

    public static SearchResult threaded_search(List<State> path, int bound, AtomicInteger nr_threads) throws ExecutionException, InterruptedException {
        State state = path.get(path.size() - 1);
        int f = state.getG() + state.getH();
        if(f > bound)
            return new SearchResult(false, f, null);
        if(state.isGoalState())
            return new SearchResult(true, -1, path);
        int min = Integer.MAX_VALUE;

        List<State> executedByCurrentThread = new ArrayList<>();
        List<State> candidates = state.getListOfCandidates();

        // remove all candidates already explored
        candidates.removeIf(path::contains);

        // reserve one candidate to be executed in the current thread
        if(candidates.size() > 0){
            int last_candidate_idx = candidates.size() - 1;
            executedByCurrentThread.add(candidates.get(last_candidate_idx));
            candidates.remove(last_candidate_idx);
        }

        int nr_candidates = candidates.size();
        List<FutureTask<SearchResult>> futureTaskList = new ArrayList<>();

        // start a new thread for each canddiate, if enough threads are available
        for (State candidate : candidates) {
            if (nr_threads.updateAndGet(a -> a > 0 ? a - 1 : 0) > 0) {
                List<State> newPath = new ArrayList<>(path);
                newPath.add(candidate);
                FutureTask<SearchResult> futureTask = new FutureTask<>(() -> {
                    return threaded_search(newPath, bound, nr_threads);
                });
                futureTaskList.add(futureTask);
                Thread t = new Thread(futureTask);
                // start the thread
                t.start();
            } else executedByCurrentThread.add(candidate); // if a new thread cannot be created, the candidate will be processed by the current thread
        }

        // execute all the candidates that should be executed by the current thread
        for(State candidate: executedByCurrentThread){
            path.add(candidate);
            SearchResult searchResult = threaded_search(path, bound, nr_threads);
            if(searchResult.found)
                return searchResult;
            if(searchResult.t < min)
                min = searchResult.t;
            path.remove(path.size() - 1);
        }

        // wait for threads to complete execution
        for(FutureTask<SearchResult> futureTask : futureTaskList){
            SearchResult searchResult = futureTask.get();
            nr_threads.incrementAndGet();
            if(searchResult.found)
                return searchResult;
            if(searchResult.t < min)
                min = searchResult.t;
        }

        return new SearchResult(false, min, null);
    }

}
