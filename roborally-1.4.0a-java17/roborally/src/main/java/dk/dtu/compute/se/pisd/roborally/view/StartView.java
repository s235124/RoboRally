package dk.dtu.compute.se.pisd.roborally.view;

import static dk.dtu.compute.se.pisd.roborally.RoboRally.*;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

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
            appController.createJoinView();
            System.out.println("created a join");
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
