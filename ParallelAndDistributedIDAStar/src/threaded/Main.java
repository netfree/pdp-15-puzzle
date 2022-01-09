package threaded;

import domain.State;
import sequential.SequentialIDAStar;
import utils.StateUtils;

import java.io.FileNotFoundException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        State state = StateUtils.readInitialStateFromFile("initial_state.in");
        List<State> path = SequentialIDAStar.ida_star(state);
        assert path != null;
        path.forEach(System.out::println);
    }
}
