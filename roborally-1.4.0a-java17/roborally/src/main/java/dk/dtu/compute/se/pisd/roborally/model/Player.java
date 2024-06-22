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

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Player extends Subject {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;

    final public Board board;

    public List<String> checkpointSpacesPassedThrough;

    private String name;
    private String color;

    public String cardStr;

    private Space space;
    private Heading heading = SOUTH;

    private CommandCardField[] program;
    private CommandCardField[] cards;

    public int points;

    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;
        this.cardStr = "";

        this.space = null;

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
        this.points = 0;
        this.checkpointSpacesPassedThrough = new ArrayList<>();
    }

    public Player() {
        this.board = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public void setCardStr () {
        this.cardStr = "";
        for (int i = 0; i < this.program.length; i++) {
            if (program[i].getCard() != null)
                this.cardStr += this.program[i].getCard().command.ordinal() + (i == this.program.length - 1 ? "" : ",");
            else
                this.cardStr += "8" + (i == this.program.length - 1 ? "" : ",");
        }
    }

    public void strToCards () {
        String[] cardsAsString = this.cardStr.split(",");
        for (int i = 0; i < cardsAsString.length; i++) {
            int card = Integer.parseInt(cardsAsString[i]);
            if (card == 8)
                continue;
            Command c = Command.values()[card];
            program[i].setCard(new CommandCard(c));
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    public CommandCardField[] getProgramFields() {
        return program;
    }

    public void setProgramCards(CommandCardField[] program) {
        for (int i = 0; i < program.length; i++)
            this.program[i].setCard(program[i].getCard());
    }

    public CommandCardField getCardField(int i) {
        return cards[i];
    }

    public CommandCardField[] getCards() {
        return cards;
    }

    public void setCardFields(CommandCardField[] cards) {
        for (int i = 0; i < cards.length; i++)
            this.cards[i].setCard(cards[i].getCard());
    }

    public void died () {
        int playerNum = this.name.charAt(this.name.length() - 1) - '0' - 1;
        Space defaultSpace = board.getSpace(playerNum, playerNum);

        if (defaultSpace.getPlayer() != null) {
            board.getSpace(defaultSpace.x, defaultSpace.y).getPlayer().setSpace(board.getSpace(defaultSpace.x + 1, defaultSpace.y));
        }

        this.setSpace(defaultSpace);
        this.points = 0;
        this.checkpointSpacesPassedThrough.clear();
        System.out.println(this.name + " has died, and lost all their points, and gone back to space with coordinates (" + defaultSpace.x + ", " + defaultSpace.y + ")");
    }

    @Override
    public String toString() {
        return name + ", has color: " + color + ", at position: (" + space.x + "," + space.y + "), facing: " + heading;
    }
}
