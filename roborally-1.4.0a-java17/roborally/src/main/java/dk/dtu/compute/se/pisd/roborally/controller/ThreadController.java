package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.ServerPlayer;
import javafx.application.Platform;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadController implements Runnable {

    private volatile boolean flag = true;

    int id;

    AppController appController;

    public ThreadController(AppController appController, int id) {
        this.appController = appController;
        this.id = id;
    }

    @Override
    public void run() {
        while (flag) {
            // Polling
            boolean result = pollServer();

            System.out.println(result);

            if (result) {
                startGame();
                flag = false;
            }

            try { // I sleep
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Handle the interruption appropriately
            }
        }
    }

    private boolean pollServer() {
        try {
            return HttpController.isReady(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startGame () {
        List<ServerPlayer> players;

        try {
            players = HttpController.getPlayersFromLobbyID(this.appController.currentLobbyID);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        this.appController.gameController.board.clearPlayers();
        Board board = this.appController.gameController.board;

        AtomicInteger i = new AtomicInteger();
        players.forEach(player -> {
            Player player1 = new Player(board, player.getColor(), player.getPlayerName());
            player1.cardStr = player.getCardStr();
            player1.setSpace(this.appController.gameController.board.getSpace(i.get(), i.get()));
            this.appController.gameController.board.addPlayer(player1);
            i.getAndIncrement();
        });

        this.appController.gameController.startProgrammingPhase();
        Platform.runLater(() -> this.appController.createBoardView());
    }

    public void stopPolling() {
        flag = false;
    }
}
