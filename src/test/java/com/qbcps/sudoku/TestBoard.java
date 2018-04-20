package com.qbcps.sudoku;
/*
 * Copyright 4/19/18 by Stephen Beitzel
 */

import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
@Test(groups = {"all"})
public class TestBoard {
    public static int[][] UNSOLVED = {
          //  0 1 2 3 4 5 6 7 8
            { 7,8,1,6,0,2,9,0,5 }, // 0
    	    { 9,0,2,7,1,0,0,0,0 }, // 1
    	    { 0,0,6,8,0,0,0,1,2 }, // 2
    	    { 2,0,0,3,0,0,8,5,1 }, // 3
    	    { 0,7,3,5,0,0,0,0,4 }, // 4
    	    { 0,0,8,0,0,9,3,6,0 }, // 5
            { 1,9,0,0,0,7,0,8,0 }, // 6
    	    { 8,6,7,0,0,3,4,0,9 }, // 7
    	    { 0,0,5,0,0,0,1,0,0 }  // 8
          //  0 1 2 3 4 5 6 7 8
    };

    public static int[][] SOLVED = { { 7,8,1,6,3,2,9,4,5 },
    	                       { 9,5,2,7,1,4,6,3,8 },
    	                       { 4,3,6,8,9,5,7,1,2 },
    	                       { 2,4,9,3,7,6,8,5,1 },
    	                       { 6,7,3,5,8,1,2,9,4 },
    	                       { 5,1,8,4,2,9,3,6,7 },
    	                       { 1,9,4,2,6,7,5,8,3 },
    	                       { 8,6,7,1,5,3,4,2,9 },
    	                       { 3,2,5,9,4,8,1,7,6 } };

    @Test
    public void testInitFromPuzzle() {
        Board b = new Board(UNSOLVED);
        Cell d1 = b.getCell(0, 4);
        Cell d2 = b.getCell(0, 7);
        List<Integer> possibles = Arrays.asList(Integer.valueOf(3), Integer.valueOf(4));
        for (Cell c : Arrays.asList(d1, d2)) {
            Assert.assertEquals(c.getPossibles().size(), 2);
            Assert.assertTrue(c.getPossibles().containsAll(possibles));
        }
    }

    @Test
    public void testWiring() {
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
