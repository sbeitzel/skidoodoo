package com.qbcps.sudoku;
/*
 * Copyright 4/20/18 by Stephen Beitzel
 */

import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
@Test(groups = "all")
public class TestFonts {
    private static final Logger __l = LoggerFactory.getLogger(TestFonts.class);

    @Test(enabled = false)
    public void testFindFamilies() {
        __l.info("Font families on this system");
        for (String family : Font.getFamilies()) {
            __l.info(family);
        }
    }
}
