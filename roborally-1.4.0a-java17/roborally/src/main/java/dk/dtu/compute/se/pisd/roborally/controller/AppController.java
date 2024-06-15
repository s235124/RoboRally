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
package dk.dtu.compute.se.pisd.roborally.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import org.jetbrains.annotations.NotNull;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoardPlayer;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private RoboRally roboRally;

    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * Starts a new game.
     * Includes the ability to choose which robot each player wants.
     * @author Nico Laursen (s23
     */
    public void newGame() {

        // XXX the board should eventually be created programmatically or loaded from a file
        //     here we just create an empty board with the required number of players.
        Board board = new Board(8,8);


        choosePlayerAndColors(board);

        // Since this is a new game, the board will be loaded with default checkpoints, walls and holes
        // by using this constructor (which is different from the constructor with only the board as arg).
        gameController = new GameController(board, false);

        // XXX: V2
        // board.setCurrentPlayer(board.getPlayer(0));
        gameController.startProgrammingPhase();

        roboRally.createBoardView(gameController);
    }

    public void saveBoard () {
        String fileName = roboRally.saveMenu("Board");
        if (!fileName.isEmpty())
            LoadBoard.saveBoard(gameController.board, fileName);
    }

    public void loadBoard () {
        ClassLoader classLoader = LoadBoardPlayer.class.getClassLoader();

        String path = classLoader.getResource("boards").getPath();

        StringBuilder actualPath = new StringBuilder();

        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == '%') {
                i += 2;
                c = ' ';
            }
            actualPath.append(c);
        }

        File directory = new File(actualPath.toString());
        File[] files = directory.listFiles();
        List<String> fileNames = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    int dotIndex = fileName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        fileName = fileName.substring(0, dotIndex);
                    }
                    fileNames.add(fileName);
                }
            }
        }

        System.out.println("File names: " + fileNames);

        String boardName = roboRally.loadMenu(fileNames, "Board");
        if (boardName != null)
            this.gameController = new GameController(LoadBoard.loadBoard(boardName));
    }

    public void loadBoardWithNewGame () {
        loadBoard();

        if (this.gameController == null) return;

        choosePlayerAndColors(this.gameController.board);
        this.gameController.startProgrammingPhase();
        roboRally.createBoardView(this.gameController);
    }

    public void choosePlayerAndColors (Board board) {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            // Since the UI shouldn't allow this, we trust that it won't happen
            // (but its left here just in case)
//            if (this.gameController != null) {
//                // The UI should not allow this, but in case this happens anyway.
//                // give the user the option to save the game or abort this operation!
//                if (!stopGame()) {
//                    return;
//                }
//            }

            int no = result.get();

            List<String> selectedColors = new ArrayList<>();

            for (int i = 0; i < no; i++) {
                //v
                List<String> remainingColors = new ArrayList<>(PLAYER_COLORS);
                remainingColors.removeAll(selectedColors);
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                ChoiceDialog<String> dialog2 = new ChoiceDialog<>(remainingColors.get(0), remainingColors);
                dialog.setTitle("Color Selection");
                dialog.setHeaderText("Player " + (i + 1) + ": Choose a color");
                dialog.setContentText("Color:");

                Optional<String> result2 = dialog2.showAndWait();
                if (result2.isPresent()) {
                    String selectedColor = result2.get();
                    selectedColors.add(selectedColor);
                    // Use the selected color to initialize the player
                    player.setColor(selectedColor);
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Color Selected");
                    alert.setHeaderText(null);
                    alert.setContentText("Player " + (i + 1) + " has chosen " + selectedColor + " as their color.");
                    alert.showAndWait();
                } else {
                    // User closed the dialog without selecting a color
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("No Color Selected");
                    alert.setHeaderText(null);
                    alert.setContentText("No color was selected for Player " + (i + 1) + ". Exiting...");
                    alert.showAndWait();
                    return;
                }
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
            }
        }
//        this.gameController = new GameController(board);
    }

    /**
     * Saves an ongoing game.
     * @author Mirza Zia Beg (s235124)
     */
    public void saveGame() {
        String fileName = roboRally.saveMenu("Game");

        if (!fileName.isEmpty())
            LoadBoardPlayer.saveBoardPlayer(this.gameController.board, fileName);
    }

    /**
     * Loads a previously saved game.
     * The game has to be one that was saved from a prior game, otherwise bad stuff happens.
     * @author Mirza Zia Beg (s235124)
     */
    public void loadGame() {
        ClassLoader classLoader = LoadBoardPlayer.class.getClassLoader();

        String path = classLoader.getResource("games").getPath();

        StringBuilder actualPath = new StringBuilder();

        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == '%') {
                i += 2;
                c = ' ';
            }
            actualPath.append(c);
        }

        File directory = new File(actualPath.toString());
        File[] files = directory.listFiles();
        List<String> fileNames = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    int dotIndex = fileName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        fileName = fileName.substring(0, dotIndex);
                    }
                    fileNames.add(fileName);
                }
            }
        }

        System.out.println("File names: " + fileNames);

        String boardName = roboRally.loadMenu(fileNames, "Game");
        if (boardName != null)
            this.gameController = new GameController(LoadBoardPlayer.loadBoardPlayer(boardName));

        roboRally.createBoardView(this.gameController);
    }

    public void createHostView () {
        roboRally.createHostView();
    }

    public void createJoinView(){
        roboRally.createJoinView();
    }

    public void createMenuBarView () {
        roboRally.createMenuBarView();
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            //saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}
