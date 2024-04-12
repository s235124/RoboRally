package dk.dtu.compute.se.pisd.roborally.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.io.IOException;

public class BoardAdapter extends TypeAdapter<Board> {
    @Override
    public void write(JsonWriter out, Board board) throws IOException {
        assert board != null;
        out.beginObject();
        out.name("phase").value(board.getPhase().toString());
        out.endObject();
    }

    @Override
    public Board read(JsonReader in) throws IOException {
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
        return board;
    }
}
