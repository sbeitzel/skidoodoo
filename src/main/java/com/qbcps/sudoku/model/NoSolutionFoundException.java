package com.qbcps.sudoku.model;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

/**
 * Exception thrown by a {@link Solver} to indicate that it could not solve a particular puzzle.
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class NoSolutionFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int[][] _puzzle;

    public NoSolutionFoundException(int[][] puzzle) {
        copyPuzzle(puzzle);
    }

    public NoSolutionFoundException(int[][] puzzle, String message, Throwable cause) {
        super(message, cause);
        copyPuzzle(puzzle);
    }

    public int[][] getPuzzle() {
        return _puzzle;
    }

    private void copyPuzzle(int[][] puzzle) {
        if (puzzle != null && puzzle.length>0) {
            _puzzle = new int[puzzle.length][puzzle[0].length];
            for (int i=0; i<puzzle.length; i++) {
                System.arraycopy(puzzle[i], 0, _puzzle[i], 0, puzzle[i].length);
            }
        } else {
            _puzzle = null;
        }
    }
}
