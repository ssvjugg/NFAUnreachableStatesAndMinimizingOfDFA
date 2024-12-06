import java.util.*;

public class State {
    private final String name;
    private final boolean finalState;
    private final Map<String, List<State>> transitions;

    public State(String name, boolean finalState) {
        this.name = name;
        this.finalState = finalState;
        transitions = new HashMap<>();
    }

    /**
     * Function that adds transition into Map of transitions from current state
     * @param k symbol that leads to the v
     * @param v state that will be current after transition by symbol k
     */
    public void addTransition(String k, State v) {
        
    }

    public boolean isFinalState() {
        return finalState;
    }

    public String getName() {
        return name;
    }
}
