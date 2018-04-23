package com.qbcps.sudoku.model;
/*
 * Copyright 4/21/18 by Stephen Beitzel
 */

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class QBGenerator implements Generator {
    private static final Logger __l = LoggerFactory.getLogger(QBGenerator.class);

    @Override
    public int[][] generate(Difficulty difficulty) {
        // first row is constraint free, so we can just set it
        Board b = new Board();
        for (int col=0; col<9; col++) {
            Cell c = b.getCell(0, col);
            ArrayList<Integer> possible = new ArrayList<>(c.getPossibles());
            Collections.shuffle(possible);
            Integer i = possible.get(0);
            c.setValue(i.intValue());
        }
        // we've now set the first row. Let's set the last row.
        for (int col=0; col<9; col++) {
            Cell c = b.getCell(8, col);
            ArrayList<Integer> possible = new ArrayList<>(c.getPossibles());
            Collections.shuffle(possible);
            Integer i = possible.get(0);
            c.setValue(i.intValue());
        }
        // Okay, now let's let the solver work
        QBSolver solver = new QBSolver();
        int[][] puzzle = solver.getSolution(b.getBoard());
        // puzzle is now filled in. Let's poke some holes.
        int holeCount = 0;
        switch (difficulty) {
            case HARD:
                holeCount = 55;
                break;
            case MEDIUM:
                holeCount = 51;
                break;
            case EASY:
                holeCount = 48;
                break;
        }
        Random r = new SecureRandom();
        while (holeCount > 0) {
            int ix = r.nextInt(81);
            int row = ix / 9;
            int col = ix % 9;
            if (puzzle[row][col] > 0) {
                holeCount--;
                puzzle[row][col] = 0;
            }
        }

        return  puzzle;
    }
}
