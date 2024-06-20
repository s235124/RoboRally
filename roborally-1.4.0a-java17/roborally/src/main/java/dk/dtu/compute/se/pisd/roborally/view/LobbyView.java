package dk.dtu.compute.se.pisd.roborally.view;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class LobbyView extends VBox implements ViewObserver {
    boolean isHost;

    AppController appController;

    List<String> playerID;

    Button ready;

    Label waiting;

    VBox vbox;

    public LobbyView(AppController appController, boolean isHost) {
        this.isHost = isHost;
        this.appController = appController;
        playerID = new ArrayList<>();

        playerID.add("blue");
        playerID.add("red");
        playerID.add("green");

        if (isHost) {
            ready = new Button("Ready");
            ready.setOnAction(e ->  {
                // Send signal to server for the other players

                this.appController.gameController.startProgrammingPhase();
                this.appController.createBoardView(this.appController.gameController);
            });
        }
        else {
            waiting = new Label("Waiting for host...");
        }
        vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(10);


        for (String ID : playerID) {
            vbox.getChildren().add(new Label(ID));
        }

        vbox.getChildren().add(isHost ? ready : waiting);
        this.getChildren().add(vbox);
    }

    @Override
    public void updateView(Subject subject) {

    }

    
}
