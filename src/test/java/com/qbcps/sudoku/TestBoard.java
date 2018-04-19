package com.qbcps.sudoku;
/*
 * Copyright 4/19/18 by Stephen Beitzel
 */

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
@Test(groups = {"all"})
public class TestBoard {

    @Test
    public void testSetup() {
        int[][] puzzle = new int[9][9];
        for (int row = 0; row<9; row++) {
            for (int col = 0; col <9; col++) {
                puzzle[row][col] = 0;
            }
        }
        Board b = new Board(puzzle);
        Cell topLeft = b.getCell(0, 0);

        Assert.assertTrue(b.getRow(0).getCells().contains(topLeft));
        Assert.assertTrue(b.getCol(0).getCells().contains(topLeft));
        Assert.assertTrue(b.getSquare(0).getCells().contains(topLeft));

        // this part tests that the message listeners were all wired up properly
        topLeft.setValue(1);
        Integer one = Integer.valueOf(1);
        // assert that 1 is no longer a possibility for any other cell in row 0 or column 0 or in the top left square
        for (Cell c : b.getRow(0).getCells().subList(1, 9)) {
            Assert.assertFalse(c.getPossibles().contains(one));
        }
        for (Cell c : b.getCol(0).getCells().subList(1, 9)) {
            Assert.assertFalse(c.getPossibles().contains(one));
        }
        for (Cell c : b.getSquare(0).getCells()) {
            if (!c.equals(topLeft)) {
                Assert.assertFalse(c.getPossibles().contains(one));
            }
        }
        // assert that 1 *is* a possibility for any cell in squares 4, 5, 7, 8
        for (int gi : new int[] {4,5,7,8}) {
            CellGroup g = b.getSquare(gi);
            for (Cell c : g.getCells()) {
                Assert.assertTrue(c.getPossibles().contains(one));
            }
        }
        // furthermore, 1 is still a possibility for cells in squares 1, 2, 3, 6 so long as they are not in row 0 or column 0
        for (int gi : new int[] {1,2,3,6}) {
            CellGroup g = b.getSquare(gi);
            for (Cell c : g.getCells()) {
                if (c.getRow() != 0 && c.getCol() != 0) {
                    Assert.assertTrue(c.getPossibles().contains(one));
                }
            }
        }
    }
}
