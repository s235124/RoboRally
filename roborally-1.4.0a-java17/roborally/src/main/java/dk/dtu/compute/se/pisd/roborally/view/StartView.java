package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import static dk.dtu.compute.se.pisd.roborally.RoboRally.MIN_APP_WIDTH;

public class StartView extends HBox implements ViewObserver {

    AppController appController;

    HBox hbox;

    Button host;
    Button join;

    public StartView (AppController appController) {
        this.appController = appController;

        host = new Button("Host a game");
        host.setStyle("-fx-background-color: #3C7F55; -fx-text-fill: white; ");
        host.setOnAction( (e) -> {
            System.out.println("creating");
            appController.createHostView();
            System.out.println("created");
        });
        host.setMinSize(150,100);
        join = new Button("Join a game");
        join.setStyle("-fx-background-color: #3C7F55; -fx-text-fill: white; ");
        join.setMinSize(150,100);
        join.setOnAction( (e) -> {
            System.out.println("Join a game");
        });

        hbox = new HBox();
        hbox.setSpacing(MIN_APP_WIDTH - 10);
        hbox.getChildren().addAll(host, join);

        this.getChildren().add(hbox);
    }
    @Override
    public void updateView(Subject subject) {

    }
}
