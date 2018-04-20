package com.qbcps.sudoku.model;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public enum Difficulty {

    SOLVED, EASY, MEDIUM, HARD;

    private static final Difficulty[] __allValues = Difficulty.values();

    public static Difficulty fromString(String s) {
        for (Difficulty e : __allValues) {
            if (e.name().equals(s)) {
                return e;
            }
        }
        throw new IllegalArgumentException("No such Difficulty: " + s);
    }
}

