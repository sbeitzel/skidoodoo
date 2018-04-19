package com.qbcps.sudoku;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

import org.testng.annotations.Test;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
@Test(groups = {"all"})
public class TestSolvers {

    @Test
    public void testSKPerverse() {
        runPuzzles(new SKGeneratorAdapter(), new SKSolverAdapter());
    }

    @Test
    public void testQBSolver() {
        runPuzzles(new SKGeneratorAdapter(), new QBSolver());
    }

    private void runPuzzles(Generator gen, Solver solver) {
        for (Difficulty d : Difficulty.values()) {
            try {
                solver.getSolution(gen.generate(d));
            } catch (NoSolutionFoundException nsfe) {
                System.out.println("Unable to solve a puzzle of difficulty "+d);
            }
        }
    }
}
