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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

import javafx.scene.control.ButtonBar;


import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;


/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(Board board) {
        this.board = board;
    }


    //For new game
    public GameController(Board board, boolean e) {
        this.board = board;
        initializeGame();
    }
    /**
     *
     * @author Melih Kelkitli, s235114
     *
     */
    private void initializeGame() {
        board.checkpointSpaces.add("0,2");
        board.checkpointSpaces.add("0,4");
        board.checkpointSpaces.add("0,6");
        board.checkpointSpaces.add("7,3");
        board.checkpointSpaces.add("7,5");

        for (int i = 0; i < 8; i++) {
            board.addWallToSpace(i, 7, Heading.NORTH);
        }

        board.addWallToSpace(2,0,Heading.WEST);
        board.addWallToSpace(2,1,Heading.WEST);

        board.addWallToSpace(5,0,Heading.WEST);
        board.addWallToSpace(5,1,Heading.WEST);

        board.addHole(3,2);
        board.addHole(3,4);
        board.addHole(4,3);
        board.addHole(4,5);
    }

    public void moveForward(@NotNull Player player) {
        if (player.board == board) {
            Space space = player.getSpace();
            Heading heading = player.getHeading();

            Space target = board.getNeighbour(space, heading); //target er vores næste space som ønsker spilleren skal bevæge sig til

            if (target != null && !target.hasAnyWall()) { // Tjek for væg i spillerens bevægelsesretning
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {
                    // we don't do anything here  for now; we just catch the
                    // exception so that we do no pass it on to the caller
                    // (which would be very bad style).
                }
            }
        }
    }

    /**
     *
     * @author sakariye, s235100
     *
     */
    public void fastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);
    }

    /**
     *
     * @author sakariye, s235100
     *
     */
     public void turnRight(@NotNull Player player) {
         Heading heading = player.getHeading();
         player.setHeading(heading.prev());
     }

    /**
     *
     * @author sakariye, s235100
     *
     */
    public void turnLeft(@NotNull Player player) {
        Heading heading = player.getHeading();
        player.setHeading(heading.next());
    }

    /**
     * A method for choosing whether the player wants to go left or right
     * @param player The player to be rotated
     * @author Subeer Abdirahman Awil Mohamed (s235088)
     */
     public void turnLeftOrTurnRight(Player player) {
         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
         alert.setTitle("Choose Direction");
         alert.setHeaderText("Choose which way to turn");
         alert.setContentText("Choose your option.");

         ButtonType buttonLeft = new ButtonType("Turn Left");
         ButtonType buttonRight = new ButtonType("Turn Right");

         alert.getButtonTypes().setAll(buttonLeft, buttonRight);

         Optional<ButtonType> result = alert.showAndWait();
         if (result.isPresent() && result.get() == buttonLeft) {
            turnLeft(player);
         } else if (result.isPresent() && result.get() == buttonRight) {
            turnRight(player);
         }
     }


    /**
     * A method to turn the player 180 degrees
     * @param player The player to be rotated
     * @author Mohammed Josef Ismael (s235079)
     */
   private void uTurn(@NotNull Player player) {
        Heading currentHeading = player.getHeading();
        player.setHeading(currentHeading.next().next());
   }

    /**
     * A method to move the player in the opposite direction of the players heading
     * @param player The player to be moved
     * @author Mohammed Josef Ismael (s235079)
     */
    private void BackUp(@NotNull Player player){
        if (player.board == board) {
            Space space = player.getSpace();
            Heading heading = player.getHeading().next().next();

            Space target = board.getNeighbour(space, heading);
            if (target != null && !target.hasAnyWall()) {
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {
                    // we don't do anything here  for now; we just catch the
                    // exception so that we do no pass it on to the caller
                    // (which would be very bad style).
                }
            }
        }
    }

    private void shoot (Player player) {
        board.laserBeam(player);
    }

    void moveToSpace(@NotNull Player player, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        assert board.getNeighbour(player.getSpace(), heading) == space; // make sure the move to here is possible in principle
        Player other = space.getPlayer();
        Space target = board.getNeighbour(space, heading);

        if (other != null){
            if (target != null) {
                // XXX Note that there might be additional problems with
                //     infinite recursion here (in some special cases)!
                //     We will come back to that!
                moveToSpace(other, target, heading);

                // Note that we do NOT embed the above statement in a try catch block, since
                // the thrown exception is supposed to be passed on to the caller

                assert target.getPlayer() == null : target; // make sure target is free now
            } else {
                throw new ImpossibleMoveException(player, space, heading);
            }
        }
        player.setSpace(space);

        for (int i = 0; i < 5; i++) {
            int x = board.checkpointSpaces.get(i).charAt(0) - '0';
            int y = board.checkpointSpaces.get(i).charAt(2) - '0';
            if (space.x == x && space.y == y && !player.checkpointSpacesPassedThrough.contains(x + "," + y)) {
                if (i == player.checkpointSpacesPassedThrough.size()) {
                    space.getPlayer().points++;
                    System.out.println(space.getPlayer().getName() + " has gone through checkpoint " + (i + 1) + ".");
                    space.getPlayer().checkpointSpacesPassedThrough.add(x + "," + y);
                }
            }
        }
    }

    public void moveCurrentPlayerToSpace(Space space) {
        // TODO: Import or Implement this method. This method is only for debugging purposes. Not useful for the game.
    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                    if (currentPlayer.points >= board.checkpointSpaces.size()) {
                        System.out.println(currentPlayer.getName() + " has won");

                    }
                }
                for (int j = 0; j < board.holes.size(); j++) {
                    if (currentPlayer.getSpace() == board.holes.get(j)) {
                        currentPlayer.died();
                    }
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case OPTION_LEFT_RIGHT:
                    this.turnLeftOrTurnRight(player);
                    break;
                case U_TURN:
                    this.uTurn(player);
                    break;
                case BackUp:
                    this.BackUp(player);
                    break;
                case SHOOT:
                    this.shoot(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }


    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }


    class ImpossibleMoveException extends Exception {

        private Player player;
        private Space space;
        private Heading heading;

        public ImpossibleMoveException(Player player, Space space, Heading heading) {
            super("Move impossible");
            this.player = player;
            this.space = space;
            this.heading = heading;
        }
    }

}
