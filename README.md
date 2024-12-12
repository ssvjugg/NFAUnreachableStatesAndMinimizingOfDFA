# NKAUnreachableStates
This project implements the removal of unreachable states from an undetermined finite state machine (NKA).

---

### The example of input file:

---
\- S 0|A 1|B  
\- A 0|E 1|B  
\- B 0|D 1|C  
\- E 0|J 1|F  
\- D 0|F 1|C  
\- C 0|A 1|B  
\- F 0|I 1|E  
\- J 0|G 1|F  
\- I 1|E  
\- G 0|G 1|G e|H  
 f H  
---
*In the file, each state should be on a separate line with its transitions as in the example. It is also desirable that the first state be the initial one. Also "-" means that state is not final, "f" means final.*


