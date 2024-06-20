package dk.dtu.compute.se.pisd.roborally.view;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.HttpController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Lobby;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class JoinView extends VBox implements ViewObserver {

    boolean notHost;

    AppController appcontroller;

    List<Button> places;
    Button refresh;

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
                    HttpController.addPlayerToLobby(list.get(finalI).getLobbyID(), gameBoard.getPlayer(gameBoard.getPlayersNumber() - 1));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                appcontroller.createLobbyView(list.get(finalI).getLobbyID());
            });
        }

        refresh = new Button("refresh");
        refresh.setOnAction(e -> {
            refresh();
        });

        vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(10);

        
        vbox.getChildren().addAll(places);
        vbox.getChildren().add(refresh);
        this.getChildren().add(vbox);
    }

    public void refresh () {
        this.getChildren().clear();
        places.clear();

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
                    HttpController.addPlayerToLobby(list.get(finalI).getLobbyID(), gameBoard.getPlayer(gameBoard.getPlayersNumber() - 1));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                appcontroller.createLobbyView(list.get(finalI).getLobbyID());
            });
        }

        refresh = new Button("refresh");
        refresh.setOnAction(e -> {
            refresh();
        });

        vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(10);


        vbox.getChildren().addAll(places);
        vbox.getChildren().add(refresh);
        this.getChildren().add(vbox);
    }


    @Override
    public void updateView(Subject subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateView'");
    }
    
}
