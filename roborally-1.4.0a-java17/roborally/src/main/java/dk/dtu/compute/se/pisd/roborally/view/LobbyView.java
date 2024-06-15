package dk.dtu.compute.se.pisd.roborally.view;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class LobbyView extends VBox implements ViewObserver {
    private boolean isHost;

    List<String> playerID;

    Button ready;

    VBox vbox;

    public LobbyView(boolean isHost) {
        this.isHost = isHost;
        playerID = new ArrayList<>();

        playerID.add("blue");
        playerID.add("red");
        playerID.add("green");

        ready = new Button("Ready");
        ready.setOnAction(e -> System.out.println("Readying up"));

        vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(10);


        for (String ID : playerID) {
            vbox.getChildren().add(new Label(ID));
        }

        vbox.getChildren().add(ready);
        this.getChildren().add(vbox);
    }

    @Override
    public void updateView(Subject subject) {

    }

    
}
