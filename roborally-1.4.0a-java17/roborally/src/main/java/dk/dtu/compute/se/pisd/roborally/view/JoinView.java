package dk.dtu.compute.se.pisd.roborally.view;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.HttpController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Lobby;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class JoinView extends VBox implements ViewObserver {

    private boolean notHost;
    private AppController appController;
    private List<Button> places;
    private Button refresh;
    private VBox vbox;

    public JoinView(AppController appController, boolean notHost) {
        this.appController = appController;
        this.notHost = notHost;

        places = new ArrayList<>();
        vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(10);

        refreshLobbyList();

        refresh = new Button("Refresh");
        refresh.setOnAction(e -> refreshLobbyList());

        vbox.getChildren().add(refresh);
        this.getChildren().add(vbox);
    }

    private void refreshLobbyList() {
        List<Lobby> lobbies = new ArrayList<>();
        try {
            List<Lobby> fetchedLobbies = HttpController.getLobbies();
            lobbies.addAll(fetchedLobbies);
        } catch (Exception e) {
            e.printStackTrace();
        }

        places.clear();
        vbox.getChildren().clear();
        vbox.getChildren().add(refresh); // Keep the refresh button

        for (Lobby lobby : lobbies) {
            if (lobby != null) {
                try {
                    Button lobbyButton = new Button(lobby.getLobbyID() + ", " + lobby.getCurrentPlayerCount() + "/" + lobby.getMaxPlayerCount());
                    lobbyButton.setOnAction(e -> {
                        appController.loadBoardClient(lobby);
                        Board gameBoard = appController.gameController.board;

                        appController.chooseColor(gameBoard);

                        try {
                            Player newPlayer = new Player(); // Initialiser en ny Player
                            newPlayer.setColor(getAccessibleHelp());
                            HttpController.addPlayerToLobby(lobby.getLobbyID(), newPlayer);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            throw new RuntimeException(ex);
                        }

                        appController.createLobbyView(lobby.getLobbyID());
                    });

                    places.add(lobbyButton);
                } catch (Exception e) {
                    System.err.println("Error creating button for lobby: " + lobby.getLobbyID());
                    e.printStackTrace();
                }
            }
        }

        try {
            vbox.getChildren().addAll(places);
        } catch (Exception e) {
            System.err.println("Error adding buttons to VBox.");
            e.printStackTrace();
        }
    }

    @Override
    public void updateView(Subject subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateView'");
    }
}
