import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            var l = State.readNkaFromFile(Paths.get("nka.txt"));
            for (State s : l)
                System.out.println(s);
            System.out.println();
            State.removeUnreachableStates(l);
            l.forEach(System.out::println);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}