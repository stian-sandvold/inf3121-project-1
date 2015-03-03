import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Game {

    private int boardSize;

    /**
     * Constructor for Game
     * Starts solving the problem, and writes solution to given file
     * @param fileIn File to read puzzle from
     * @param fileOut File to write solution to
     * @throws FileNotFoundException
     */
    public Game(String fileIn, String fileOut) throws FileNotFoundException {

        String res = solvePuzzle(new State(readBoardFromFile(fileIn)));
        writeSolutionToFile(fileOut, res);

    }

    /**
     * writeSolutionToFile
     *
     * Writes the solution given to the given file
     *
     * @param fileOut File to write solution to
     * @param res solution to write into the file
     * @throws FileNotFoundException
     */
    private void writeSolutionToFile(String fileOut, String res) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new File(fileOut));
        System.out.println("Solution: \n" + res);

        out.print(res);
        out.close();
    }

    /**
     * readBoardFromFile
     * Reads puzzle from a given file, makes it a String of numbers and returns a call to convertToBoard
     * @param fileName File to read from
     * @return Returns a byte[][] from a call to convertToBoard
     * @throws FileNotFoundException
     */
    private byte[][] readBoardFromFile(String fileName) throws FileNotFoundException {
        Scanner in = new Scanner(new File(fileName));
        boardSize = Integer.parseInt(in.nextLine()); // Size of board
        String input = "";

        // Read rows and append to a single String
        for(int i = 0; i < boardSize; i++)
            input += in.nextLine() + " ";

        in.close();

        /* Remove unnecessary extra spaces */
        return convertToBoard(input.replaceAll("\\s+", " "));

    }

    /**
     * convertToBoard
     *
     * Takes a string of numbers, separated by a single space, and converts it into a 2-dimensional byte array.
     *
     * @param data The String representing the board.
     * @return a byte[][] array representing the board.
     */
    private byte[][] convertToBoard(String data) {
        byte[][] res = new byte[this.boardSize][this.boardSize];
        String[] input = data.split(" ");

        for (int row = 0; row < res.length; row++)
            for (int col = 0; col < res[row].length; col++)
                res[row][col] = (byte)Integer.parseInt(input[((row * this.boardSize) + col)]);

        return res;
    }

    /**
     * solvePuzzle
     *
     * Takes the root state (as argument) and starts the A*-algorithm.
     * Also prints a message to stdout if no solution is found, and the time it took to solve
     * the puzzle in ms (Just for fun\gimmick)
     *
     * @param root The root State of the puzzle.
     * @return The String representing the solution.
     */
    String solvePuzzle(State root) {
        // Just for fun!
        final long startTime = System.currentTimeMillis();

        /* Let the solving begin! */
        AStarResult res = AStarSearch(root);

        // Just for fun!
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + "ms" );


        if(res == null)
            return "No solution.";
        else
            return res.toString();

    }

    /**
     * AStarSearch
     *
     * My own implementation of the A* Algorithm.
     * Based on the pseudo-code in the curriculum book of this subject(INF4130)
     *
     * Only using HashMap for checking if a state have been visited; Using the State object
     * to keep track of paths with parent pointers.
     *
     * Using the java standard PriorityQueue, with an initial size of N*N and a custom Comparator
     * that uses State.getPriority() to compare States priority.
     *
     * Although it's based on the book, I made changes to fit my own implementation.
     * Since the algorithm is explained in the book, I will only comment a few parts of this method
     * and not the entire thing.
     *
     * I'm not using a set of goal states(or a reference to a goal-state in this case) as an
     * argument in this implementation, since I made my own way to check for a goal-state in
     * State class.
     *
     * @param start The inital State to start the search
     * @return AStarResult object, a custom object for storing the result info, or null if no solution can be found.
     */
    private AStarResult AStarSearch(State start) {
        PriorityQueue<State> queue = new PriorityQueue<State>((this.boardSize * this.boardSize), new Comparator<State>() {
            @Override
            public int compare(State o1, State o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });

        HashMap<String, State> seenStates = new HashMap<String, State>();
        // I'm counting the initial State as a new State, since the examples on the subject page and the examples in the oblig-text differs; This seems more right.
        int statesVisited = 0, newStates = 1, editStates = 0;

        queue.add(start);

        while (!queue.isEmpty()) {
            State current = queue.poll(); // Get the next State to examine
            State[] moves = current.getMovesArray(); // Get the moves available from this State

            seenStates.put(current.toString(), current); // Add this State to seenStates
            statesVisited++;

            // Check if we found a goal state
            if (current.isGoalState())
                return new AStarResult(current, statesVisited, newStates, editStates); // Create a custom object to store the solution info

            for (State move : moves) {

                // Check if we have been to thisState before.
                if (seenStates.containsKey(move.toString()))
                    continue;

                if (!queue.contains(move)) {
                    move.setParent(current);
                    move.setCostFromStart(current.getCostFromStart() + 1);

                    queue.add(move);
                    newStates++;
                } else {
                    if (move.getPriority() >= current.getCostFromStart() + 1 + move.getCostToGoal()) {
                        move.setParent(current);
                        move.setCostFromStart(current.getCostFromStart() + 1);

                        // Update the priority
                        queue.remove(move);
                        queue.add(move);

                        editStates++;
                    }
                }
            }
        }
        return null;
    }

    /**
     * solutionToString
     *
     * Takes a State and traverse it back to the root, storing the
     * moves as it goes.
     *
     * Using an iterative solution, since I encountered a StackOverflow during testing.
     * Makes sense to use it on difficult puzzles(with a long solution, or a large board)
     *
     * @param state The GOAL State
     * @return A String with the initials of the directions used to solve the puzzle
     */
    private String solutionToString(State state) {
        State temp = state;
        String res = "";

        while(temp.getParent() != null) {
            res = temp.direction + res;
            temp = temp.getParent();
        }

        return res;
    }

    /**
     * AStarResult
     *
     * Custom class to store the solution
     */
    private class AStarResult {
        public final int visitedStates;
        public final int newStates;
        public final int editedStates;
        public final String solution;

        /**
         * The constructor for AStarResult
         * @param current The last State found in the AStarSearch
         * @param statesVisited Number of states visited
         * @param newStates Number of states found
         * @param editStates Number of states that changed priority
         */
        public AStarResult(State current, int statesVisited, int newStates, int editStates) {
            this.visitedStates = statesVisited;
            this.newStates = newStates;
            this.editedStates = editStates;
            this.solution = solutionToString(current);
        }

        /**
         * toString
         *
         * What is the final result when writing the solution.
         *
         * @return The final solution in the required format.
         */
        public String toString() {
            String res = "";

            res += solution.length() + "\n";
            res += solution + "\n";
            res += visitedStates + "\n";
            res += newStates + "\n";
            res += editedStates + "\n";

            return res;
        }
    }
}