package com.paulvaroutsos.sudokusolver;
/*
 * Copyright 4/21/18 by Stephen Beitzel
 */

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.paulvaroutsos.satsolver.DPSolver;
import com.qbcps.sudoku.model.NoSolutionFoundException;
import com.qbcps.sudoku.model.Solver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts an int[][] sudoku grid into a series of lines that the {@link Mapper} expects.
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class SATAdapter implements Solver {
    private static final Logger __l = LoggerFactory.getLogger(SATAdapter.class);

    private static String transformPuzzle(int[][] puzzle) {
        StringBuilder sb = new StringBuilder();
        for (int row=0; row<9; row++) {
            for (int col=0; col<9; col++) {
                if (puzzle[row][col] > 0) {
                    sb.append(row+1).append(" ").append(col+1).append(" ").append(puzzle[row][col]).append(" 0\n");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public int[][] getSolution(int[][] puzzle) throws NoSolutionFoundException {
        try {
            String readyForMapper = transformPuzzle(puzzle);
            ByteArrayInputStream is = new ByteArrayInputStream(readyForMapper.getBytes("UTF-8"));
            InputStreamReader reader = new InputStreamReader(is);
            StringBuilder sb = new StringBuilder();
            SudokuReader sr = new SudokuReader(reader, sb);
            sr.extractClauses();
            // sb now holds all the lines stating the problem
            Mapper m = new Mapper(sb);
            m.buildSudokuClauses();
            // sb now holds the lines declaring the rules of sudoku after all the puzzle lines
            DPSolver dp = new DPSolver();
            int[] solution = dp.solve(sb);
            return UnMapper.unMap(solution);
        } catch (UnsupportedEncodingException e) {
            __l.error("The JVM doesn't support UTF-8; you've got bigger problems than a sudoku", e);
            throw new NoSolutionFoundException(puzzle, "Can't transform the puzzle for the solver", e);
        }
    }
}
