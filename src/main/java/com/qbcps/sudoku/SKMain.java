package com.qbcps.sudoku;
/*
 * Copyright 4/18/18 by Stephen Beitzel
 */

import java.net.URL;
import java.util.ResourceBundle;

import com.qbcps.sudoku.model.Board;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the main app window/menu
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class SKMain implements Initializable {
    private static final Logger __l = LoggerFactory.getLogger(SKMain.class);
    private static final String[] PREFERRED_FONTS = new String[] {
            "Crystal",
            "Courier New",
            "Courier",
            "Monospaced"
    };

    @FXML public MenuBar _mBar;
    @FXML public TextArea _boardDisplay;

    private Stage _myStage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font f;
        for (String family : PREFERRED_FONTS) {
            f = Font.font(family);
            if (family.equals(f.getFamily())) {
                _boardDisplay.setFont(f);
                break;
            }
        }
    }

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
        GeneratorDialog.display(_myStage, puzzle -> {
            _boardDisplay.clear();
            _boardDisplay.setText(Board.render(puzzle.getBoard()));
        });
    }

    @FXML
    @SuppressWarnings("unused")
    public void handleQuit(ActionEvent evt) {
        Platform.exit();
    }
}
