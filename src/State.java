import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class State {
    private final String name;
    private boolean finalState;
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
        transitions.computeIfAbsent(k, key -> new ArrayList<>()).add(v);
    }

    public boolean isFinalState() {
        return finalState;
    }

    public String getName() {
        return name;
    }

    /**
     * This function read from states from file
     * @param p Path to file in system
     * @return List of States that describes NKA
     */
    public static List<State> readNkaFromFile(Path p) throws IOException {
        List<State> states = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+");
                State currentState;
                if ((currentState = containsState(parts[1], states)) == null) {
                    currentState = new State(parts[1], "f".equals(parts[0]));
                    states.add(currentState);
                }
                else if ("f".equals(parts[0])){
                    currentState.finalState = true;
                }

                for (int i = 2; i < parts.length; i++) {
                    String[] transition = parts[i].split("\\|");
                    State tempState;
                    if ((tempState = containsState(transition[1], states)) == null) {
                        tempState = new State(transition[1], false);
                        currentState.addTransition(transition[0], tempState);
                        states.add(tempState);
                    }
                    else {
                        currentState.addTransition(transition[0], tempState);
                    }
                }
            }
        }
        return states;
    }

    private static State containsState(String name, List<State> lst) {
        for (State s : lst) {
            if (name.equals(s.getName()))
                return s;
        }
        return null;
    }

    /**
     * This function remove unreachable states from NKA that is described by List of States
     * @param states List of States that will be modified after applying this function
     */
    public static void removeUnreachableStates(List<State> states) {
        if (states.isEmpty()) throw new IllegalStateException("List must contains at least one state");
        Set<State> reachableStates = new LinkedHashSet<>();
        reachableStates.add(states.get(0)); // Added start state
        boolean loopFlag = true;
        while (loopFlag) {
            loopFlag = false;
            List<State> statesToAdd = new ArrayList<>();
            for (State st : reachableStates) {
                for (List<State> transitions : st.transitions.values()) {
                    for (State transition : transitions) {
                        if (!reachableStates.contains(transition)) {
                            statesToAdd.add(transition);
                            loopFlag = true;
                        }
                    }
                }
            }
            reachableStates.addAll(statesToAdd);
        }
        states.removeIf(st -> !reachableStates.contains(st));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(isFinalState() ? "f " : "- ").append(name);
        for (var t : transitions.entrySet()) {
            for (var s : t.getValue())
                sb.append(String.format(" %s|%s", t.getKey(), s.name));
        }
        return sb.toString();
    }
}
