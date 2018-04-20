package com.qbcps.sudoku.model;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            return solve(b, 0);
        } catch (NoSolutionFoundException nsfe) {
            __l.debug("Board failed with state:\n"+Board.render(nsfe.getPuzzle()), nsfe);
            throw nsfe;
        } catch (Exception e) {
            __l.debug("Solution failed", e);
            throw new NoSolutionFoundException(puzzle, "Exception thrown during solving", e);
        }
    }

    private int[][] solve(Board b, int guessNumber) throws NoSolutionFoundException {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<guessNumber; i++) {
            sb.append(" ");
        }
        String indent = sb.toString();
        while (true) {
            // this check is because I don't yet trust my data structure wiring
            b.integrityCheck();

            // check to see if we've solved it. Lazy is good!
            int[][] state = b.getBoard();
            SudokuChecker checker = new SudokuChecker(state);
            if (checker.completed()) {
                __l.debug(indent+"Puzzle completed");
                if (checker.checkPuzzle()) {
                    __l.debug(indent+"Puzzle solved with "+guessNumber+" guesses.");
                    return state;
                }
                __l.debug(indent+"Invalid solution");
                // we've got a number in every square but it isn't a valid solution
                throw new NoSolutionFoundException(state);
            }

            // look for cells with only one possibility
            if (fillInSingles(b)) {
                continue;
            }

            // look for possibilities with only one cell
            if (fillInSingleInGroup(b)) {
                continue;
            }

            // look for doubles, where cell1 and cell2 in a group only have the same two possibilities.
            // In which case, remove those possibilities from the rest of the group's cells.
            // This doesn't set a value but it limits the scope for guessing and can cut down the time to find
            // a solution pretty dramatically.
            if (findDoubles(b)) {
                continue;
            }

            // finally we need to guess. This is where recursion/backtracking happens.
            return guess(b, guessNumber+1);
        }
    }

    private boolean findDoubles(Board b) {
        boolean modified = false;
        for (CellGroup g : b.getGroups()) {
            List<Cell> cells = g.getCells();
            for (int p1 = 0; p1 < 8; p1++) {
                Cell first = cells.get(p1);
                if (first.getPossibles().size() == 2) {
                    for (int p2 = p1+1; p2 < 9; p2++) {
                        Cell second = cells.get(p2);
                        if (second.getPossibles().size() == 2 && second.getPossibles().containsAll(first.getPossibles())) {
                            // we know that the other cells in the group can't have these possibilities
                            modified = g.removePossibilities(Arrays.asList(first, second), first.getPossibles()) || modified;
                        }
                    }
                }
            }
        }
        return modified;
    }

    private boolean fillInSingles(Board b) {
        boolean found = false;
        for (Cell c : b) {
            if (c.getPossibles().size() == 1) {
                found = true;
                c.setValue(c.getPossibles().get(0).intValue());
            }
        }
        return found;
    }

    /**
     * If there is only one cell within a group which can possibly hold a given value,
     * then that's where that value belongs, even if there are other possible values still
     * for that cell.
     *
     * @param b the board to examine
     * @return <code>true</code> if any cells were modified.
     */
    private boolean fillInSingleInGroup(Board b) {
        boolean found = false;
        for (CellGroup group : b.getGroups()) {
            Map<Integer, List<Cell>> valueMap = new HashMap<>();
            for (Cell c : group.getCells()) {
                for (Integer possible : c.getPossibles()) {
                    List<Cell> cells = valueMap.computeIfAbsent(possible, k -> new ArrayList<>());
                    cells.add(c);
                }
            }
            for (Map.Entry<Integer, List<Cell>> entry : valueMap.entrySet()) {
                if (entry.getValue().size() == 1) {
                    // there is only one cell which can be set to the key
                    Cell c = entry.getValue().get(0);
                    c.setValue(entry.getKey().intValue());
                    found = true;
                }
            }
        }
        return found;
    }

    private int[][] guess(Board b, int guessNumber) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<guessNumber; i++) {
            sb.append(" ");
        }
        String indent = sb.toString();

        __l.debug(indent+"g: "+guessNumber);
        // for each available cell in b
        for (int row=0; row<9; row++) {
            for (int col=0; col<9; col++) {
                Cell c = b.getCell(row, col);
                if (!c.getPossibles().isEmpty()) {
                    // here's a guess
                    List<Integer> possibles = c.getPossibles();
                    for (Integer v : possibles) {
                        try {
                            // duplicate the board
                            Board b2 = new Board(b);
                            __l.debug(indent + "[" + row + ", " + col + "] = " + v + "?");
                            b2.getCell(row, col).setValue(v.intValue());
                            return solve(b2, guessNumber);
                        } catch (Exception e) {
                            __l.debug(indent + "[" + c.getRow() + ", " + c.getCol() + "] != " + v);
                            // here's an experiment. If we know that assigning value v to cell c doesn't work, then
                            // let's make a copy of Board b and remove v from c-prime's list of possibilities.
                            try {
                                Board bPrime = new Board(b);
                                Cell cPrime = bPrime.getCell(row, col);
                                cPrime.removePossibility(v.intValue());
                                b = bPrime;
                            } catch (IllegalArgumentException iae) {
                                // well, *that* is a bit of a surprise
                                __l.warn("Exception removing demonstrated bad value", iae);
                            }
                        }
                    }
                }
            }
        }
        throw new NoSolutionFoundException(b.getBoard(), "Couldn't find the solution by guessing; we give up", null);
    }
}
