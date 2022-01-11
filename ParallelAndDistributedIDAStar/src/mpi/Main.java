package mpi;
import domain.DistributedSearchResult;
import domain.State;
import utils.StateUtils;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class Main {

    public static void main(String[] args) throws IOException {
        MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank();
        if (me == 0) {
            State state = StateUtils.readInitialStateFromFile("initial_state.in");
            main_worker(state);
        } else {
            worker_search();
        }
        MPI.Finalize();
    }

    private static void main_worker(State initial_state) {
        int size = MPI.COMM_WORLD.Size();
        int workers = size - 1;
        int bound = initial_state.getH();
        boolean found = false;
        long time = System.currentTimeMillis();

        Queue<State> queue = new LinkedList<>();
        queue.add(initial_state);
        while (true) {
            assert queue.peek() != null;
            if (!(queue.size() + queue.peek().getListOfCandidates().size() - 1 <= workers)) break;
            State candidate = queue.poll();
            assert candidate != null;
            queue.addAll(candidate.getListOfCandidates());
        }

        while (!found) {
            Queue<State> temp = new LinkedList<>(queue);
            for (int i = 0; i < queue.size(); i++) {
                State candidate = temp.poll();
                MPI.COMM_WORLD.Send(new boolean[]{false}, 0, 1, MPI.BOOLEAN, i + 1, 0);
                MPI.COMM_WORLD.Send(new Object[]{candidate}, 0, 1, MPI.OBJECT, i + 1, 0);
                MPI.COMM_WORLD.Send(new int[]{bound}, 0, 1, MPI.INT, i + 1, 0);
            }

            Object[] searchResults = new Object[size];

            for (int i = 1; i <= queue.size(); i++) {
                MPI.COMM_WORLD.Recv(searchResults, i - 1, 1, MPI.OBJECT, i, 0);
            }

            int newBound = Integer.MAX_VALUE;

            for (int i = 0; i < queue.size(); i++) {
                DistributedSearchResult p = (DistributedSearchResult) searchResults[i];
                if (p.getMin() == -1) {
                    System.out.println("Reached goal state: ");
                    System.out.println(p.getState());
                    System.out.println("in " + (System.currentTimeMillis() - time) + "ms");
                    found = true;
                    break;
                } else if (p.getMin() < newBound) {
                    newBound = p.getMin();
                }
            }
            if(!found){
                System.out.println("Bound " + bound + " finished");
                bound = newBound;
            }
        }

        for (int i = 1; i < size; i++) {
            State candidate = queue.poll();
            MPI.COMM_WORLD.Send(new boolean[]{true}, 0, 1, MPI.BOOLEAN, i, 0);
            MPI.COMM_WORLD.Send(new Object[]{candidate}, 0, 1, MPI.OBJECT, i, 0);
            MPI.COMM_WORLD.Send(new int[]{bound}, 0, 1, MPI.INT, i, 0);
        }
    }

    private static void worker_search() {
        while (true) {
            Object[] state = new Object[1];
            int[] bound = new int[1];
            boolean[] found = new boolean[1];

            MPI.COMM_WORLD.Recv(found, 0, 1, MPI.BOOLEAN, 0, 0);
            MPI.COMM_WORLD.Recv(state, 0, 1, MPI.OBJECT, 0, 0);
            MPI.COMM_WORLD.Recv(bound, 0, 1, MPI.INT, 0, 0);

            if (found[0]) {
                return;
            }

            int minBound = bound[0];
            State candidate = (State) state[0];
            DistributedSearchResult result = search(candidate, minBound);
            MPI.COMM_WORLD.Send(new Object[]{result}, 0, 1, MPI.OBJECT, 0, 0);
        }
    }

    public static DistributedSearchResult search(State current, int bound) {
        int estimation = current.getG() + current.getH();
        if (estimation > bound) {
            return new DistributedSearchResult(estimation, current);
        }
        if (estimation > 80) {
            return new DistributedSearchResult(estimation, current);
        }
        if (current.getH() == 0) {
            return new DistributedSearchResult(-1, current);
        }
        int min = Integer.MAX_VALUE;

        State ans = null;

        for (State next : current.getListOfCandidates()) {
            DistributedSearchResult result = search(next, bound);
            int t = result.getMin();
            if (t == -1) {
                return new DistributedSearchResult(-1, result.getState());
            }
            if (t < min) {
                ans = result.getState();
                min = t;
            }
        }
        return new DistributedSearchResult(min, ans);
    }

}