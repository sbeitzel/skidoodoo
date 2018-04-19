package com.qbcps.sudoku;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the main app window/menu
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class SKMain {
    private static final Logger __l = LoggerFactory.getLogger(SKMain.class);

    @FXML public MenuBar _mBar;

    private Stage _myStage;

    /**
     * Tell the controller what stage it's being shown on, in case it needs to open subordinate windows
     *
     * @param s the stage
     */
    void setStage(Stage s) {
        _myStage = s;
    }

    @FXML
    @SuppressWarnings("unused")
    public void handleAbout(ActionEvent evt) {
        __l.warn("About not implemented");
    }

    @FXML
    @SuppressWarnings("unused")
    public void handleNewBoard(ActionEvent evt) {
        __l.warn("New not implemented");
    }

    @FXML
    @SuppressWarnings("unused")
    public void handleQuit(ActionEvent evt) {
        Platform.exit();
    }
}
