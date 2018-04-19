package com.qbcps.sudoku;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rkoutnik.sudoku.SudokuChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backtracking sudoku solver
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class QBSolver implements Solver {
    private static final Logger __l = LoggerFactory.getLogger(QBSolver.class);

    @Override
    public int[][] getSolution(int[][] puzzle) throws NoSolutionFoundException {
        try {
            Board b = new Board(puzzle);
            return solve(b);
        } catch (NoSolutionFoundException nsfe) {
            __l.debug("Board failed with state:\n"+Board.render(nsfe.getPuzzle()), nsfe);
            throw nsfe;
        } catch (Exception e) {
            __l.debug("Solution failed", e);
            throw new NoSolutionFoundException(puzzle, "Exception thrown during solving", e);
        }
    }

    private int[][] solve(Board b) throws NoSolutionFoundException {
        // this is a single pass at collapsing possibilities.
        // 1. any cell with a single possibility should be set
        boolean valueFound, workDone = true;
        int loopCount = 1;
        while (true) {
            __l.debug("Starting loop "+(loopCount++));
            workDone = false;
            int singles = 0;
            do {
                valueFound = false;
                for (Cell c : b) {
                    if (c.getPossibles().size() == 1) {
                        valueFound = true;
                        c.setValue(c.getPossibles().get(0).intValue());
                        singles++;
                    }
                }
            } while (valueFound);
            __l.debug("Found "+singles+" cells with only a single possibility");
            // do an integrity check of the board
            b.integrityCheck();

            // at this point, we have no unset cells with only one possibility.
            // 2a. check to see if we've solved it
            int[][] state = b.getBoard();
            SudokuChecker checker = new SudokuChecker(state);
            if (checker.completed()) {
                __l.debug("Puzzle completed");
                if (checker.checkPuzzle()) {
                    __l.debug("Puzzle solved");
                    return state;
                }
                __l.debug("Invalid solution");
                // we've got a number in every square but it isn't a valid solution
                throw new NoSolutionFoundException(state);
            }
            __l.debug("Not yet completed");
            // 2b. look for doubles, where cell1 and cell2 in a group only have the same two possibilities. In which case, remove those possibilities from the rest of the group's cells
            for (CellGroup g : b.getGroups()) {
                List<Cell> cells = g.getCells();
                for (int p1 = 0; p1 < 8; p1++) {
                    Cell first = cells.get(p1);
                    if (first.getPossibles().size() == 2) {
                        for (int p2 = p1+1; p2 < 9; p2++) {
                            Cell second = cells.get(p2);
                            if (second.getPossibles().size() == 2 && second.getPossibles().containsAll(first.getPossibles())) {
                                // we know that the other cells in the group can't have these possibilities
                                workDone = g.removePossibilities(Arrays.asList(first, second), first.getPossibles()) || workDone;
                            }
                        }
                    }
                }
            }
            // 3. if no work was done, then we need to guess. This is where recursion/backtracking happens.
            // if work was done, we should loop back up to the top
            if (!workDone) {
                __l.debug("Welp, we need to guess");
                return guess(b);
            }
        }
    }

    private int[][] guess(Board b) {
        __l.debug("Starting a guess");
        for (Cell c : b) {
            // find a cell with possibilities
            if (c.getPossibles().size()>1) {
                __l.debug("Found a cell with multiple possibilities at row "+c.getRow()+", column "+c.getCol());
                ArrayList<Integer> workingList = new ArrayList<>(c.getPossibles());
                for (Integer v : workingList) {
                    // pick one of the possible values
                    Board b2 = new Board(b);
                    b2.getCell(c.getRow(), c.getCol()).setValue(v.intValue());
                    // try to solve the new board
                    try {
                        // if we get a solution, great, return it
                        __l.debug("Guessing value "+v);
                        return solve(b2);
                    } catch (NoSolutionFoundException nsfe) {
                        // if we can't solve the board that way, then remove that possibility from the cell and try the next value
                        c.removePossibility(v.intValue());
                    }
                }
            }
        }
        // if we have tried all the possible values for the remaining cells and we still can't find a solution, give up.
        throw new NoSolutionFoundException(b.getBoard());
    }

}
