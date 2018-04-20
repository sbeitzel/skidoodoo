package com.qbcps.sudoku;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

import com.qbcps.sudoku.model.Board;
import com.qbcps.sudoku.model.Difficulty;
import com.qbcps.sudoku.model.Generator;
import com.qbcps.sudoku.model.NoSolutionFoundException;
import com.qbcps.sudoku.model.QBSolver;
import com.qbcps.sudoku.model.SKSolverAdapter;
import com.qbcps.sudoku.model.Solver;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
@Test(groups = {"all"})
public class TestSolvers {

    @Test(groups = {"rkoutnik"}, dataProvider = "generators", dataProviderClass = DataProviders.class)
    public void testSKPerverse(String generatorName, Generator generator, String difficulty) {
        Solver solver = new SKSolverAdapter();
        System.out.println("testing rkoutnik solver against "+generatorName);
        runPuzzles(generator, solver, Difficulty.fromString(difficulty));
    }

    @Test(dataProvider = "generators", dataProviderClass = DataProviders.class)
    public void testQBSolver(String generatorName, Generator generator, String difficulty) {
        Solver solver = new QBSolver();
        System.out.println("testing QBSolver against "+generatorName);
        runPuzzles(generator, solver, Difficulty.fromString(difficulty));
    }

    @Test(dataProvider = "boards", dataProviderClass = DataProviders.class)
    public void testQBFixed(String difficulty, Board b) {
        QBSolver solver = new QBSolver();
        solver.getSolution(b.getBoard());
    }

    @Test(groups = {"rkoutnik"}, dataProvider = "boards", dataProviderClass = DataProviders.class)
    public void testSKFixed(String difficulty, Board b) {
        SKSolverAdapter solver = new SKSolverAdapter();
        solver.getSolution(b.getBoard());
    }

    private void runPuzzles(Generator gen, Solver solver, Difficulty d) {
        try {
            solver.getSolution(gen.generate(d));
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
