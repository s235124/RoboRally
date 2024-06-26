package dk.dtu.compute.se.pisd.roborally.view;

import static dk.dtu.compute.se.pisd.roborally.RoboRally.*;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartView extends HBox implements ViewObserver {

    AppController appController;

    private BorderPane boardRoot;

    HBox hbox;

    Button host;
    Button join;
    Button local;

    public StartView (AppController appController) {
        this.appController = appController;

        host = new Button("Host a game");
        host.setStyle("-fx-font-weight: bold");
        host.getStyleClass().setAll("btn","btn-primary","btn-lg");
        host.setOnAction(e -> {
            appController.loadBoardHost();
            appController.createHostView();
        });
        host.setMinSize(150,100);
        join = new Button("Join a game");
        join.setStyle("-fx-font-weight: bold");
        join.getStyleClass().setAll("btn","btn-info","btn-lg");
        join.setMinSize(150,100);
        join.setOnAction(e -> appController.createJoinView());
        local = new Button("Play locally");
        local.setStyle("-fx-font-weight: bold");
        local.getStyleClass().setAll("btn","btn-success","btn-lg");
        local.setMinSize(150,100);
        local.setOnAction(e -> appController.createMenuBarView());

        hbox = new HBox();
        hbox.setSpacing(200);
        hbox.getChildren().addAll(host, join, local);

        this.getChildren().add(hbox);
    }
    @Override
    public void updateView(Subject subject) {

    }
}
