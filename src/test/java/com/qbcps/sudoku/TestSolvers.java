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

    public void testSKPerverse() {
        Generator gen = new SKGeneratorAdapter();
        Solver psolv = new SKSolverAdapter();
        for (Difficulty d : Difficulty.values()) {
            try {
                psolv.getSolution(gen.generate(d));
            } catch (NoSolutionFoundException nsfe) {
                System.out.println("Unable to solve a puzzle of difficulty "+d);
            }
        }
    }
}
