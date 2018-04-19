package com.qbcps.sudoku;
/*
 * Copyright 4/19/18 by Stephen Beitzel
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A square in a sudoku board
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class Cell {
    private ArrayList<Integer> _possibles = new ArrayList<>(9);
    private int value = 0;
    private final ArrayList<CellChangeListener> _listeners = new ArrayList<>();
    private final int _row;
    private final int _col;

    Cell(int row, int col) {
        for (int i = 1; i < 10; i++) {
            _possibles.add(Integer.valueOf(i));
        }
        _row = row;
        _col = col;
    }

    public int getRow() {
        return _row;
    }

    public int getCol() {
        return _col;
    }

    public void setValue(int v) {
        if (_possibles.contains(Integer.valueOf(v))) {
            value = v;
            _possibles.clear();
            for (CellChangeListener ccl : _listeners) {
                ccl.valueSet(this, v);
            }
        } else {
            throw new IllegalArgumentException("The value " + v + " is not a possible value for this cell. Possible values are: " + _possibles);
        }
    }

    public int getValue() {
        if (_possibles.isEmpty()) {
            return value;
        } else {
            return 0;
        }
    }

    public void addListener(CellChangeListener ccl) {
        _listeners.add(ccl);
    }

    public void removeListener(CellChangeListener ccl) {
        _listeners.remove(ccl);
    }

    public List<Integer> getPossibles() {
        return Collections.unmodifiableList(_possibles);
    }

    public void retainPossibilities(Collection<Integer> values) {
        _possibles.retainAll(values);
        if (_possibles.isEmpty() && value == 0) {
            throw new IllegalArgumentException("This removes all the possibilities for an unset cell!");
        }
    }

    public boolean removePossibility(int v) {
        if (value == 0 && _possibles.size() == 1 && _possibles.contains(Integer.valueOf(v))) {
            throw new IllegalArgumentException("This removes the one possible value from the cell!");
        }
        return _possibles.remove(Integer.valueOf(v));
    }
}
