package com.qbcps.sudoku;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

import com.qbcps.sudoku.model.Difficulty;
import com.qbcps.sudoku.model.Generator;
import com.rkoutnik.sudoku.SudokuChecker;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
@Test(groups = {"all"})
public class TestGenerators {
    @Test(dataProviderClass = DataProviders.class, dataProvider = "generators")
    public void testCreateBoards(String gName, Generator gen, String difficulty) {
        Difficulty d = Difficulty.fromString(difficulty);
        int[][] board = gen.generate(d);
        SudokuChecker checker = new SudokuChecker(board);
        Assert.assertTrue(checker.checkPuzzle(), "The generator created an invalid puzzle!");
    }
}
