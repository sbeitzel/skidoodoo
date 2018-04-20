package com.qbcps.sudoku.model;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

import com.rkoutnik.sudoku.PerverseSudokuSolver;

/**
 * Adapts the original project's solver to the {@link Solver} interface
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class SKSolverAdapter implements Solver {

    private PerverseSudokuSolver solver = new PerverseSudokuSolver();

    @Override
    public int[][] getSolution(int[][] puzzle) throws NoSolutionFoundException {
        solver.reset(puzzle);
        if (solver.solve()) {
            return solver.getBoard();
        }
        throw new NoSolutionFoundException(solver.getBoard());
    }
}
