# AI Algorithms & Exercises

Repository containing implementations of Artificial Intelligence algorithms and search strategies in Java.

## Included Projects:

### 1. Protein Folding (2D HP Model)
- **Goal:** Maximize H-H contacts on a 2D grid (energy minimization).
- **Algorithms:** DFS (Depth-First Search), BFS, MINCOST, A* (A-Star).
- **Heuristics:** Missing Contacts / Potential Contacts.


## Requirements
- Java JDK 11+
- Maven

---

## Usage

### 1. Build the Project
Open a terminal in the project root and run the following Maven command to compile and package the application:

```bash
mvn clean package

java -cp "target/proteinfolding-1.0-SNAPSHOT.jar:libs/searchstateexplorer-framework.jar" com.piera.ProteinFolding --size 9 --algos="A*" --protein="PHHPHPPHP" -v 3