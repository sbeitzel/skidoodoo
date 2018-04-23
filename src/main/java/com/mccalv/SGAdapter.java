package com.mccalv;
/*
 * Copyright 4/19/18 by Stephen Beitzel
 */

import com.qbcps.sudoku.model.Difficulty;
import com.qbcps.sudoku.model.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter to fit mccalv's generator to the QBCPS interface
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class SGAdapter implements Generator {
    private static final Logger __l = LoggerFactory.getLogger(SGAdapter.class);

    @Override
    public int[][] generate(Difficulty difficulty) {
        Sudoku.SudokuLevel level;
        switch (difficulty) {
            case EASY:
                level = Sudoku.SudokuLevel.EASY;
                break;
            case MEDIUM:
                level = Sudoku.SudokuLevel.MEDIUM;
                break;
            case HARD:
                level = Sudoku.SudokuLevel.DIFFICULT;
                break;
            default:
                level = null;
                break;
        }
        Sudoku puzzle;
        if (level != null) {
            puzzle = SudokuGenerator.generatePlayableRandomSudoku(level);
        } else {
            puzzle = SudokuGenerator.generateRandomSudoku();
        }
        // convert Sudoku to int[][]
        Integer[][] matrix = puzzle.getMatrix();
        int[][] board = new int[9][9];
        for (int row=0; row<9; row++) {
            for (int col=0; col<9; col++) {
                board[row][col] = matrix[row][col].intValue();
            }
        }
        return board;
    }
}
