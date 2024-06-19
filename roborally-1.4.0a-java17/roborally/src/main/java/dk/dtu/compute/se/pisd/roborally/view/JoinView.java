package dk.dtu.compute.se.pisd.roborally.view;

import java.util.ArrayList;
import java.util.Arrays;
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

    AppController appcontroller;

    List<Button> places;
    private Button Refresh;

    VBox vbox;

    public JoinView(AppController appController, boolean notHost){
        this.appcontroller = appController;
        this.notHost = notHost;

        places = new ArrayList<>();

        List<Lobby> list = new ArrayList<>();
        try {
            List<Lobby> f = HttpController.getLobbies();
            list.addAll(f);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < list.size(); i++) {
            places.add(new Button(list.get(i).getLobbyID() + ", " + list.get(i).getCurrentPlayerCount() + "/" + list.get(i).getMaxPlayerCount()));
            int finalI = i;

            places.get(i).setOnAction(e -> {
                appcontroller.loadBoardClient(list.get(finalI));
                Board gameBoard = appcontroller.gameController.board;

                appcontroller.chooseColor(gameBoard);

                try {
                    HttpController.addPlayerToLobby(finalI, gameBoard.getPlayer(gameBoard.getPlayersNumber() - 1));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                appController.createLobbyView(list.get(finalI).getLobbyID());
            });
        }

        Refresh = new Button("Refresh");
        Refresh.setOnAction(e -> {
            System.out.println("refreshing the lobby");
        });

        vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(10);

        
        vbox.getChildren().addAll(places);
        vbox.getChildren().add(Refresh);
        this.getChildren().add(vbox);
    }




    @Override
    public void updateView(Subject subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateView'");
    }
    
}
