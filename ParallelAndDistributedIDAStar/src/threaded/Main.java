package threaded;

import domain.State;
import utils.StateUtils;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {
    static Instant start;

    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
        State state = StateUtils.readInitialStateFromFile("initial_state.in");
        start = Instant.now();
        List<State> path = IDAStar.ida_star(state, 0);
        System.out.println("sequential finished in " + Duration.between(start, Instant.now()).toMillis() + " ms\n");
        assert path != null;
        start = Instant.now();
        path = IDAStar.ida_star(state, 5);
        System.out.println("5 threads finished in " + Duration.between(start, Instant.now()).toMillis() + " ms\n");
        assert path != null;
        start = Instant.now();
        path = IDAStar.ida_star(state, 8);
        System.out.println("8 threads finished in " + Duration.between(start, Instant.now()).toMillis() + " ms\n");
        assert path != null;
        path = IDAStar.ida_star(state, 100);
        System.out.println("100 threads finished in " + Duration.between(start, Instant.now()).toMillis() + " ms\n");
        assert path != null;
        System.out.println(path.toString());
    }
}
