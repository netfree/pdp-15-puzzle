package threaded;

import domain.State;
import sequential.SequentialIDAStar;
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
        List<State> path = SequentialIDAStar.ida_star(state, 7);
        System.out.println("finished in " + Duration.between(start, Instant.now()).toMillis() + " ms\n");
        assert path != null;
        path.forEach(System.out::println);
    }
}
