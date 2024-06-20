package dk.dtu.compute.se.pisd.roborally.model;

public class ServerPlayer {
    Integer id;

    String playerName;

    String cardStr;

    String color;

    Lobby currentLobby;

    public ServerPlayer(String playerName, String cardStr, String color, Lobby currentLobby) {
        this.id = 0;
        this.playerName = playerName;
        this.cardStr = cardStr;
        this.color = color;
       this.currentLobby = currentLobby;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getCardStr() {
        return cardStr;
    }

    public void setCardStr(String cardStr) {
        this.cardStr = cardStr;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Lobby getCurrentLobby() {
        return currentLobby;
    }

    public void setCurrentLobby(Lobby currentLobby) {
        this.currentLobby = currentLobby;
    }
}
