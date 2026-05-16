package com.batch;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        //
        // WebView
        //

        WebView webView = new WebView();

        //
        // Engine HTML
        //

        WebEngine engine = webView.getEngine();

        //
        // Chargement du frontend
        //

        String url = getClass()
                .getResource("/web/index.html")
                .toExternalForm();

        engine.load(url);

        //
        // Exposition Java -> JavaScript
        //

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {

            if (newState == Worker.State.SUCCEEDED) {

                JSObject window =
                        (JSObject) engine.executeScript("window");

                window.setMember(
                        "javaBackend",
                        new BackendBridge(engine)
                );
            }
        });

        //
        // Fenêtre
        //

        Scene scene =
                new Scene(webView, 1100, 700);

        stage.setTitle("CSV Batch Processor");

        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}