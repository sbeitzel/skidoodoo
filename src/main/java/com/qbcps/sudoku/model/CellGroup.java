package com.qbcps.sudoku.model;
/*
 * Copyright 4/19/18 by Stephen Beitzel
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A group of cells in a sudoku board (column, row, or square)
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class CellGroup implements CellChangeListener {
    private ArrayList<Cell> _cells = new ArrayList<>(9);

    public void addCell(Cell c) {
        _cells.add(c);
        c.addListener(this);
    }

    @Override
    public void valueSet(Cell c, int v) {
        for (Cell c1 : _cells) {
            c1.removePossibility(v);
        }
    }

    public boolean removePossibilities(Collection<Cell> excludeCells, Collection<Integer> values) {
        boolean didWork = false;
        for (Cell c : _cells) {
            if (!excludeCells.contains(c)) {
                for (Integer i : values) {
                    didWork = c.removePossibility(i.intValue()) || didWork;
                }
            }
        }
        return didWork;
    }

    public List<Cell> getCells() {
        return Collections.unmodifiableList(_cells);
    }
}
