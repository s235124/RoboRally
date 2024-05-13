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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60; // 75;
    final public static int SPACE_WIDTH = 60; // 75;

    public final Space space;


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        File img = new File("roborally/images/empty.png");

        String absName = img.getAbsolutePath();

        StringBuilder realAbsName = new StringBuilder().append("file:");

        for (int i = 0; i < absName.length(); i++) {
            if (absName.charAt(i) == ' ') {
                realAbsName.append("%20");
                continue;
            }
            if (absName.charAt(i) == '\\') {
                realAbsName.append("/");
                continue;
            }
            realAbsName.append(absName.charAt(i));
        }


        this.setStyle("-fx-background-image: url(" + realAbsName + "); " +
                "-fx-background-repeat: stretch; " +
                "-fx-background-size: " + SPACE_WIDTH + " " + SPACE_HEIGHT + "; " +
                "-fx-background-position: center center;");

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
        drawCheckpoints();
        drawWalls(); // Tilføj dette kald for at tegne væggene ved initialisering
    }

    /**
     * A method to draw the players every time it is called.
     * @author Mirza Zia Beg (s235124)
     */
    private void updatePlayer() {
        this.getChildren().clear();

        Player player = space.getPlayer();
        if (player != null) {
            ImageView playerImgView = getPlayerImage(player);

            playerImgView.setFitWidth(SPACE_WIDTH * 0.8);
            playerImgView.setFitHeight(SPACE_HEIGHT * 0.8);

            playerImgView.setRotate((90 * player.getHeading().ordinal()) % 360);
            this.getChildren().add(playerImgView);
        }
    }

    /**
     * Gets an image of the player based on their color.
     * @param player The player whose icon is needed to be found
     * @return An ImageView of the player
     * @author Mirza Zia Beg (s235124)
     */
    private ImageView getPlayerImage(Player player) {
        int robot = 0;

        switch (player.getColor()) {
            case "red":
                robot = 1;
                break;
            case "green":
                robot = 2;
                break;
            case "blue":
                robot = 3;
                break;
            case "orange":
                robot = 4;
                break;
            case "grey":
                robot = 5;
                break;
            case "magenta":
                robot = 6;
                break;
            default:
                break;
        }

        File img = new File("roborally/images/r" + robot + ".png");

        String absName = img.getAbsolutePath();

        StringBuilder realAbsName = new StringBuilder();

        for (int i = 0; i < absName.length(); i++) {
            if (absName.charAt(i) == ' ') {
                realAbsName.append("%20");
                continue;
            }
            if (absName.charAt(i) == '\\') {
                realAbsName.append("/");
                continue;
            }
            realAbsName.append(absName.charAt(i));
        }

        Image playerImg = new Image("file:" + realAbsName);
        return new ImageView(playerImg);
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            this.getChildren().clear(); // Fjerner gamle børneelementer før opdatering
            drawCheckpoints();
            updatePlayer(); // Derefter opdaterer vi spilleren
            drawWalls(); // Tegner væggene først
        }
    }

    /**
     * Draws the checkpoints on the spaces where they should be.
     * @author Mirza Zia Beg (s235124)
     */
    public void drawCheckpoints () {
        for (int i = 0; i < space.board.checkpointSpaces.size(); i++) {
            if (space.board.checkpointSpaces.get(i).charAt(0) - '0' == space.x &&
                    space.board.checkpointSpaces.get(i).charAt(2) - '0' == space.y) {
                File fimg = new File("roborally/images/" + (i + 1) + ".png");

                String absName = fimg.getAbsolutePath();

                StringBuilder realAbsName = new StringBuilder().append("file:");

                for (int j = 0; j < absName.length(); j++) {
                    if (absName.charAt(j) == ' ') {
                        realAbsName.append("%20");
                        continue;
                    }
                    if (absName.charAt(j) == '\\') {
                        realAbsName.append("/");
                        continue;
                    }
                    realAbsName.append(absName.charAt(j));
                }

                this.setStyle("-fx-background-image: url(" + realAbsName + "); " +
                        "-fx-background-repeat: stretch; " +
                        "-fx-background-size: " + SPACE_WIDTH + " " + SPACE_HEIGHT + "; " +
                        "-fx-background-position: center center;");
            }
        }
    }

    /**
     * @author Melih Kelkitli, s235114
     */
    private void drawWalls() {
        // Vi antager, at væggene repræsenteres som en liste af Heading objekter i space objektet.
        List<Heading> walls = space.getWalls();

        File fimg = new File("roborally/images/wall.png");
        String absName = fimg.getAbsolutePath();

        StringBuilder realAbsName = new StringBuilder();

        for (int i = 0; i < absName.length(); i++) {
            if (absName.charAt(i) == ' ') {
                realAbsName.append("%20");
                continue;
            }
            if (absName.charAt(i) == '\\') {
                realAbsName.append("/");
                continue;
            }
            realAbsName.append(absName.charAt(i));
        }

        Image img = new Image("file:" + realAbsName);

        ImageView imgv = new ImageView(img);

        imgv.setFitWidth(5);
        imgv.setFitHeight(60);

        for (Heading wall : walls) {
            switch (wall) {
                case NORTH, SOUTH:
                    imgv.setRotate(90);
                    break;
                case EAST, WEST:
                    imgv.setRotate(180);
                    break;
            }

            this.getChildren().add(imgv);
        }
    }
}

