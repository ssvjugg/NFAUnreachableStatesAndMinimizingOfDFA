import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class State {
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
     * @return List of States that describes NFA
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
     * This function remove unreachable states from NFA that is described by List of States
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

    /**
     *  This function removes unnecessary states from
     * @param states List of States that represents the DFA
     * @return List of States that will represent the minimized DFA
     */
    public static List<State> minimizingDFA(List<State> states) {
        var partitionedStates = states.stream().collect(Collectors.partitioningBy(State::isFinalState, Collectors.toSet()));
        List<Set<State>> partition = List.of(partitionedStates.get(false), partitionedStates.get(true));  // Divided states into two groups
        boolean flag = true;

        while (flag) {
            List<Set<State>> newPartition = new ArrayList<>();

            for (Set<State> part : partition) {
                Map<String, Set<State>> groups = new HashMap<>();
                for (State state : part) {
                    String key = getStateGroup(state, partition);
                    groups.computeIfAbsent(key, k -> new HashSet<>()).add(state);
                }
                newPartition.addAll(groups.values());
            }

            flag = newPartition.size() != partition.size();
            partition = newPartition;
        }

        return createMinimizedDfa(partition);
    }

    private static String getStateGroup(State state, List<Set<State>> partitions) {
        StringBuilder key = new StringBuilder();
        List<String> symbols = new ArrayList<>(state.transitions.keySet());

        for (String symbol : symbols) {
            List<State> nextStates = state.transitions.get(symbol);
            int nextStateIndex = -1;
            for (Set<State> partition : partitions) {
                if (partition.contains(nextStates.get(0))) { // Should be only one for DFA
                    nextStateIndex = partitions.indexOf(partition);
                    break;
                }
            }
            key.append(nextStateIndex);
        }
        return key.toString();
    }

    private static List<State> createMinimizedDfa(List<Set<State>> partitions) {
        List<State> minimizedStates = new ArrayList<>();
        // Remove unnecessary states
        for (Set<State> partition : partitions) {
            State representative = partition.iterator().next();
            State newState = new State(representative.name, representative.isFinalState());
            minimizedStates.add(newState);
        }

        // Make transitions
        for (int i = 0; i < partitions.size(); i++) {
            State representative = partitions.get(i).iterator().next();

            for (var transition : representative.transitions.entrySet()) {
                State nextState = transition.getValue().get(0);
                Set<State> nextStatePartition = partitions.stream()
                        .filter(p -> p.contains(nextState))
                        .findFirst()
                        .orElse(null);
                int nextStateIndex = partitions.indexOf(nextStatePartition);
                minimizedStates.get(i).addTransition(transition.getKey(), minimizedStates.get(nextStateIndex));
            }
        }

        return minimizedStates;
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
