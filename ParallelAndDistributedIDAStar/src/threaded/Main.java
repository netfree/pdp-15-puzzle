package threaded;

import domain.State;
import utils.StateUtils;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        State state = StateUtils.readInitialStateFromFile("initial_state.in");
        System.out.println(state);
    }
}
