package dk.dtu.compute.se.pisd.roborally.view;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.HttpController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Lobby;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;
import org.kordamp.bootstrapfx.BootstrapFX;

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

        refresh();
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

            places.get(i).setStyle("-fx-background-color: navy; -fx-text-fill: white; -fx-font-weight: bold;");

            int finalI = i;

            places.get(i).setOnAction(e -> {
                if (list.get(finalI).getCurrentPlayerCount() == list.get(finalI).getMaxPlayerCount()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Cannot join server");
                    alert.setHeaderText("Too many players");
                    alert.setContentText("There are too many players in this server, cannot join");

                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                    dialogPane.getStyleClass().addAll("alert", "alert-danger");

                    ButtonType okButtonType = alert.getButtonTypes().stream()
                            .filter(buttonType -> buttonType.getText().equals("OK"))
                            .findFirst()
                            .orElse(null);

                    if (okButtonType != null) { // Get styled
                        Node okButton = alert.getDialogPane().lookupButton(okButtonType);
                        okButton.getStyleClass().addAll("btn", "btn-danger");
                        okButton.setStyle("-fx-text-fill: black;");
                    }

                    alert.showAndWait();
                }
                else {
                    appcontroller.loadBoardClient(list.get(finalI));
                    Board gameBoard = appcontroller.gameController.board;

                    appcontroller.gameController.board.myColor = appcontroller.chooseColor(gameBoard);

                    try {
                        if (!HttpController.addPlayerToLobby(list.get(finalI).getLobbyID(), gameBoard.getPlayer(gameBoard.getPlayersNumber() - 1)))
                            return;
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    appcontroller.createLobbyView(list.get(finalI).getLobbyID());
                }
            });
        }

        refresh = new Button("Refresh");
        refresh.setStyle("-fx-background-color: navy; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; ");
        refresh.setOnAction(e -> {
            refresh();
        });

        vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(10);


        vbox.getChildren().addAll(places);
        vbox.getChildren().add(refresh);
        this.getChildren().add(vbox);
        appcontroller.increaseStageSize();
    }


    @Override
    public void updateView(Subject subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateView'");
    }
    
}
