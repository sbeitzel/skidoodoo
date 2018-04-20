package com.rkoutnik.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/*
 *Changelog
 * Added two new methods to search for doubletons.
 * 	Neither method will find an answer, just reduce the possibles
 * Added equals method.
 * 	It's a little odd, but works
 * Improved justGuess
 * 	It can now detect if a board has multiple solutions.
 */

/**
 * Attempts to solve a given Sudoku puzzle in a
 * number of ways.
 * <p>
 * Called "Perverse" because of the way possible is stored.
 */
public class PerverseSudokuSolver {
    /**
     * Calls the {@link #init()} function to reset the board
     */
    public PerverseSudokuSolver() {
        sg = new SudokuGenerator();
        init();
    }

    /**
     * Each method checks (in different ways) to see if it
     * can find a new number
     * If said method does find a number, it starts back at
     * the beginning, and sets off a chain reaction
     *
     * @return true if the board is solved, false otherwise
     */
    public boolean solve() {
        int countdown = 20;
        while (!solved() && --countdown > 0) {
            if (given()) {
                continue;
            }
            if (findSingletons()) {
                continue;
            }
            if (doubles()) {
                continue;
            }
            if (doubletons()) {
                continue;
            }
            justGuess();
        }
        if (!solved()) {
            System.out.println(zerosLeft());
        }
        return solved();
    }

    /**
     * Iterates through every given number
     *
     * @return true if it found a new number, false otherwise
     */
    public boolean given() {
        boolean repeat = false;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!found[i][j]) {
                    if (board[i][j] != 0) {
                        repeat = true;
                        foundNum(i, j, board[i][j]);
                    } else if (possible[i][j].size() == 1) {
                        repeat = true;
                        foundNum(i, j, possible[i][j].get(0).intValue());
                    }
                }
            }
        }
        return repeat;
    }

    private void singletonSliceIterate(int row, int col, int[] values) {
        for (int k=0; k<possible[row][col].size(); k++) {
            values[possible[row][col].get(k).intValue()]++;
        }
    }

    private boolean markFoundInSlice(boolean didWork, int row, int col, Collection<Integer> singletons) {
        for (Integer singleton : singletons) {
            if (possible[row][col].contains(singleton) && !found[row][col]) {
                foundNum(row, col, singleton.intValue());
                didWork = true;
            }
        }
        return didWork;
    }

    /**
     * Given a single row (or column), look for cells with only one possible value.
     *
     * @param didWork has any work already been done in the current iteration context
     * @param singletons collection to hold all the singletons found
     * @param values some kind of storage
     * @param sliceIndex row (or column) index
     * @param isRow if <code>true</code>, the slice is a row; otherwise, it's a column
     * @return <code>true</code> if we've found any singles in this pass || didWork was already true
     */
    private boolean findSingletonBySlice(boolean didWork, List<Integer> singletons, int[] values, int sliceIndex, boolean isRow) {
        if (isRow) {
            for (int col=0; col<9; col++) {
                singletonSliceIterate(sliceIndex, col, values);
            }
        } else {
            for (int row=0; row<9; row++) {
                singletonSliceIterate(row, sliceIndex, values);
            }
        }

        for (int j = 1; j < 10; j++) {
            if (values[j] == 1) {
                singletons.add(Integer.valueOf(j));
            }
        }

        if (isRow) {
            for (int col=0; col<9; col++) {
                didWork = markFoundInSlice(didWork, sliceIndex, col, singletons);
            }
        } else {
            for (int row=0; row<9; row++) {
                didWork = markFoundInSlice(didWork, row, sliceIndex, singletons);
            }
        }

        return didWork;
    }

    /**
     * Iterates through r/c/s, attempting to find an instance where
     * a number appears only once in possible.
     *
     * @return true if it found a new number, false otherwise
     */
    public boolean findSingletons() {
        boolean repeat = false;
        //LOTS of iteration, but I'm out of ideas.
        int[] values;
        ArrayList<Integer> singletons = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            values = new int[10];
            singletons.clear();
            repeat = findSingletonBySlice(repeat, singletons, values, i, true);
        }

        for (int i = 0; i < 9; i++) {
            values = new int[10];
            singletons.clear();
            repeat = findSingletonBySlice(repeat, singletons, values, i, false);
        }

        int[] corners = {0, 3, 6};
        for (int a = 0; a < 3; a++) {
            for (int l = 0; l < 3; l++) {
                for (int i = corners[a]; i < corners[a] + 3; i++) {
                    values = new int[10];
                    singletons.clear();
                    for (int j = corners[l]; j < corners[l] + 3; j++) {
                        for (int k = 0; k < possible[i][j].size(); k++) {
                            values[possible[i][j].get(k).intValue()]++;
                        }
                    }
                    for (int j = 1; j < 10; j++) {
                        if (values[j] == 1) {
                            singletons.add(Integer.valueOf(j));
                        }
                    }
                    for (int j = 0; j < 9; j++) {
                        for (Integer singleton : singletons) {
                            if (possible[i][j].contains(singleton) && !found[i][j]) {
                                foundNum(i, j, singleton.intValue());
                                repeat = true;
                            }
                        }
                    }
                }
            }
        }
        return repeat;
    }

    /**
     * Doubleton: Use the same principle of singletons, except looking for doubles
     * If two cells only contain the same two numbers, then those
     * two numbers can be eliminated from the other cells in the block.
     *
     * @return true if it found a new number, false otherwise
     */
    public boolean doubles() {
        boolean repeat = false;
        ArrayList<Integer> possDoubs = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            possDoubs.clear();
            for (int j = 0; j < 9; j++) {
                if (possible[i][j].size() == 2 && !foundDouble[i][j]) {
                    possDoubs.add(Integer.valueOf(j));
                }
            }
            for (int j = 0; j < possDoubs.size() - 1; j++) {
                for (int k = j + 1; k < possDoubs.size(); k++) {
                    if (possible[i][possDoubs.get(j).intValue()].equals(possible[i][possDoubs.get(k).intValue()])) {
                        foundDouble[i][possDoubs.get(j).intValue()] = true;
                        foundDouble[i][possDoubs.get(k).intValue()] = true;
                        int zero = possible[i][possDoubs.get(j).intValue()].get(0).intValue();
                        int one = possible[i][possDoubs.get(j).intValue()].get(1).intValue();
                        for (int q = 0; q < 9; q++) {
                            if (q == possDoubs.get(j).intValue() || q == possDoubs.get(k).intValue()) {
                                continue;
                            } else {
                                // TODO next pass, this can be simplified as well
                                if (possible[i][q].indexOf(Integer.valueOf(zero)) != -1) {
                                    possible[i][q].remove(Integer.valueOf(zero));
                                }
                                if (possible[i][q].indexOf(Integer.valueOf(one)) != -1) {
                                    possible[i][q].remove(Integer.valueOf(one));
                                }
                            }
                        }
                        repeat = true;
                    }
                }
            }
        }

        for (int i = 0; i < 9; i++) {
            possDoubs.clear();
            for (int j = 0; j < 9; j++) {
                if (possible[j][i].size() == 2 && !foundDouble[j][i]) {
                    possDoubs.add(Integer.valueOf(j));
                }
            }

            for (int j = 0; j < possDoubs.size() - 1; j++) {
                for (int k = j + 1; k < possDoubs.size(); k++) {
                    if (possible[possDoubs.get(j).intValue()][i].equals(possible[possDoubs.get(k).intValue()][i])) {
                        foundDouble[possDoubs.get(j).intValue()][i] = true;
                        foundDouble[possDoubs.get(k).intValue()][i] = true;
                        if (possible[possDoubs.get(j).intValue()][i].size() == 1) {
                            print();
                            System.out.println(j + " " + k);
                        }
                        int zero = possible[possDoubs.get(j).intValue()][i].get(0).intValue();
                        int one = possible[possDoubs.get(j).intValue()][i].get(1).intValue();
                        for (int q = 0; q < 9; q++) {
                            if (q == possDoubs.get(j).intValue() || q == possDoubs.get(k).intValue()) {
                                continue;
                            } else {
                                // TODO next pass, simplify this
                                if (possible[q][i].indexOf(Integer.valueOf(zero)) != -1) {
                                    possible[q][i].remove(Integer.valueOf(zero));
                                }
                                if (possible[q][i].indexOf(Integer.valueOf(one)) != -1) {
                                    possible[q][i].remove(Integer.valueOf(one));
                                }
                            }
                        }
                        repeat = true;
                    }
                }
            }
        }

        int[] corners = {0, 3, 6};
        for (int a = 0; a < 3; a++) {
            for (int l = 0; l < 3; l++) {
                for (int i = corners[a]; i < corners[a] + 3; i++) {
                    possDoubs.clear();
                    for (int j = corners[l]; j < corners[l] + 3; j++) {
                        if (possible[i][j].size() == 2 && !foundDouble[i][j]) {
                            possDoubs.add(Integer.valueOf(i));
                            possDoubs.add(Integer.valueOf(j));
                        }
                    }
                    for (int j = 0; j < possDoubs.size() - 1; j += 2) {
                        for (int k = j + 2; k < possDoubs.size(); k += 2) {
                            if (possible[possDoubs.get(j).intValue()][possDoubs.get(j + 1).intValue()]
                                    .equals(possible[possDoubs.get(k).intValue()][possDoubs.get(k + 1).intValue()])) {
                                foundDouble[possDoubs.get(j).intValue()][possDoubs.get(j + 1).intValue()] = true;
                                foundDouble[possDoubs.get(k).intValue()][possDoubs.get(k + 1).intValue()] = true;
                                int zero = possible[possDoubs.get(j).intValue()][possDoubs.get(j + 1).intValue()].get(0).intValue();
                                int one = possible[possDoubs.get(j).intValue()][possDoubs.get(j + 1).intValue()].get(1).intValue();
                                for (int q = corners[a]; q < corners[a] + 3; q++) {
                                    for (int r = corners[l]; r < corners[l] + 3; r++) {
                                        if (q == possDoubs.get(j).intValue() && r == possDoubs.get(j + 1).intValue()) {
                                            continue;
                                        }
                                        if (q == possDoubs.get(k).intValue() && r == possDoubs.get(k + 1).intValue()) {
                                            continue;
                                        } else {
                                            // TODO next pass, simplify
                                            if (possible[i][q].indexOf(Integer.valueOf(zero)) != -1) {
                                                possible[i][q].remove(Integer.valueOf(zero));
                                            }
                                            if (possible[i][q].indexOf(Integer.valueOf(one)) != -1) {
                                                possible[i][q].remove(Integer.valueOf(one));
                                            }
                                        }
                                    }
                                }
                                repeat = true;
                            }
                        }
                    }
                }
            }
        }

        return repeat;
    }


    /**
     * Second round of doubletons.  This time,
     * If two cells both contain the same two numbers /and the numbers do not appear elsewhere in the block/
     * The possible numbers in those two cells can be reduced down to those two.
     *
     * @return true if it found a new number, false otherwise
     */
    public boolean doubletons() {
        boolean result = false;
        //Compare every cell to every other one
        //If there are two numbers in common, check if the numbers appear elsewhere.
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = j + 1; k < 9; k++) {
                    ArrayList<Integer> common = inCommon(possible[i][j], possible[i][k]);
                    if (common.size() == 2) {
                        boolean checkOthers = true;
                        for (int l = 0; l < 9; l++) {
                            if (l == j || l == k) {
                                continue;
                            }
                            if (possible[i][l].contains(common.get(0)) || possible[i][l].contains(common.get(1))) {
                                checkOthers = false;
                                break;
                            }
                        }
                        if (checkOthers) {
                            possible[i][j] = new ArrayList<Integer>(common);
                            possible[i][k] = new ArrayList<Integer>(common);
                            result = true;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = i + 1; k < 9; k++) {
                    ArrayList<Integer> common = inCommon(possible[j][i], possible[j][k]);
                    if (common.size() == 2) {
                        boolean checkOthers = true;
                        for (int l = 0; l < 9; l++) {
                            if (l == i || l == k) {
                                continue;
                            }
                            if (possible[j][l].contains(common.get(0)) || possible[j][l].contains(common.get(1))) {
                                checkOthers = false;
                                break;
                            }
                        }
                        if (checkOthers) {
                            possible[j][i] = new ArrayList<Integer>(common);
                            possible[j][k] = new ArrayList<Integer>(common);
                            result = true;
                        }
                    }
                }
            }
        }
        //And this is where I started to loathe Sudoku
        //I'm wondering how I could write an Iterator to replace a lot of this.
        //There are three iterations through the square
        //1.) i/j = Original cell
        //2.) k/l = Cell we're comparing Original to
        //3.) m/n = Checking other cells to see if they contain the same numbers as i/j or k/l
        int[] corners = {0, 3, 6};
        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                for (int i = corners[a]; i < corners[a] + 3; i++) {
                    for (int j = corners[b]; j < corners[b] + 3; j++) {
                        int k = i;
                        int l = j;
                        if (j % 3 == 2) {
                            if (i % 3 == 2) {
                                continue;
                            }
                            k++;
                            l = corners[b];
                        }

                        for (; k < corners[a] + 3; k++) {
                            for (; l < corners[b] + 3; l++) {
                                ArrayList<Integer> common = inCommon(possible[i][j], possible[k][l]);
                                if (common.size() == 2) {
                                    boolean checkOthers = true;

                                    for (int m = corners[a]; m < corners[a] + 3; m++) {
                                        for (int n = corners[b]; n < corners[b] + 3; n++) {
                                            //This could be one if statement, it's been broken up for readability
                                            if (m == i && n == j) {
                                                continue;
                                            }
                                            if (m == k && n == l) {
                                                continue;
                                            }
                                            if (possible[m][n].contains(common.get(0)) || possible[m][n]
                                                    .contains(common.get(1))) {
                                                checkOthers = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (checkOthers) {
                                        possible[i][j] = new ArrayList<Integer>(common);
                                        possible[k][l] = new ArrayList<Integer>(common);
                                        result = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private ArrayList<Integer> inCommon(ArrayList<Integer> one, ArrayList<Integer> two) {
        ArrayList<Integer> common = new ArrayList<Integer>();
        for (Integer anOne : one) {
            for (Integer aTwo : two) {
                if (anOne.equals(aTwo)) {
                    common.add(anOne);
                }
            }
        }
        return common;
    }

    public void justGuess() {
        ArrayList<PerverseSudokuSolver> solved = new ArrayList<PerverseSudokuSolver>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0 && !possible[i][j].isEmpty()) {
                    for (int k = 0; k < possible[i][j].size(); k++) {
                        PerverseSudokuSolver guess = new PerverseSudokuSolver();
                        //Something interesting happened here.  I gave it this board, but reset possible
                        //It came up with a different answer.  I should follow this up sometime.
                        guess.board = this.board;
                        guess.possible = this.possible;
                        guess.foundNum(i, j, possible[i][j].get(k).intValue());
                        if (guess.solve() && !solved.isEmpty()) {
                            boolean notIncluded = true;
                            for (int l = 0; l < solved.size(); l++) {
                                if (solved.get(k).equals(guess)) {
                                    notIncluded = false;
                                }
                            }
                            if (notIncluded) {
                                solved.add(guess);
                            }
                        } else if (guess.solve()) {
                            solved.add(guess);
                        }
                    }
                }
            }
        }
        if (solved.size() > 1) {
            System.err.println("Board contains multiple solutions");
			/*
			throw new RuntimeException("Board contiains multiple solutions");
			*/
        }
        if (!solved.isEmpty()) {
            this.board = solved.get(0).board;
        }
    }

    public void foundNum(int x, int y, int numFound) {

        if (board[x][y] != 0 && board[x][y] != numFound) {
            throw new RuntimeException("Attempting to place a number where one was already found");
        }

        board[x][y] = numFound;
        possible[x][y].clear();
        possible[x][y].add(Integer.valueOf(numFound));
        found[x][y] = true;

        for (int i = 0; i < 9; i++) {
            if (i != x) {
                if (possible[i][y].indexOf(Integer.valueOf(numFound)) != -1) {
                    possible[i][y].remove(Integer.valueOf(numFound));
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            if (i != y) {
                if (possible[x][i].indexOf(Integer.valueOf(numFound)) != -1) {
                    possible[x][i].remove(Integer.valueOf(numFound));
                }
            }
        }
        int cornerX = 0;
        int cornerY = 0;
        if (x > 2) {
            if (x > 5) {
                cornerX = 6;
            } else {
                cornerX = 3;
            }
        }
        if (y > 2) {
            if (y > 5) {
                cornerY = 6;
            } else {
                cornerY = 3;
            }
        }
        for (int i = cornerX; i < 10 && i < cornerX + 3; i++) {
            for (int j = cornerY; j < 10 && j < cornerY + 3; j++) {
                if (i != x && j != y) {
                    if (possible[i][j].indexOf(Integer.valueOf(numFound)) != -1) {
                        possible[i][j].remove(Integer.valueOf(numFound));
                    }
                }
            }
        }
    }

    public boolean solved() {
        SudokuChecker sc = new SudokuChecker(board);
        return sc.completed() && sc.checkPuzzle();
    }

    /**
     * Accessor for the board, intended for use by testing fixtures
     *
     * @return the board
     */
    public int[][] getBoard() {
        return board;
    }

    public void reset(int[][] board) {
        this.board = board;
        init();
    }

    public void reset(int diff) {
        this.board = sg.nextBoard(diff);
        init();
    }

    public void init() {
        possible = new ArrayList[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                possible[i][j] = new ArrayList<Integer>();
                for (int k = 1; k < 10; k++) {
                    possible[i][j].add(Integer.valueOf(k));
                }
            }
        }
        found = new boolean[9][9];
        foundDouble = new boolean[9][9];
    }

    public void print() {
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("-  -  -  | -  -  -  |  -  -  -");
            }
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0 & j != 0) {
                    System.out.print("| ");
                }
                System.out.print(board[i][j] + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private int zerosLeft() {
        int empty = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    empty++;
                }
            }
        }
        return empty;
    }

    private void data(int difficulty) {
        int empty = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    empty++;
                }
            }
        }
        System.out.println(empty);
    }

    /**
     * equals
     * compares the boards of two solvers
     *
     * @return 0 if the boards are different, 1 otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PerverseSudokuSolver)) {
            return false;
        }
        PerverseSudokuSolver otherSudoku = (PerverseSudokuSolver) other;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.board[i][j] != otherSudoku.board[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        SudokuGenerator sg = new SudokuGenerator();
        PerverseSudokuSolver ss = new PerverseSudokuSolver();
        int one = 1;
        while (one > 0) {
            ss.reset(sg.nextBoard(35));
            System.out.println(ss.solve());
        }
        ss.print();
        ss.data(35);
    }

    int[][] board;
    ArrayList<Integer>[][] possible;
    boolean[][] found;
    boolean[][] foundDouble;
    SudokuGenerator sg;

    @Override
    public int hashCode() {
        int result = Objects.hash(sg);
        result = 31 * result + Arrays.hashCode(getBoard());
        return result;
    }
}
