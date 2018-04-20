package com.qbcps.sudoku.model;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

import com.rkoutnik.sudoku.SudokuGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A sudoku generator that calls through to the project's original generator
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class SKGeneratorAdapter implements Generator {
    private static final Logger __l = LoggerFactory.getLogger(SKGeneratorAdapter.class);

    private final SudokuGenerator _sg = new SudokuGenerator();

    @Override
    public int[][] generate(Difficulty difficulty) {
        // comment from SudokuGenerator:
        /* We define difficulty as follows:
      			Easy: 32+ clues (49 or fewer holes)
      			Medium: 27-31 clues (50-54 holes)
      			Hard: 26 or fewer clues (54+ holes)
      			This is human difficulty, not algorithmically (though there is some correlation)
      	*/
        switch (difficulty) {
            case MEDIUM:
                return _sg.nextBoard(50);
            case HARD:
                return _sg.nextBoard(55);
            case EASY:
                return _sg.nextBoard(35);
            case SOLVED:
            default:
                return _sg.nextBoard(0);
        }
    }
}
