package dk.dtu.compute.se.pisd.roborally.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.io.IOException;

public class PlayerAdapter extends TypeAdapter<Player> {
    @Override
    public void write(JsonWriter out, Player player) throws IOException {
        out.beginObject();
        out.name("name").value(player.getName());
        out.name("color").value(player.getColor());
        out.name("space").value(player.getSpace().x + "," + player.getSpace().y);
        out.name("heading").value(player.getHeading().name());

        out.name("program");
            out.beginArray();
                for (int i = 0; i < 5; i++)
                    out.value(player.getProgramField(i).getCard() == null ? null : player.getProgramField(i).getCard().getName());
            out.endArray();

        out.name("cards");
            out.beginArray();
                for (int i = 0; i < 8; i++)
                    out.value(player.getCardField(i).getCard() == null ? null : player.getCardField(i).getCard().getName());
            out.endArray();
        out.endObject();
    }

    @Override
    public Player read(JsonReader in) throws IOException {
        Player player = new Player();
        Board board = new Board(8,8);
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "name":
                    player.setName(in.nextString());
                    break;
                case "color":
                    player.setColor(in.nextString());
                    break;
                case "space":
                    String[] spaceName = in.nextString().split(",");
                    player.setSpace(new Space(board, Integer.parseInt(spaceName[0]), Integer.parseInt(spaceName[1])));
                    break;
                case "heading":
                    Heading h = Heading.valueOf(in.nextString());
                    player.setHeading(h);
                case "program":
                    break;
            }
        }
        in.endObject();
        return player;
    }
}
