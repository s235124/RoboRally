package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.ServerPlayer;

import java.util.List;

public class ReceiveCardsThread implements Runnable {
    private volatile boolean flag = true;

    int id;

    GameController gameController;

    public ReceiveCardsThread(GameController gameController, int id) {
        this.gameController = gameController;
        this.id = id;
    }

    @Override
    public void run() {
        while (flag) {
            // Polling
            boolean result = pollServer();

            System.out.println(result);

            if (result) {
                flag = false;
            }

            try { // I sleep
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Handle the interruption appropriately
            }
        }
    }

    private boolean pollServer() {
        try {
            List<ServerPlayer> players = HttpController.getPlayersFromLobbyID(id);
            for (int i = 0; i < players.size(); i++) {
                ServerPlayer player = players.get(i);
                if (HttpController.receiveCardsByColor(id, player.getColor()).equals("") || HttpController.receiveCardsByColor(id, player.getColor()).equals("10")) {
                    System.out.println("Empty");
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stopPolling() {
        flag = false;
    }
}
