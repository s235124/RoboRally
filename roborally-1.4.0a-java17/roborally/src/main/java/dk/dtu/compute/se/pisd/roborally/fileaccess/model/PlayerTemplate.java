package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class PlayerTemplate {
    public String name;
    public String color;

    public int spaceX;
    public int spaceY;
    public int points;

    public Heading heading;

    public CommandCardFieldTemplate[] program = new CommandCardFieldTemplate[5];
    public CommandCardFieldTemplate[] cards = new CommandCardFieldTemplate[8];
}
