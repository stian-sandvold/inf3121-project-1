import java.util.Stack;

public class State {

    private boolean goalState;
    private int empty_row, empty_col;
    private byte board[][];
    private int toGoal = 0, fromStart = 0;
    private State parent = null;
    public String direction = "";

    /* Init the State with just a board */
    public State(byte[][] board) {

        populateBoard(board);
        analyseBoard();

    }

    /**
     * State
     *
     * Constructor for a new State, when the State is a child of another State.
     * Calls populateBoard to populate the array
     *
     * @param board a byte[][] array with the current State
     * @param row Which row to move the empty piece
     * @param col Which column to move the empty piece
     * @param direction Which direction the move was made ("U", "D", "L", "R")
     */
    private State(byte[][] board, int row, int col, String direction) {
        /* Populate array */
        populateBoard(board);

        /* Make the last move */
        this.board[empty_row][empty_col] = this.board[row][col];
        this.board[row][col] = (byte)0;

        /* Update 0's position */
        this.empty_row = row;
        this.empty_col = col;

        this.direction = direction;

        analyseBoard();
    }

    /**
     * populateBoard
     * Takes a byte[][] array and copies each value into a new byte[][] array.
     *
     * @param board The board to be copied
     */
    private void populateBoard(byte[][] board) {
        /* New board */
        this.board = new byte[board.length][board.length];

        /* Iterate over all positions */
        for(int row = 0; row < this.board.length; row++) {
            for(int col = 0; col < this.board[row].length; col++) {
                /* Copy the value */
                this.board[row][col] = board[row][col];

                /* Save the 0's pos, and check if it's the goal position for 0 */
                if((char)this.board[row][col] == (byte)0) {
                    this.empty_row = row;
                    this.empty_col = col;
                }
            }
        }
    }

    /**
     * analyseBoard
     *
     * analyses the board, by iterating over all the positions.
     * Assumes this is a goal State, and if it finds any misplaced tiles
     * it falsifies that assumption.
     *
     * Also integrates manhattan\misplaced heuristics to be calculated
     * when iterating over the array and finding misplaced tiles.
     */
    private void analyseBoard() {
        /* Assume this is a goal-state */
        this.goalState = true;

        /* Iterate over all positions */
        for(int row = 0; row < this.board.length; row++) {
            for(int col = 0; col < this.board[row].length; col++) {
                if(!inTheRightPlace(row, col)) {
                    // If it's the wrong number in the wrong position, it's not the goal state.
                    this.goalState = false;

                    /**
                     *  Manhattan heuristics!
                     *
                     *  the right row for goalNum: goal_row = (goalNum - 1) % board.length
                     *  the right col for goalNum: goal_col = (goalNum - 1) - (goal_row * board.length)
                     *
                     *  The cost to go from <row, col> to <goal_row, goal_col>:
                     *  | row - goal_row | + | col - goal_col |
                     **/
                    String heuristics = "manhattan";
                    if(heuristics.equals("manhattan") || heuristics.equals("all")) {

                        if(this.board[row][col] == (byte)0)
                            continue;


                        // The number we have, but should be here
                        int wrongNumber = (int)this.board[row][col];

                        // The position wrongNumber should be in
                        int goal_row = (int) Math.floor(wrongNumber / board.length);
                        int goal_col = (wrongNumber % board.length) - 1;

                        // The cost to get it there
                        int row_cost = row - goal_row;
                        int col_cost = col - goal_col;

                        // Absolute values
                        if(row_cost < 0)
                            row_cost = row_cost * -1;
                        if(col_cost < 0)
                            col_cost = col_cost * -1;

                        // Add the cost of this move to toGoal.
                        this.toGoal += (row_cost + col_cost);
                    }

                    /**
                     * Misplaced piece heuristics!
                     *
                     * adds +1 if we are here.
                     **/
                    if(heuristics.equals("misplaced") || heuristics.equals("all")) {
                        this.toGoal++;
                    }
                }
            }
        }
    }

    /**
     * inTheRightPlace
     *
     * Checks if the position [row][col] has the correct value as a
     * potential goal State. That means 1 should be on [0][0], 2 on [0][1]
     * and so on, until [N-1][N-1] which should have 0.
     *
     * @param row Which row to check [0 -> N-1]
     * @param col Which column to check [0 -> N-1]
     * @return True if the correct number is in this position, false if not.
     */
    private boolean inTheRightPlace(int row, int col) {
        return row == this.board.length - 1 && col == this.board.length - 1 && this.board[row][col] == (byte) 0 || this.board[row][col] == (byte) (((row * this.board.length) + col) + 1);
    }

    /**
     * setCostFromStart
     *
     * Used to set the actual cost from the root\start State
     * to this State in the amount of moves required.
     *
     * @param cost Amount of moves required to reach this state from the root\start State.
     */
    public void setCostFromStart(int cost) {
        this.fromStart = cost;
    }

    /**
     * setParent
     *
     * Sets the parent pointer from this State to the previous State.
     *
     * @param parent The previous\parent State of this State
     */
    public void setParent(State parent) {
        this.parent = parent;
    }

    /**
     * getParent
     *
     * The parent pointer is used to traverse the solution (if it exists)
     *
     * @return A pointer to the parent State, or null if  this is the root State.
     */
    public State getParent() {
        return this.parent;
    }

    /**
     * getPriority
     *
     * returns the priority of the State, based on the cost
     * from the start\root State to this State, and the estimated
     * cost from this State to the goal State.
     *
     * In the A* pseudo-code, this is the f(w) = g(w) + h(w), when this is the State 'w'.
     * For reference: g(w) = g(v) + c(v, w) is set in the
     * A*-algorithm as setCostFromStart(v.getCostFromStart() + 1)
     *
     * @return The sum of the calculated cost from the root State to this State, and the estimated cost from this State to the goal State.
     */
    public int getPriority() {
        return this.getCostFromStart() + this.getCostToGoal();
    }


    /**
     * toString
     *
     * returns a String based on the State's board. The String is unique
     * to this exact board's positions.
     *
     * @return an unique String to identify the State.
     */
    public String toString() {
        String res = "";

        for(int row = 0; row < this.board.length; row++)
            for(int col = 0; col < this.board[row].length; col++)
                res += this.board[row][col] + "";

        return res;
    }


    /**
     * isGoalState
     *
     * Checks if goalState is true, which it is if all the positions have the correct value.
     *
     * @return returns true if the board is a goal State, false if not.
     */
    public boolean isGoalState() {
        return this.goalState;
    }

    /**
     * getCostFromStart
     *
     * This is the actual cost from the start\root State
     * to this State. In this case, the cost is equal to the number
     * of moves made to reach this State.
     * @return The number of moves required to reach this State.
     */
    public int getCostFromStart() {
        return this.fromStart;
    }

    /**
     * getCostToGoal
     *
     * This is the estimate from v state to goal state
     * in the common A* pseudo-code, this is the h(v)
     * where this object is v.
     *
     * Set the static variable useHeuristics to false to negate the effect.
     *
     * @return The cost from this State to the goal State based on heuristics
     */
    public int getCostToGoal() {
        boolean useHeuristics = true;
        return (useHeuristics ? this.toGoal : 0);
    }

    /**
     * getMovesArray
     *
     * Finds moves that are legal, and
     * not the opposite of the last move made.
     *
     * @return State[] array of states reachable from this State.
     **/
    public State[] getMovesArray() {
        /* Using a stack cause it makes a cleaner code */
        Stack<State> res = new Stack<State>();

        /* UP */
        if(empty_row-1 >= 0 && !this.direction.equals("D"))
            res.push(new State(this.board, empty_row-1, empty_col, "U"));

        /* DOWN */
        if(empty_row+1 < board.length && !this.direction.equals("U"))
            res.push(new State(this.board, empty_row+1, empty_col, "D"));

        /* LEFT */
        if(empty_col-1 >= 0 && !this.direction.equals("R"))
            res.push(new State(this.board, empty_row, empty_col-1, "L"));

        /* RIGHT */
        if(empty_col+1 < board.length && !this.direction.equals("L"))
            res.push(new State(this.board, empty_row, empty_col+1, "R"));

        return res.toArray(new State[res.size()]);
    }

}
