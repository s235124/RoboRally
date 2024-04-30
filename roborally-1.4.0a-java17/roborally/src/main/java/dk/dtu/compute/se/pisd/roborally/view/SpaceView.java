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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
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

        this.setStyle("-fx-background-image: url('file:C:/Users/ziabe/Documents/Uniting/Sem%202/Advanced%20Programming/roborally-1.2.0a-java17/roborally-1.4.0a-java17/roborally/images/empty.png'); " +
                "-fx-background-repeat: stretch; " +
                "-fx-background-size: " + SPACE_WIDTH + " " + SPACE_HEIGHT + "; " +
                "-fx-background-position: center center;");

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
        drawWalls(); // Tilføj dette kald for at tegne væggene ved initialisering
    }

    private void updatePlayer() {
        this.getChildren().clear();

        Player player = space.getPlayer();
        if (player != null) {
            ImageView playerImgView = getPlayerImage(player);

//            Polygon arrow = new Polygon(0.0, 0.0,
//                    10.0, 20.0,
//                    20.0, 0.0 );
//            try {
//                arrow.setFill(Color.valueOf(player.getColor()));
//            } catch (Exception e) {
//                arrow.setFill(Color.MEDIUMPURPLE);
//            }

            playerImgView.setFitWidth(SPACE_WIDTH * 0.8);
            playerImgView.setFitHeight(SPACE_HEIGHT * 0.8);

            playerImgView.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(playerImgView);
        }
    }

    private ImageView getPlayerImage(Player player) {
        // long ass way of converting a relative path to absolute because url suck :(
        File img = new File("roborally/images/r" + player.getName().charAt(player.getName().length() - 1) + ".png");

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
            drawWalls(); // Tegner væggene først
            updatePlayer(); // Derefter opdaterer vi spilleren
        }
    }

    /**
     *
     * @author Melih Kelkitli, s235114
     *
     */
    private void drawWalls() {
        // Vi antager, at væggene repræsenteres som en liste af Heading objekter i space objektet.
        List<Heading> walls = space.getWalls();

        for (Heading wall : walls) {
            Line line = new Line();
            switch (wall) {
                case NORTH:
                    line.setStartX(0);
                    line.setEndX(SPACE_WIDTH);
                    line.setStartY(0);
                    line.setEndY(0);
                    break;
                case SOUTH:
                    line.setStartX(0);
                    line.setEndX(SPACE_WIDTH);
                    line.setStartY(SPACE_HEIGHT);
                    line.setEndY(SPACE_HEIGHT);
                    break;
                case EAST:
                    line.setStartX(SPACE_WIDTH);
                    line.setEndX(SPACE_WIDTH);
                    line.setStartY(0);
                    line.setEndY(SPACE_HEIGHT);
                    break;
                case WEST:
                    line.setStartX(0);
                    line.setEndX(0);
                    line.setStartY(0);
                    line.setEndY(SPACE_HEIGHT);
                    break;
            }
            line.setStroke(Color.RED); // Vælg en passende farve for væggen
            line.setStrokeWidth(5); //Vælg en passende tykkelse for væggen
            this.getChildren().add(line);
        }
    }

}
