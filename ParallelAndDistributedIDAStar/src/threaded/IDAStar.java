package threaded;
import domain.State;
import domain.ThreadedSearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

public class IDAStar {
    public static List<State> ida_star(State initialState, int nr_threads) throws ExecutionException, InterruptedException {
        int bound = initialState.getH();
        List<State> path = new ArrayList<>();
        path.add(initialState);
        while(true){
            ThreadedSearchResult searchResult = threaded_search(path, bound, new AtomicInteger(nr_threads + 1));
            System.out.println("Bound " + bound + " finished");
            if(searchResult.found)
                return path;
            if(searchResult.t == Integer.MAX_VALUE)
                return null;
            bound = searchResult.t;
        }
    }

    public static ThreadedSearchResult search(List<State> path, int bound){
        State state = path.get(path.size() - 1);
        int f = state.getG() + state.getH();
        if(f > bound)
            return new ThreadedSearchResult(false, f, null);
        if(state.isGoalState())
            return new ThreadedSearchResult(true, -1, path);
        int min = Integer.MAX_VALUE;
        for(State succ: state.getListOfCandidates()){
            if(!path.contains(succ)){
                path.add(succ);
                ThreadedSearchResult searchResult = search(path, bound);
                if(searchResult.found)
                    return searchResult;
                if(searchResult.t < min)
                    min = searchResult.t;
                path.remove(path.size() - 1);
            }
        }
        return new ThreadedSearchResult(false, min, null);
    }

    public static ThreadedSearchResult threaded_search(List<State> path, int bound, AtomicInteger nr_threads) throws ExecutionException, InterruptedException {
        State state = path.get(path.size() - 1);
        int f = state.getG() + state.getH();
        if(f > bound)
            return new ThreadedSearchResult(false, f, null);
        if(state.isGoalState())
            return new ThreadedSearchResult(true, -1, path);
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

        List<FutureTask<ThreadedSearchResult>> futureTaskList = new ArrayList<>();

        // start a new thread for each canddiate, if enough threads are available
        for (State candidate : candidates) {
            if (nr_threads.updateAndGet(a -> a > 0 ? a - 1 : 0) > 0) {
                List<State> newPath = new ArrayList<>(path);
                newPath.add(candidate);
                FutureTask<ThreadedSearchResult> futureTask = new FutureTask<>(() -> {
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
            ThreadedSearchResult searchResult = search(path, bound);
            if(searchResult.found)
                return searchResult;
            if(searchResult.t < min)
                min = searchResult.t;
            path.remove(path.size() - 1);
        }

        // wait for threads to complete execution
        for(FutureTask<ThreadedSearchResult> futureTask : futureTaskList){
            ThreadedSearchResult searchResult = futureTask.get();
            nr_threads.incrementAndGet();
            if(searchResult.found)
                return searchResult;
            if(searchResult.t < min)
                min = searchResult.t;
        }

        return new ThreadedSearchResult(false, min, null);
    }

}
