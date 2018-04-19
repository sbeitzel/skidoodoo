package com.qbcps.sudoku;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

import com.rkoutnik.sudoku.SudokuChecker;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
@Test(groups = {"all"})
public class TestGenerators {

    @Test
    public void testSKGenerator() {
        Generator skgen = new SKGeneratorAdapter();
        int[][] puzzle = skgen.generate(Difficulty.SOLVED);
        SudokuChecker checker = new SudokuChecker(puzzle);
        Assert.assertTrue(checker.checkPuzzle(), "The generator created an invalid puzzle!");
    }
}
