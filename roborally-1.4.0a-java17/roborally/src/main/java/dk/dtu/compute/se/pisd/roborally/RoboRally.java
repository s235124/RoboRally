/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally;

import java.util.List;
import java.util.Optional;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.view.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class RoboRally extends Application {

    public static final int MIN_APP_WIDTH = 600;

    private Stage stage;
    private BorderPane boardRoot;

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        AppController appController = new AppController(this);

        // create the primary scene with the a menu bar and a pane for
        // the board view (which initially is empty); it will be filled
        // when the user creates a new game or loads a game
        StartView startView = new StartView(appController);
        boardRoot = new BorderPane();
        VBox vbox = new VBox(startView, boardRoot);
        vbox.setMinWidth(MIN_APP_WIDTH);
        Scene primaryScene = new Scene(vbox);

        stage.setScene(primaryScene);
        stage.setTitle("RoboRally");
        stage.setOnCloseRequest(
                e -> {
                    e.consume();
                    appController.exit();} );
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    public void createBoardView(GameController gameController) {
        // if present, remove old BoardView
        boardRoot.getChildren().clear();

        if (gameController != null) {
            // create and add view for new board
            BoardView boardView = new BoardView(gameController);
            boardRoot.setCenter(boardView);
        }

        stage.sizeToScene();
    }

    public void createHostView () {
        LobbyView lobbyView = new LobbyView(true);
        Scene lobbyScene = new Scene(lobbyView);
        stage.setScene(lobbyScene);
        stage.setTitle("RoboRally");
        stage.setMinWidth(MIN_APP_WIDTH);
        stage.show();
    }

    public void createJoinView(){
        JoinView joinView = new JoinView(true);
        Scene joinScene = new Scene(joinView);
        stage.setScene(joinScene);
        stage.setTitle("RoboRally");
        stage.setMinWidth(MIN_APP_WIDTH);
        stage.show();
    }

    public void createMenuBarView () {
        stage.close();

        AppController appController = new AppController(this);

        RoboRallyMenuBar menuBar = new RoboRallyMenuBar(appController);
        boardRoot = new BorderPane();
        VBox vbox = new VBox(menuBar, boardRoot);
        vbox.setMinWidth(MIN_APP_WIDTH);
        Scene primaryScene = new Scene(vbox);

        stage.setScene(primaryScene);
        stage.setTitle("RoboRally");
        stage.setOnCloseRequest(
                e -> {
                    e.consume();
                    appController.exit();} );
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Shows a dialogue box with a text field where the player inputs a name.
     *
     * @return The name written by the user, or an empty string if cancelled.
     * @author Mirza Zia Beg (s235124) / Microsoft Copilot
     */
    public String saveMenu (String gameOrBoard) {
        final String[] input = {""};
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Save " + gameOrBoard);
        dialog.setHeaderText("Saving " + gameOrBoard + ":");

        TextField textField = new TextField();
        dialog.getDialogPane().setContent(textField);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return textField.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            input[0] = result;
            System.out.println("Name: " + input[0]);
        });

        return input[0];
    }

    /**
     * Shows a dialogue box with a list of choices.
     *
     * @param choices The list of choices from which the dialogue box will show its choices.
     * @return A choice of the choices, or null if it was cancelled.
     * @author Mirza Zia Beg (s235124)
     */
    public String loadMenu (List<String> choices, String gameOrBoard) {
        ChoiceDialog<String> cd = new ChoiceDialog<>(choices.get(0), choices);
        cd.setTitle("Load " + gameOrBoard);
        cd.setHeaderText("Loading " + gameOrBoard + ":");

        Optional<String> result = cd.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // XXX just in case we need to do something here eventually;
        //     but right now the only way for the user to exit the app
        //     is delegated to the exit() method in the AppController,
        //     so that the AppController can take care of that.
    }

    public static void main(String[] args) {
        launch(args);
    }

}