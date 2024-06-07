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
package dk.dtu.compute.se.pisd.roborally.model;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.*;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    public List<Space> holes;

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    private int stepCounter;

    public List<String> checkpointSpaces;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        holes = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;
        checkpointSpaces = new ArrayList<>();
    }

    public void addHole(int x, int y) {
        if (isValidSpace(x, y)) {
            Space space = getSpace(x, y);
            space.setHole(true);
            holes.add(space);
        }
    }

    private boolean isValidSpace(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public Player iterateSpace(Player shooter){
        int x = shooter.getSpace().x;
        int y = shooter.getSpace().y;

        if (shooter.getHeading() == Heading.NORTH || shooter.getHeading() == Heading.SOUTH)
            for (int i = 0; i < height; i++){
                if(spaces[x][i].getPlayer() != null && spaces[x][i].getPlayer() != shooter){
                    return spaces[x][i].getPlayer();
                }
            }
        else
            for (int i = 0; i < width; i++){
                if(spaces[i][y].getPlayer() != null && spaces[i][y].getPlayer() != shooter){
                    return spaces[i][y].getPlayer();
                }
            }
        return null;
    }


    public void laserBeam(Player shooter){

        Player v = iterateSpace(shooter);
        if(v == null){
            System.out.println("v");
            return;
        }
        System.out.println("v's y coord is "+ v.getSpace().y);

        if(shooter.getHeading() == Heading.SOUTH && shooter.getSpace().y < v.getSpace().y){
            Heading heading = v.getHeading();
            v.setHeading(heading.next().next());
            System.out.println("ve");
            //animation for later
            //Platform.runLater(() -> {
            //LaserBeam laserBeam = new LaserBeam(shooter.getSpace().x*60, shooter.getSpace().y+60, v.getSpace().x*60, v.getSpace().y+60);
            // Add the laser beam to
            //mainBoardPane.getChildren().add(laserBeam);
            //laserBeam.animate();
            //});
        }
        else if(shooter.getHeading() == Heading.NORTH && shooter.getSpace().y > v.getSpace().y){
            Heading heading = v.getHeading();
            v.setHeading(heading.next().next());
            System.out.println("vee");
        }
        else if(shooter.getHeading() == Heading.WEST && shooter.getSpace().x > v.getSpace().x){
            Heading heading = v.getHeading();
            v.setHeading(heading.next().next());
            System.out.println("vee");
        }
        else if(shooter.getHeading() == Heading.EAST && shooter.getSpace().x < v.getSpace().x){
            Heading heading = v.getHeading();
            v.setHeading(heading.next().next());
            System.out.println("vee");
        }
    }

    public void setPlayers(List<Player> players) {
        for (Player player : players) {
            Player newPlayer = new Player(this, player.getColor(), player.getName());
            newPlayer.setHeading(player.getHeading());
            newPlayer.setSpace(new Space(this, player.getSpace().x, player.getSpace().y));
            newPlayer.setCardFields(player.getCards());
            newPlayer.setProgramCards(player.getProgramFields());
            this.players.add(newPlayer);
        }
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    public void setSpace (int x, int y, Space space) {
        spaces[x][y] = space;
    }

    public int getPlayersNumber() {
        return players.size();
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    public Player getCurrentPlayer() {
        return current;
    }

    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public int getStepCounter(){
        return stepCounter;
    }

    public void setStepCounter(){
        stepCounter++;
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     *
     * @author Melih Kelkitli, s235114
     *
     */
    public void addWallToSpace(int x, int y, Heading direction) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            spaces[x][y].addWall(direction);
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        if (space.getWalls().contains(heading)) {
            return null;
        }
        // TODO needs to be implemented based on the actual spaces
        //      and obstacles and walls placed there. For now it,
        //      just calculates the next space in the respective
        //      direction in a cyclic way.

        // XXX an other option (not for now) would be that null represents a hole
        //     or the edge of the board in which the players can fall

        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                if (y >= height - 1)
                    return null;
                y = y + 1;
                break;
            case WEST:
                if (x <= 0)
                    return null;
                x = x - 1;
                break;
            case NORTH:
                if (y <= 0)
                    return null;
                y = y - 1;
                break;
            case EAST:
                if (x >= width - 1)
                    return null;
                x = x + 1;
                break;
        }
        Heading reverse = Heading.values()[(heading.ordinal() + 2)% Heading.values().length];
        Space result = getSpace(x, y);
        if (result != null) {
            if (result.getWalls().contains(reverse)) {
                return null;
            }
        }
        return result;
    }

    public String toString () {
        return width + ", " + height + ", current player is " + current.getName() + ", gameId=" + gameId + ", phase=" + phase + ", step=" + step + ", stepMode=" + stepMode;
    }

    public String getStatusMessage() {

        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", Step: " + getStep() +
                ", moves: " + getStepCounter() +
                ", Player Points = " + getCurrentPlayer().points;
    }
}
