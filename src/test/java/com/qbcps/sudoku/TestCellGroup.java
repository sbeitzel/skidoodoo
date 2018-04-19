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
public class TestCellGroup {

    @Test
    public void testNotifyOnSetValue() {
        int row = 0;
        CellGroup group = new CellGroup();
        for (int col = 0; col<9; col++) {
            group.addCell(new Cell(row, col));
        }
        Cell first = group.getCells().get(0);
        first.setValue(1);
        for (Cell c : group.getCells().subList(1, 9)) {
            Assert.assertFalse(c.getPossibles().contains(Integer.valueOf(first.getValue())));
            Assert.assertFalse(c.getPossibles().isEmpty());
        }
    }
}
