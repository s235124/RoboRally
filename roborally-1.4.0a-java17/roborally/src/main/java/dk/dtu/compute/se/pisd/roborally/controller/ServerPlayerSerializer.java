package dk.dtu.compute.se.pisd.roborally.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dk.dtu.compute.se.pisd.roborally.model.ServerPlayer;

import java.lang.reflect.Type;

class PlayerSerializer implements JsonSerializer<ServerPlayer> {
    @Override
    public JsonElement serialize(ServerPlayer player, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", player.getId());
        jsonObject.addProperty("playerName", player.getPlayerName());
        jsonObject.addProperty("cardStr", player.getCardStr());
        jsonObject.addProperty("color", player.getColor());
        return jsonObject;
    }
}
