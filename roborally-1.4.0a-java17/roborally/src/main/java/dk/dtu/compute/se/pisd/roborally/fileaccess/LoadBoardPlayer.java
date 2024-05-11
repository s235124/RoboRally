package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.CommandCardFieldTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LoadBoardPlayer {
    private static final String BOARDSFOLDER = "boards";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";

    /**
     * A method to load a board from a file
     * @param boardname
     * @return A board with the name boardname
     * @author Mirza Zia Beg (s235124)
     */
    public static Board loadBoardPlayer(String boardname) {
        if (boardname == null) {
            boardname = DEFAULTBOARD;
        }

        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(BOARDSFOLDER + "/" + boardname + "." + JSON_EXT);
        if (inputStream == null) {
            // TODO these constants should be defined somewhere
            return new Board(8,8);
        }

        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        Board result;
        // FileReader fileReader = null;
        JsonReader reader = null;
        try {
            // fileReader = new FileReader(filename);
            reader = gson.newJsonReader(new InputStreamReader(inputStream));
            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

            result = new Board(template.width, template.height);
            result.setGameId(template.gameID != null ? template.gameID : -1);
            result.setPhase(template.phase);
            result.setStep(template.step);
            result.setStepMode(template.stepMode);
            result.checkpointSpaces.addAll(template.checkpointSpaces);

            for (SpaceTemplate spaceTemplate: template.spaces) {
                Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {
                    space.getActions().addAll(spaceTemplate.actions);
                    space.getWalls().addAll(spaceTemplate.walls);
                }
            }

            for (PlayerTemplate playerTemplate: template.players) {
                Player player = new Player(result, playerTemplate.color, playerTemplate.name);
                player.setSpace(result.getSpace(playerTemplate.spaceX, playerTemplate.spaceY));
                player.setHeading(playerTemplate.heading);
                player.points = playerTemplate.points;
                CommandCardField[] programs = new CommandCardField[5];
                CommandCardField[] cards = new CommandCardField[8];

                for (int i = 0; i < 5; i++) {
                    if (playerTemplate.program[i] != null) {
                        programs[i] = new CommandCardField(player);
                        programs[i].setCard(playerTemplate.program[i].card);
                        programs[i].setVisible(playerTemplate.program[i].visible);
                    }
                }
                for (int i = 0; i < 8; i++) {
                    if (playerTemplate.cards[i] != null) {
                        cards[i] = new CommandCardField(player);
                        cards[i].setCard(playerTemplate.cards[i].card);
                        cards[i].setVisible(playerTemplate.cards[i].visible);
                    }
                }
                player.setProgramCards(programs);
                player.setCardFields(cards);
                result.addPlayer(player);
                if (playerTemplate.name.equals(template.currentPlayerName))
                    result.setCurrentPlayer(player);
            }

            reader.close();
            return result;
        } catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    inputStream = null;
                } catch (IOException e2) {}
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {}
            }
        }
        return null;
    }

    /**
     * A method to save a board
     * @param board The board to be saved
     * @param name The name of the file
     * @author Mirza Zia Beg (s235124)
     */
    public static void saveBoardPlayer(Board board, String name) {
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;
        template.gameID = board.getGameId();
        template.step = board.getStep();
        template.stepMode = board.isStepMode();
        template.phase = board.getPhase();
        template.checkpointSpaces = new ArrayList<>();

        for (int i=0; i<board.width; i++) {
            for (int j=0; j<board.height; j++) {
                Space space = board.getSpace(i,j);
                if (!space.getWalls().isEmpty() || !space.getActions().isEmpty()) {
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;
                    spaceTemplate.actions.addAll(space.getActions());
                    spaceTemplate.walls.addAll(space.getWalls());
                    template.spaces.add(spaceTemplate);
                    continue;
                }
                for (int k = 0; k < board.checkpointSpaces.size(); k++) {
                    int x = board.checkpointSpaces.get(k).charAt(0) - '0';
                    int y = board.checkpointSpaces.get(k).charAt(2) - '0';
                    if (space.x == x && space.y == y) {
                        template.checkpointSpaces.add(board.checkpointSpaces.get(k));
                    }
                }
            }
        }

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            PlayerTemplate playerTemplate = new PlayerTemplate();
            playerTemplate.name = player.getName();
            playerTemplate.color = player.getColor();
            playerTemplate.spaceX = player.getSpace().x;
            playerTemplate.spaceY = player.getSpace().y;
            playerTemplate.points = player.points;
            playerTemplate.heading = player.getHeading();

            for (int j = 0; j < 5; j++) {
                if (player.getProgramField(j) != null) {
                    playerTemplate.program[j] = new CommandCardFieldTemplate();
                    playerTemplate.program[j].card = player.getProgramFields()[j].getCard();
                    playerTemplate.program[j].visible = player.getProgramFields()[j].isVisible();
                }
            }
            for (int j = 0; j < 8; j++) {
                if (player.getCardField(j) != null) {
                    playerTemplate.cards[j] = new CommandCardFieldTemplate();
                    playerTemplate.cards[j].card = player.getCards()[j].getCard();
                    playerTemplate.cards[j].visible = player.getCards()[j].isVisible();
                }
            }
            template.players.add(playerTemplate);

            if (board.getCurrentPlayer().equals(player)) {
                template.currentPlayerName = playerTemplate.name;
            }
        }


        ClassLoader classLoader = LoadBoardPlayer.class.getClassLoader();

        String filename =
                classLoader.getResource(BOARDSFOLDER).getPath() + "/" + name + "." + JSON_EXT;

        StringBuilder actualFilename = new StringBuilder();

        for (int i = 0; i < filename.length(); i++) {
            char c = filename.charAt(i);
            if (c == '%') {
                i += 2;
                c = ' ';
            }
            actualFilename.append(c);
        }

        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(actualFilename.toString());
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(template, template.getClass(), writer);
            writer.close();
        } catch (IOException e1) {
            System.out.println(e1.getMessage());
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }
}
