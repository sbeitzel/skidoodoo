package com.qbcps.sudoku;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

import com.qbcps.sudoku.model.Board;
import com.qbcps.sudoku.model.Difficulty;
import com.qbcps.sudoku.model.Generator;
import com.qbcps.sudoku.model.NoSolutionFoundException;
import com.qbcps.sudoku.model.QBSolver;
import com.qbcps.sudoku.model.Solver;
import com.rkoutnik.sudoku.SudokuChecker;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
@Test(groups = {"all"})
public class TestSolvers {

    @Test(dataProvider = "solverMatrix", dataProviderClass = DataProviders.class)
    public void testSolvers(String comboName, Generator generator, String difficulty, Solver solver) {
        System.out.println("testing " + comboName);
        runPuzzles(generator, solver, Difficulty.fromString(difficulty));
    }

    @Test(dataProvider = "boards", dataProviderClass = DataProviders.class)
    public void testQBFixed(String difficulty, Board b) {
        System.out.println(difficulty);
        QBSolver solver = new QBSolver();
        solver.getSolution(b.getBoard());
    }

    private void runPuzzles(Generator gen, Solver solver, Difficulty d) {
        try {
            int[][] solved = solver.getSolution(gen.generate(d));
            SudokuChecker checker = new SudokuChecker(solved);
            Assert.assertTrue(checker.completed() && checker.checkPuzzle(), "Solver didn't solve the puzzle!");
        } catch (NoSolutionFoundException nsfe) {
            Assert.fail("Unable to solve a puzzle of difficulty " + d);
        }
    }

    @Test(dataProvider = "solvers", dataProviderClass = DataProviders.class)
    public void testLegacyPuzzle(String solverName, Solver solver) {
        String solution = Board.render(TestBoard.SOLVED);
        String solverSolution = Board.render(solver.getSolution(TestBoard.UNSOLVED));
        Assert.assertEquals(solverSolution, solution, solverName+" got an unexpected solution");
    }
}
