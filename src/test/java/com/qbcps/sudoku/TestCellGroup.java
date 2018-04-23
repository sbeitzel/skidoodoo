package com.qbcps.sudoku;
/*
 * Copyright 4/19/18 by Stephen Beitzel
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.qbcps.sudoku.model.Cell;
import com.qbcps.sudoku.model.CellGroup;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
@Test(groups = {"all"})
public class TestCellGroup {

    private CellGroup initGroup() {
        int row = 0;
        CellGroup group = new CellGroup();
        for (int col = 0; col<9; col++) {
            group.addCell(new Cell(row, col));
        }
        return group;
    }

    @Test
    public void testNotifyOnSetValue() {
        CellGroup group = initGroup();
        Cell first = group.getCells().get(0);
        first.setValue(1);
        for (Cell c : group.getCells().subList(1, 9)) {
            Assert.assertFalse(c.getPossibles().contains(Integer.valueOf(first.getValue())));
            Assert.assertFalse(c.getPossibles().isEmpty());
        }
    }

    @Test
    public void testRemovePossibles() {
        CellGroup group = initGroup();
        ArrayList<Integer> toRemove = new ArrayList<>();
        for (int i = 3; i<9; i++) {
            toRemove.add(Integer.valueOf(i));
        }
        Cell c1 = group.getCells().get(0);
        Cell c2 = group.getCells().get(1);
        ArrayList<Integer> toKeep = new ArrayList<>();
        toKeep.add(Integer.valueOf(1));
        toKeep.add(Integer.valueOf(2));
        c1.retainPossibilities(toRemove);
        c2.retainPossibilities(toRemove);
        // c1 and c2 should still contain the possibilities 1 and 2, and no others. So, in the QBSolver context, they're a "double".
        Assert.assertTrue(c1.getPossibles().containsAll(toRemove));
        Assert.assertTrue(c2.getPossibles().containsAll(toRemove));
        Assert.assertFalse(c1.getPossibles().containsAll(toKeep));
        Assert.assertFalse(c2.getPossibles().containsAll(toKeep));
        // Similarly, cells 3-9 should have as possibilities everything *except* 1 and 2.
        for (Cell c : group.getCells().subList(2, 9)) {
            Assert.assertTrue(c.getPossibles().containsAll(toKeep));
            Assert.assertTrue(c.getPossibles().containsAll(toRemove));
        }
        group.removePossibilities(Arrays.asList(c1, c2), toRemove);
        Assert.assertTrue(c1.getPossibles().containsAll(toRemove));
        Assert.assertTrue(c2.getPossibles().containsAll(toRemove));
        Assert.assertFalse(c1.getPossibles().containsAll(toKeep));
        Assert.assertFalse(c2.getPossibles().containsAll(toKeep));
        for (Cell c : group.getCells().subList(2, 9)) {
            Assert.assertTrue(c.getPossibles().containsAll(toKeep));
            Assert.assertFalse(c.getPossibles().containsAll(toRemove));
        }
    }

    @Test
    public void testRemainingAfterSet() {
        CellGroup group = initGroup();
        List<Cell> cells = group.getCells();

        for (int i=0; i<7; i++) {
            cells.get(i).setValue(i+1);
        }
        List<Integer> remaining = Arrays.asList(Integer.valueOf(8), Integer.valueOf(9));
        for (Cell c : cells.subList(7, 9)) {
            Assert.assertEquals(c.getPossibles().size(), 2);
            Assert.assertTrue(c.getPossibles().containsAll(remaining));
        }
    }
}
