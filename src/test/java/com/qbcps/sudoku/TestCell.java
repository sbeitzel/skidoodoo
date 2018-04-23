package com.qbcps.sudoku;
/*
 * Copyright 4/19/18 by Stephen Beitzel
 */

import com.qbcps.sudoku.model.Cell;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
@Test(groups = {"all"})
public class TestCell {

    @Test
    public void testInitialState() {
        Cell c = new Cell(0, 0);
        Assert.assertEquals(c.getValue(), 0);
        for (int v=1; v<10; v++) {
            Assert.assertTrue(c.getPossibles().contains(Integer.valueOf(v)));
        }
    }

    @Test
    public void testSetValue() {
        Cell c = new Cell(0, 0);
        c.setValue(3);
        Assert.assertEquals(c.getValue(), 3);
        Assert.assertTrue(c.getPossibles().isEmpty());
    }
}
