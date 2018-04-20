package com.qbcps.sudoku;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

import com.mccalv.SGAdapter;
import com.qbcps.sudoku.model.Board;
import com.qbcps.sudoku.model.Difficulty;
import com.qbcps.sudoku.model.Generator;
import com.qbcps.sudoku.model.SKGeneratorAdapter;
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

    public void testMCGenerator() {
        Generator mcgen = new SGAdapter();
        int[][] puzzle = mcgen.generate(Difficulty.SOLVED);
        SudokuChecker checker = new SudokuChecker(puzzle);
        Assert.assertTrue(checker.checkPuzzle(), "The generator created an invalid puzzle!");
    }

    @Test(dataProviderClass = DataProviders.class, dataProvider = "generators")
    public void testCreateBoards(String gName, Generator gen, String difficulty) {
        Difficulty d = Difficulty.fromString(difficulty);
        int[][] board = gen.generate(d);
        System.out.println(Board.renderArray(board));
    }
}
