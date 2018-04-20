package com.qbcps.sudoku;
/*
 * Copyright 4/19/18 by Stephen Beitzel
 */

import com.mccalv.SGAdapter;
import com.qbcps.sudoku.model.Board;
import com.qbcps.sudoku.model.QBSolver;
import com.qbcps.sudoku.model.SKGeneratorAdapter;
import com.qbcps.sudoku.model.SKSolverAdapter;
import org.testng.annotations.DataProvider;

/**
 * Data provider methods for the test cases
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class DataProviders {

    /**
     * Create all the generators to be used in test cases
     *
     * @return generator name, instance, difficulty tuples
     */
    @DataProvider(name = "generators")
    public static Object[][] getGenerators() {
        return new Object[][] {
                {"rkoutnik", new SKGeneratorAdapter(), "EASY"}
                ,{"rkoutnik", new SKGeneratorAdapter(), "MEDIUM"}
                ,{"rkoutnik", new SKGeneratorAdapter(), "HARD"}
                ,{"mccalv", new SGAdapter(), "EASY"}
                ,{"mccalv", new SGAdapter(), "MEDIUM"}
                ,{"mccalv", new SGAdapter(), "HARD"}
        };
    }

    @DataProvider(name = "solvers")
    public static Object[][] getSolvers() {
        return new Object[][] {
                {"rkoutnik", new SKSolverAdapter()},
                {"QBSolver", new QBSolver()}
        };
    }

    @DataProvider(name = "boards")
    public static Object[][] getBoards() {
        return new Object[][] {
                {"EASY", new Board(new int[][]{{4, 0, 6, 0, 1, 2, 0, 0, 5},
                                               {3,0,0,4,7,9,0,0,6},
                                               {1,8,9,0,3,5,0,4,2},
                                               {0,0,3,0,5,1,0,0,7},
                                               {7,2,0,0,0,8,0,0,4},
                                               {0,6,0,0,2,4,0,3,1},
                                               {6,9,0,0,4,7,0,2,3},
                                               {2,0,0,5,8,0,4,0,9},
                                               {0,0,4,2,9,0,6,0,8}
                })},
                {"MEDIUM", new Board(new int[][]{{8,0,0,5,0,0,0,9,2},
                {0,2,5,0,0,6,0,0,0},
                {0,0,1,0,0,0,0,0,0},
                {0,8,9,1,0,0,5,0,0},
                {0,5,7,4,0,0,0,0,0},
                {0,6,4,0,3,0,1,8,0},
                {4,0,0,3,0,8,6,0,1},
                {0,1,3,0,0,0,0,0,0},
                {0,7,0,6,0,0,2,0,4}
                })},
                {"HARD", new Board(new int[][]{{0,0,0,0,4,0,0,7,0},
                {0,1,0,5,7,0,9,8,0},
                {0,0,2,8,0,0,0,0,0},
                {0,7,6,0,1,0,2,3,0},
                {4,5,0,0,0,0,0,0,0},
                {9,0,0,6,0,0,0,1,0},
                {5,0,0,0,0,0,1,0,0},
                {2,0,0,0,0,1,0,5,8},
                {0,0,0,0,0,0,6,0,0}
                })},
                {"EASY", new Board(new int[][]{{6,5,0,0,0,0,0,0,0},
                {4,0,7,0,2,0,0,0,9},
                {0,2,1,0,0,0,4,8,7},
                {5,9,0,0,7,0,0,0,0},
                {8,7,0,0,1,6,0,0,0},
                {0,1,6,0,0,4,0,0,0},
                {9,0,0,7,0,0,0,6,0},
                {0,3,0,1,0,0,9,4,8},
                {0,0,0,9,4,0,0,0,2}
                })},
                {"MEDIUM", new Board(new int[][]{{1,4,0,0,7,0,0,3,0},
                {2,0,0,8,0,0,0,4,0},
                {8,3,9,0,0,0,0,0,0},
                {0,0,0,7,0,0,0,0,1},
                {0,0,8,3,0,0,0,0,0},
                {3,9,0,0,5,0,0,6,0},
                {0,0,0,6,8,3,0,0,0},
                {0,8,0,0,0,0,0,0,7},
                {0,0,0,0,2,0,0,0,0}
                })},
                {"HARD", new Board(new int[][]{{0,0,0,0,2,0,0,0,0},
                {0,0,0,0,0,5,8,6,0},
                {0,0,0,0,0,1,0,0,0},
                {0,0,7,0,0,0,4,0,0},
                {0,0,3,4,5,0,0,0,0},
                {0,0,0,6,0,0,0,0,3},
                {0,0,2,0,0,0,0,0,0},
                {9,3,0,0,8,6,0,0,0},
                {0,0,0,0,0,2,0,0,0}
                })}
        };
    }
}
