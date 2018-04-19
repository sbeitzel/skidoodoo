package com.qbcps.sudoku;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
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
        } catch (Exception e) {
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
                    if (c.possibles.size() == 1) {
                        valueFound = true;
                        c.setValue(c.possibles.get(0).intValue());
                        singles++;
                    }
                }
            } while (valueFound);
            __l.debug("Found "+singles+" cells with only a single possibility");
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
                        for (int p2 = 1; p2 < 9; p2++) {
                            Cell second = cells.get(p2);
                            if (second.getPossibles().size() == 2) {
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
        for (Cell c : b) {
            // find a cell with possibilities
            if (c.getPossibles().size()>1) {
                ArrayList<Integer> workingList = new ArrayList<>(c.getPossibles());
                for (Integer v : workingList) {
                    // pick one of the possible values
                    Board b2 = new Board(b);
                    b2._cells[c.getRow()][c.getCol()].setValue(v.intValue());
                    // try to solve the new board
                    try {
                        // if we get a solution, great, return it
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

    private static class Cell {
        private ArrayList<Integer> possibles = new ArrayList<>(9);
        private int value = 0;
        private final ArrayList<CellChangeListener> _listeners = new ArrayList<>();
        private final int _row;
        private final int _col;

        Cell(int row, int col) {
            for (int i=1; i<10; i++) {
                possibles.add(Integer.valueOf(i));
            }
            _row = row;
            _col = col;
        }

        public int getRow() {
            return _row;
        }

        public int getCol() {
            return _col;
        }

        public void setValue(int v) {
            if (possibles.contains(Integer.valueOf(v))) {
                value = v;
                possibles.clear();
                for (CellChangeListener ccl : _listeners) {
                    ccl.valueSet(this, v);
                }
            } else {
                throw new IllegalArgumentException("The value "+v+" is not a possible value for this cell. Possible values are: "+possibles);
            }
        }

        public int getValue() {
            if (possibles.isEmpty()) {
                return value;
            } else {
                return 0;
            }
        }

        public void addListener(CellChangeListener ccl) {
            _listeners.add(ccl);
        }

        public void removeListener(CellChangeListener ccl) {
            _listeners.remove(ccl);
        }

        public List<Integer> getPossibles() {
            return Collections.unmodifiableList(possibles);
        }

        public boolean removePossibility(int v) {
            return possibles.remove(Integer.valueOf(v));
        }
    }

    private static class CellGroup implements CellChangeListener {
        private ArrayList<Cell> _cells = new ArrayList<>(9);

        public void addCell(Cell c) {
            _cells.add(c);
            c.addListener(this);
        }

        @Override
        public void valueSet(Cell c, int v) {
            for (Cell c1 : _cells) {
                c1.possibles.remove(Integer.valueOf(v));
            }
        }

        public boolean removePossibilities(Collection<Cell> excludeCells, Collection<Integer> values) {
            boolean didWork = false;
            for (Cell c : _cells) {
                if (! excludeCells.contains(c)) {
                    for (Integer i : values) {
                        didWork = c.removePossibility(i.intValue()) || didWork;
                    }
                }
            }
            return didWork;
        }

        public List<Cell> getCells() {
            return Collections.unmodifiableList(_cells);
        }
    }

    private interface CellChangeListener {
        /**
         * Indicates that a particular value has been assigned to the originating cell
         * @param v the value
         * @param c the cell that changed
         */
        void valueSet(Cell c, int v);
    }

    private static class Board implements Iterable<Cell> {
        Cell[][] _cells = new Cell[9][9];
        ArrayList<CellGroup> _groups = new ArrayList<>(27);

        Board(int[][] puzzle) {
            initStructures();
            // now that we have all the cells created, we can populate them
            for (int row=0; row<9; row++) {
                for (int col=0; col<9; col++) {
                    if (puzzle[row][col] > 0) {
                        _cells[row][col].setValue(puzzle[row][col]);
                    }
                }
            }
        }

        @SuppressWarnings("CopyConstructorMissesField")
        Board(Board original) {
            initStructures();
            for (int row=0; row<9; row++) {
                for (int col=0; col<9; col++) {
                    Cell copy = _cells[row][col];
                    Cell oc = original._cells[row][col];
                    copy.possibles.retainAll(oc.possibles);
                }
            }
        }

        private void initStructures() {
            CellGroup[] rowGroups = new CellGroup[9];
            for (int r=0; r<9; r++) {
                rowGroups[r] = new CellGroup();
            }
            _groups.addAll(Arrays.asList(rowGroups));
            CellGroup[] colGroups = new CellGroup[9];
            for (int c=0; c<9; c++) {
                colGroups[c] = new CellGroup();
            }
            _groups.addAll(Arrays.asList(colGroups));
            CellGroup[] squareGroups = new CellGroup[9];
            for (int s=0; s<9; s++) {
                squareGroups[s] = new CellGroup();
            }
            _groups.addAll(Arrays.asList(squareGroups));

            for (int row = 0; row<9; row++) {
                for (int col=0; col<9; col++) {
                    Cell c = new Cell(row, col);
                    rowGroups[row].addCell(c);
                    colGroups[col].addCell(c);
                    squareGroups[getSquare(row, col)].addCell(c);
                    _cells[row][col] = c;
                }
            }
        }

        int getSquare(int row, int col) {
            if (row < 3) {
                if (col <3) {
                    return 0;
                } else if (col <6) {
                    return 1;
                } else {
                    return 2;
                }
            } else if (row < 6) {
                if (col <3) {
                    return 3;
                } else if (col <6) {
                    return 4;
                } else {
                    return 5;
                }
            } else {
                if (col <3) {
                    return 6;
                } else if (col <6) {
                    return 7;
                } else {
                    return 8;
                }
            }
        }

        public int[][] getBoard() {
            int[][] state = new int[9][9];
            for (int row=0; row<9; row++) {
                for (int col=0; col<9; col++) {
                    Cell c = _cells[row][col];
                    state[row][col] = c.getValue();
                }
            }
            return state;
        }

        @Override
        public Iterator<Cell> iterator() {
            return new BoardCellIterator();
        }

        public List<CellGroup> getGroups() {
            return Collections.unmodifiableList(_groups);
        }

        private class BoardCellIterator implements Iterator<Cell> {
            private int row = 0;
            private int col = 0;

            @Override
            public boolean hasNext() {
                return row < _cells.length && col < _cells[row].length;
            }

            @Override
            public Cell next() {
                Cell c = _cells[row][col++];
                if (col > _cells[row].length) {
                    col = 0;
                    row++;
                }
                return c;
            }
        }
    }
}
