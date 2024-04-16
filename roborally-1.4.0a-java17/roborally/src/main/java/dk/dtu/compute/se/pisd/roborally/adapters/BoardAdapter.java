package dk.dtu.compute.se.pisd.roborally.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoardAdapter extends TypeAdapter<Board> {
    @Override
    public void write(JsonWriter out, Board board) throws IOException {
        assert board != null;
        out.beginObject();
        out.name("width").value(board.width);
        out.name("height").value(board.height);
        out.name("gameid").value(board.getGameId() == null ? 0 : board.getGameId());
        out.name("currentplayercolor").value(board.getCurrentPlayer().getColor());
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            out.name("player");
            out.beginObject();
                out.name("name").value(player.getName());
                out.name("color").value(player.getColor());
                out.name("space").value(player.getSpace().x + "," + board.getPlayer(i).getSpace().y);
                out.name("heading").value(player.getHeading().toString());

                out.name("program");
                out.beginArray();
                for (int j = 0; j < 5; j++)
                    out.value(player.getProgramField(j).getCard() == null ? null : player.getProgramField(j).getCard().commandAsString());
                out.endArray();

                out.name("cards");
                out.beginArray();
                for (int j = 0; j < 8; j++)
                    out.value(player.getCardField(j).getCard() == null ? null : player.getCardField(j).getCard().commandAsString());
                out.endArray();
            out.endObject();
        }
        out.name("phase").value(board.getPhase().toString());
        out.name("step").value(board.getStep());
        out.name("stepmode").value(board.isStepMode());
        out.endObject();
    }

    @Override
    public Board read(JsonReader in) throws IOException {
        Board board = new Board(0,0);
        List<Player> players = new ArrayList<Player>();
        int width = 0;
        int height = 0;
        String currentPlayerColor = "";
        int gameId = 0;
        Phase phase = null;
        int step = 0;
        boolean stepMode = false;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "width":
                    width = in.nextInt();
                    break;
                case "height":
                    height = in.nextInt();
                    break;
                case "gameid":
                    gameId = in.nextInt();
                    break;
                case "currentplayercolor":
                    currentPlayerColor = in.nextString();
                    break;
                case "phase":
                    phase = Phase.valueOf(in.nextString());
                    break;
                case "step":
                    step = in.nextInt();
                    break;
                case "stepmode":
                    stepMode = in.nextBoolean();
                    break;
                case "player":
                    boolean hasCompletedRound = false;
                    String name = "";
                    String color = "";
                    Space space = new Space(null, 0, 0);
                    Heading heading = Heading.SOUTH;
                    CommandCard[] programCards = new CommandCard[5];
                    CommandCard[] cardCards = new CommandCard[8];
                    in.beginObject();
                    while (in.hasNext()) {
                        switch (in.nextName()) {
                            case "name":
                                name = in.nextString();
                                System.out.println(name);
                                break;
                            case "color":
                                color = in.nextString();
                                System.out.println(color);
                                break;
                            case "space":
                                String[] split = in.nextString().split(",");
                                space = new Space(board, Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                                System.out.println(space);
                                break;
                            case "heading":
                                heading = Heading.valueOf(in.nextString());
                                System.out.println(heading);
                                break;
                            case "program":
                                in.beginArray();
                                for (int i = 0; i < 5; i++) {
                                    try {
                                        String command = in.nextString();
                                        System.out.println(command);
                                        programCards[i] = new CommandCard(findCommand(command));

                                    }
                                    catch (Exception e) {
                                        programCards[i] = null;
                                        in.skipValue();
                                    }
                                }
                                in.endArray();
                                System.out.println("Program field done!");
                                break;
                            case "cards":
                                in.beginArray();
                                for (int i = 0; i < 8; i++) {
                                    try {
                                        String command = in.nextString();
                                        System.out.println(command);
                                        cardCards[i] = new CommandCard(findCommand(command));

                                    }
                                    catch (Exception e) {
                                        cardCards[i] = null;
                                        in.skipValue();
                                    }
                                }
                                in.endArray();
                                System.out.println("Card field done");
                                hasCompletedRound = true;
                                break;
                            default:
                                in.skipValue(); // Shouldnt happen but who knows :|
                                break;
                        }
                        if (hasCompletedRound) break;
                    }
                    in.endObject();
                    players.add(new Player(board, color, name));
                    CommandCardField[] programField = new CommandCardField[5];
                    CommandCardField[] cardField = new CommandCardField[8];

                    for (int i = 0; i < 5; i++) {
                        programField[i] = new CommandCardField(players.get(players.size() - 1));
                        programField[i].setCard(programCards[i]);
                    }
                    for (int i = 0; i < 8; i++) {
                        cardField[i] = new CommandCardField(players.get(players.size() - 1));
                        cardField[i].setCard(cardCards[i]);
                    }
                    players.get(players.size() - 1).setSpace(space);
                    players.get(players.size() - 1).setHeading(heading);
                    players.get(players.size() - 1).setProgramCards(programField);
                    players.get(players.size() - 1).setCardFields(cardField);
                    break;
                default:
                    in.skipValue(); // Shouldnt happen but who knows :|
                    break;
            }
        }
        in.endObject();
        board = new Board(width, height);
        board.setGameId(gameId);
        board.setPhase(phase);
        board.setStep(step);
        board.setStepMode(stepMode);
        board.setPlayers(players);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            if (currentPlayerColor.equals(board.getPlayer(i).getColor()))
                board.setCurrentPlayer(board.getPlayer(i));
            System.out.println(board.getPlayer(i).toString());
        }

        System.out.println(board.toString());
        return board;
    }

    private Command findCommand (String name) {
        return switch (name) {
            case "FORWARD" -> Command.FORWARD;
            case "FAST_FORWARD" -> Command.FAST_FORWARD;
            case "LEFT" -> Command.LEFT;
            case "RIGHT" -> Command.RIGHT;
            case "OPTION_LEFT_RIGHT" -> Command.OPTION_LEFT_RIGHT;
            default -> null;
        };
    }
}
