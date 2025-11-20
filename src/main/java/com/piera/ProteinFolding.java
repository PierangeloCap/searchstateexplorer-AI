package com.piera;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import it.uniroma1.di.tmancini.teaching.ai.search.*;
import picocli.CommandLine;

public class ProteinFolding extends Problem implements Callable<Integer> {

    @CommandLine.Option(names={"--size"},required = true,
            description = "The size of the protein, as a positive integer number.")
    private int size;


    @CommandLine.Option(names={"--algos", "--algorithms"}, required = true, split=",",
            description = "The algorithms to use, as a double quoted comma-separated list. Valid values are" +
                    "{BFS, DFS, MINCOST,}")
    private String[] algos;

    @CommandLine.Option(names={"--protein"})
    private String protein;

    @CommandLine.Option(names={"-v", "--verbosity"}, defaultValue = "0",
            description = "The verbosity level of the output, as a positive integer number. " +
                    "0 corresponds to 'standard', while higher numbers correspond to higher verbosity.")
    private int vlevel;




    public ProteinFolding() {
        super("ProteinFolding");
    }

    private void checkInput() throws IllegalArgumentException{
        List<String> algorithms = Arrays.asList(
                "bfs",
                "dfs",
                "mincost",
                "a*");
        for (String algoHeur : this.algos) {
            List<String> algoAndHeuristics = getAlgorithmAndHeuristics(algoHeur);
            String algo = algoAndHeuristics.get(0);
            if (!algorithms.contains(algo.toLowerCase())){
                throw new IllegalArgumentException(
                        String.format("[ERROR] Algorithm '%s' is unknown. Please specify one of " +
                                "{'BFS', 'DFS', 'MINCOST'}", algo));
            }
            if (algoAndHeuristics.size() > 1) {
                System.err.println("[WARNING] Heuristics are not yet supported for this problem!%n");
            }
        }

        if (this.size <= 0) {
            throw new IllegalArgumentException(
                    "[ERROR] Negative or null puzzle sizes are not allowed. Please a positive value for option '--size'");
        }
        if (this.vlevel < 0) {
            throw new IllegalArgumentException(
                    "[ERROR] Negative verbosity levels are not allowed. Please a non-negative value for option '--verbosity'");
        }


    }


    @Override
    public Integer call() {
        try {
            this.checkInput();
            ProteinFoldingState initialState = ProteinFoldingState.newInitialState(this);
            System.out.println("Random initial state:\n" + initialState);


            SearchStateExplorer explorer;
            for (String algo : algos) {
                List<String> algoAndSetting = getAlgorithmAndHeuristics(algo);
                String algorithm = algoAndSetting.get(0);
                String setting = null;
                if (algoAndSetting.size() > 1) setting = algoAndSetting.get(1);

                switch (algorithm) {
                    case "bfs":
                        explorer = new BFSExplorer(this);
                        break;
                    case "dfs":
                        explorer = new DFSExplorer(this);
                        break;
                    case "mincost":
                        explorer = new MinCostExplorer(this);
                        break;
                    case "a*":
                        explorer = new AstarExplorer(this);
                        break;
                    default:
                        throw new IllegalStateException("[ERROR] Unknown algorithm: " + algorithm);
                }


                explorer.setVerbosity(SearchStateExplorer.VERBOSITY.values()[vlevel]);

                System.out.println("\n\n\n===================\n\nAlgorithm " + explorer +
                        (setting != null ? " (" + setting + ")" : "") + " started");
                List<Action> result = explorer.run(initialState);

                System.out.println("[INFO] Algorithm " + explorer + " terminated.");
                explorer.outputStats();

                System.out.println("Initial state:\n" + initialState);
                if (result != null) {
                    System.out.println("\n\n\nSolution found by algorithm " + explorer + " (" + result.size() + " actions):\n");
                    int i = 0;
                    for (Action a : result) {
                        System.out.println(" [" + i + "]" + a);
                        i++;
                    }
                } else System.out.printf("\n\n\nNo solution found by algorithm %s%n", explorer);
            }
            return 0;
        } catch (IllegalArgumentException e){
            System.err.println(e.getMessage());
            return 1;
        } catch (IllegalStateException e){
            System.err.println(e.getMessage());
            return 2;
        }
    }

    private List<String> getAlgorithmAndHeuristics(String algo){
        List<String> algoAndSetting = Arrays.asList(algo.split(":"));
        List<String> result = new ArrayList<>();
        result.add(algoAndSetting.get(0).trim().toLowerCase());
        if (algoAndSetting.size() > 1) result.add(algoAndSetting.get(1).trim());
        return result;
    }

    public char[] getProtein() {
        return this.protein.toCharArray();
    }

    public int getSize(){
        return this.size;
    }

    private static void printOutputHeader(){
        System.out.println("\n======================================================"+
                "=\n=\t\tSearchStateExplorer\t\t      =\n" +
                "=\t\t  Exercise: Protein Folding\t\t      =\n" +
                "= Computer Science Dept - Sapienza University of Rome =\n" +
                "=======================================================\n");
    }

    public static void main(String[] args) {
        printOutputHeader();
        int exitCode = new CommandLine(new ProteinFolding()).execute(args);

        System.out.printf("%nExecution completed. Exiting %s errors.%n", exitCode > 0 ? "with" : "without");
        System.exit(exitCode);
    }
    
}
