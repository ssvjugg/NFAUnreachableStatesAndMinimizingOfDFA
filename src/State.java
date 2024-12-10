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
     * @return List of States
     */
    public static List<State> readNkaFromFile(Path p) throws IOException {
        List<State> states = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] ln = line.split("\\s+");
                State curState;
                if ((curState = containsState(ln[1], states)) == null) {
                    curState = new State(ln[1], "f".equals(ln[0]));
                    states.add(curState);
                }
                else if ("f".equals(ln[0])){
                    curState.finalState = true;
                }

                for (int i = 2; i < ln.length; i++) {
                    String[] transition = ln[i].split("\\|");
                    State tempState;
                    if ((tempState = containsState(transition[1], states)) == null) {
                        tempState = new State(transition[1], false);
                        curState.addTransition(transition[0], tempState);
                        states.add(tempState);
                    }
                    else {
                        curState.addTransition(transition[0], tempState);
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

    public static void removeUnreachableStates(List<State> states) {

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
