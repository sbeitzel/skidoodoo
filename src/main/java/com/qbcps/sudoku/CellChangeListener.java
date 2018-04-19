package com.qbcps.sudoku;
/*
 * Copyright 4/19/18 by Stephen Beitzel
 */

/**
 * A thing that can be notified that a cell has had its value set
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public interface CellChangeListener {
    /**
     * Indicates that a particular value has been assigned to the originating cell
     *
     * @param v the value
     * @param c the cell that changed
     */
    void valueSet(Cell c, int v);
}
