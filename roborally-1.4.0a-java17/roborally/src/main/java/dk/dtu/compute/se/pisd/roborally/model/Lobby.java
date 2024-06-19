package dk.dtu.compute.se.pisd.roborally.model;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    Integer lobbyID;

    int maxPlayerCount;
    int currentPlayerCount;

    Integer map;

    boolean visibility;

    boolean ready;

    List<Player> players;

    public Lobby (int maxPlayerCount, int map, boolean visibility, boolean ready) {
        this.lobbyID = null;
        this.maxPlayerCount = maxPlayerCount;
        this.currentPlayerCount = 0;
        this.map = map;
        this.visibility = visibility;
        this.ready = ready;
        players = new ArrayList<>();
    }

    public Integer getLobbyID() {
        return lobbyID;
    }

    public void setLobbyID(Integer lobbyID) {
        this.lobbyID = lobbyID;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public void setMaxPlayerCount(int maxPlayerCount) {
        this.maxPlayerCount = maxPlayerCount;
    }

    public int getCurrentPlayerCount() {
        return currentPlayerCount;
    }

    public void setCurrentPlayerCount(int currentPlayerCount) {
        this.currentPlayerCount = currentPlayerCount;
    }

    public int getMap() {
        return map;
    }

    public void setMap(int map) {
        this.map = map;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer (Player player) {
        players.add(player);
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
