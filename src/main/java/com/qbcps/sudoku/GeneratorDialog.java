package com.qbcps.sudoku;
/*
 * Copyright 4/20/18 by Stephen Beitzel
 */

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.mccalv.SGAdapter;
import com.qbcps.sudoku.model.Board;
import com.qbcps.sudoku.model.Difficulty;
import com.qbcps.sudoku.model.Generator;
import com.qbcps.sudoku.model.SKGeneratorAdapter;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class GeneratorDialog implements Initializable {
    private static final Logger __l = LoggerFactory.getLogger(GeneratorDialog.class);

    @FXML public ChoiceBox<String> _difficulty;
    @FXML public ChoiceBox<String> _generator;

    private Stage _stage;
    private Consumer<Board> _boardConsumer;

    private static final Map<String, Generator> GENERATOR_MAP = new ConcurrentHashMap<>();
    static {
        GENERATOR_MAP.put("mccalv", new SGAdapter());
        GENERATOR_MAP.put("SomeKittens", new SKGeneratorAdapter());
    }

    public static void display(Stage parent, Consumer<Board> boardConsumer) {
        try {
            Stage stage = new Stage();
            stage.initOwner(parent);
            InputStream layoutIS = Thread.currentThread().getContextClassLoader()
                                         .getResourceAsStream("com/qbcps/sudoku/GeneratorDialog.fxml");
            assert layoutIS != null;
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(layoutIS);
            GeneratorDialog gd = loader.getController();
            gd._stage = stage;
            gd._boardConsumer = boardConsumer;
            stage.setTitle("New Puzzle");
            stage.setScene(new Scene(root, 400, 300));
            stage.show();
        } catch (Exception e) {
            __l.error("Exception showing the generate new puzzle dialog", e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<String> difficultyNames = new ArrayList<>();
        for (Difficulty d : Difficulty.values()) {
            difficultyNames.add(d.name());
        }
        _difficulty.setItems(FXCollections.observableList(difficultyNames));
        _difficulty.getSelectionModel().select(Difficulty.EASY.name());

        ArrayList<String> generatorNames = new ArrayList<>(GENERATOR_MAP.keySet());
        Collections.sort(generatorNames);
        _generator.setItems(FXCollections.observableList(generatorNames));
        _generator.getSelectionModel().select(1);
    }

    @FXML
    @SuppressWarnings("unused")
    public void handleOK(ActionEvent evt) {
        try {
            Generator g = GENERATOR_MAP.get(_generator.getValue());
            Difficulty d = Difficulty.fromString(_difficulty.getValue());

            _boardConsumer.accept(new Board(g.generate(d)));
        } catch (Exception e) {
            __l.error("Exception creating / displaying board", e);
        }
        _stage.close();
    }

    @FXML
    @SuppressWarnings("unused")
    public void handleCancel(ActionEvent evt) {
        _stage.close();
    }
}
