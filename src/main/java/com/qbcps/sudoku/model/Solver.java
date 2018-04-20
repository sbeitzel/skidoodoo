package com.qbcps.sudoku.model;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

/**
 * Anything that can solve a sudoku puzzle
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public interface Solver {

    int[][] getSolution(int[][] puzzle) throws NoSolutionFoundException;
}
