package com.qbcps.sudoku;

import java.io.InputStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SKSudoku extends Application {
    private static final Logger __l = LoggerFactory.getLogger(SKSudoku.class);

    private static final double DEF_WIDTH = 600;
    private static final double DEF_HEIGHT = 400;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // display the main window
            InputStream layoutIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/qbcps/sudoku/SKMain.fxml");
            assert layoutIS != null;
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(layoutIS);
            SKMain appWindow = loader.getController();
            appWindow.setStage(primaryStage);

            primaryStage.setTitle("SKSudoku"); // TODO get this string from a resource bundle
            primaryStage.setScene(new Scene(root, DEF_WIDTH, DEF_HEIGHT));
            primaryStage.show();
            primaryStage.setOnCloseRequest(event -> Platform.exit());
        } catch (Exception e) {
            __l.error("Exception starting application!", e);
            throw e;
        }

    }
}
