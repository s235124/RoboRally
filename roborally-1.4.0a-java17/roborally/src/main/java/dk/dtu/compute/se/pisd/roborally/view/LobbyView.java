package dk.dtu.compute.se.pisd.roborally.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.HttpController;
import dk.dtu.compute.se.pisd.roborally.controller.ThreadController;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.ServerPlayer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class LobbyView extends VBox implements ViewObserver {
    boolean isHost;
    boolean isStarted;

    AppController appController;

    List<String> playerID;

    Button ready;

    Label waiting;

    VBox vbox;

    public LobbyView(AppController appController, boolean isHost) {
        this.isHost = isHost;
        this.appController = appController;
        this.isStarted = false;
        playerID = new ArrayList<>();

        playerID.add("blue");
        playerID.add("red");
        playerID.add("green");

        if (isHost) {
            ready = new Button("Ready");
            ready.setOnAction(e ->  {
                try {
                    if (!HttpController.readying(this.appController.currentLobbyID))
                        return;
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }

                // Fetch other players in the lobby
                List<ServerPlayer> players;

                try {
                    players = HttpController.getPlayersFromLobbyID(this.appController.currentLobbyID);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                this.appController.gameController.board.clearPlayers();

                AtomicInteger i = new AtomicInteger();
                players.forEach(player -> {
                    Player player1 = new Player(this.appController.gameController.board, player.getColor(), player.getPlayerName());
                    player1.cardStr = player.getCardStr();
                    player1.setSpace(this.appController.gameController.board.getSpace(i.get(), i.get()));
                    this.appController.gameController.board.addPlayer(player1);
                    i.getAndIncrement();
                });

                this.appController.gameController.startProgrammingPhase();
                this.appController.createBoardView();
            });
        }
        else {
            waiting = new Label("Waiting for host...");
            ThreadController threadController = new ThreadController(this.appController, this.appController.currentLobbyID);
            Thread thread = new Thread(threadController);
            thread.start();
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
