import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/*
    - S 0|A 1|B
    - A 0|E 1|B
    - B 0|D 1|C
    - C 0|A 1|B
    - D 0|F 1|C
    - E 0|J 1|F
    - F 0|I 1|E
    - J 0|G 1|F
    - I 1|E
    - G 0|G 1|G e|H
    f H
 */

//UnreachableStates
public class Main {
    public static void main(String[] args) {
        try {
            var l = State.readNkaFromFile(Paths.get("nka.txt"));
            for (State s : l)
                System.out.println(s);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}