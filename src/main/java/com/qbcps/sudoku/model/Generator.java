package com.qbcps.sudoku.model;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

/**
 * Anything that can generate a sudoku puzzle.
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public interface Generator {
    /**
     * Create a new puzzle with a number of holes based on the provided
     * difficulty. What the difficulty means, in terms of how many holes
     * and where they are, is up to the implementation.
     *
     * @param difficulty a hint to the generator for how many holes to leave in the puzzle
     * @return a 9x9 sudoku grid, where the integers 1-9 indicate clues and the value 0 means the space is empty
     */
    int[][] generate(Difficulty difficulty);
}
