package com.qbcps.sudoku.model;
/*
 * Copyright 4/19/18 by Stephen Beitzel
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A sudoku board
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class Board implements Iterable<Cell> {
    private Cell[][] _cells = new Cell[9][9];
    private ArrayList<CellGroup> _groups = new ArrayList<>(27);
    private CellGroup[] _rowGroups;
    private CellGroup[] _colGroups;
    private CellGroup[] _squareGroups;

    /**
     * Create a sudoku board and set it to the values in the provided array.
     *
     * @param puzzle the starting puzzle
     */
    public Board(int[][] puzzle) {
        initStructures();
        // now that we have all the cells created, we can populate them
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (puzzle[row][col] > 0) {
                    _cells[row][col].setValue(puzzle[row][col]);
                }
            }
        }
    }

    /**
     * Create an empty board
     */
    public Board() {
        initStructures();
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public Board(Board original) {
        initStructures();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                Cell copy = _cells[row][col];
                Cell oc = original._cells[row][col];
                if (oc.getValue() > 0) {
                    copy.setValue(oc.getValue());
                } else {
                    copy.retainPossibilities(oc.getPossibles());
                }
            }
        }
    }

    public void integrityCheck() throws NoSolutionFoundException {
        for (Cell c : this) {
            // if value is not zero, possibilities must be empty
            if (c.getValue() != 0 && !c.getPossibles().isEmpty()) {
                throw new NoSolutionFoundException(getBoard(), "Cell at row "+c.getRow()+", column "+c.getCol()+" has a value *and* possibilities", null);
            }
            // if possibilities is empty, value must not be zero
            if (c.getPossibles().isEmpty() && c.getValue() == 0) {
                throw new NoSolutionFoundException(getBoard(), "Cell at row "+c.getRow()+", column "+c.getCol()+" has no value *and* no possibilities", null);
            }
        }
    }

    public Cell getCell(int row, int col) {
        return _cells[row][col];
    }

    private void initStructures() {
        _rowGroups = new CellGroup[9];
        for (int r = 0; r < 9; r++) {
            _rowGroups[r] = new CellGroup();
        }
        _groups.addAll(Arrays.asList(_rowGroups));
        _colGroups = new CellGroup[9];
        for (int c = 0; c < 9; c++) {
            _colGroups[c] = new CellGroup();
        }
        _groups.addAll(Arrays.asList(_colGroups));
        _squareGroups = new CellGroup[9];
        for (int s = 0; s < 9; s++) {
            _squareGroups[s] = new CellGroup();
        }
        _groups.addAll(Arrays.asList(_squareGroups));

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                Cell c = new Cell(row, col);
                _rowGroups[row].addCell(c);
                _colGroups[col].addCell(c);
                _squareGroups[getSquare(row, col)].addCell(c);
                _cells[row][col] = c;
            }
        }
    }

    private int getSquare(int row, int col) {
        if (row < 3) {
            if (col < 3) {
                return 0;
            } else if (col < 6) {
                return 1;
            } else {
                return 2;
            }
        } else if (row < 6) {
            if (col < 3) {
                return 3;
            } else if (col < 6) {
                return 4;
            } else {
                return 5;
            }
        } else {
            if (col < 3) {
                return 6;
            } else if (col < 6) {
                return 7;
            } else {
                return 8;
            }
        }
    }

    public int[][] getBoard() {
        int[][] state = new int[9][9];
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
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

    public CellGroup getRow(int row) {
        return _rowGroups[row];
    }

    public CellGroup getCol(int col) {
        return _colGroups[col];
    }

    public CellGroup getSquare(int index) {
        return _squareGroups[index];
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

    public static String render(int[][] puzzle) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0 && i != 0) {
                sb.append("-  -  -  | -  -  -  |  -  -  -\n");
            }
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0 & j != 0) {
                    sb.append("| ");
                }
                sb.append(puzzle[i][j]).append("  ");
            }
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    public static String renderArray(int[][] puzzle) {
        StringBuilder sb = new StringBuilder("{");
        for (int i=0; i<9; i++) {
            sb.append("{");
            for (int j=0; j<9; j++) {
                sb.append(puzzle[i][j]);
                if (j < 8) {
                    sb.append(",");
                }
            }
            sb.append("}");
            if (i<8) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
