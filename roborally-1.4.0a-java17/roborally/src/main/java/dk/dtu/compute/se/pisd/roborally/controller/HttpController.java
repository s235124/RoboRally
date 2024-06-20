package dk.dtu.compute.se.pisd.roborally.controller;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Lobby;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.ServerPlayer;

public class HttpController {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static String addLobbyToServer (Board board) throws Exception {
        Lobby lobby = new Lobby(board.maxPlayers, board.map, true, false);
        Gson gson = new Gson();

        String json = gson.toJson(lobby);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/lobbies"))
                .setHeader("User-Agent", "Product Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        return response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
    }

    public static List<Lobby> getLobbies() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/lobbies"))
                .setHeader("User-Agent", "Product Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);

        System.out.println(result);

        Gson gson = new Gson();
        Type lobbyListType = new TypeToken<List<Lobby>>(){}.getType();
        List<Lobby> lobbies = gson.fromJson(result, lobbyListType);
        System.out.println(lobbies);
        return lobbies;
    }

    public static Lobby getLobbyById(int lobbyId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/lobbies/" + lobbyId))
                .setHeader("User-Agent", "Product Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);

        System.out.println(result);

        Gson gson = new Gson();
        Lobby lobby = gson.fromJson(result, Lobby.class);
        System.out.println(lobby);
        return lobby;
    }

    public static boolean addPlayerToLobby (int lobbyID, Player player) throws Exception {
        

        Lobby newLobby = getLobbyById(lobbyID);

        if (newLobby.getPlayers() == null) {
                newLobby.setPlayers(new ArrayList<>());
             }
     
        if (newLobby.getPlayers().size() >= newLobby.getMaxPlayerCount())
                return false;

        
            ServerPlayer sPlayer = new ServerPlayer(player.getName(), player.cardStr, player.getColor(),newLobby);
        Gson gson = new Gson();
        String json = gson.toJson(sPlayer);

        System.out.println(json);

        HttpRequest getRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/lobbies/" + lobbyID + "/players"))
                .setHeader("User-Agent", "Product Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> getResponse =
                httpClient.sendAsync(getRequest, HttpResponse.BodyHandlers.ofString());
        String getResult = getResponse.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);

        System.out.println(getResult);


        if (newLobby.getPlayers().size() >= newLobby.getMaxPlayerCount())
            return false;

        newLobby.addPlayer(sPlayer);

        String json1 = gson.toJson(newLobby);

        HttpRequest putRequest = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(json1))
                .uri(URI.create("http://localhost:8080/lobbies/" + lobbyID))
                .setHeader("User-Agent", "Product Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> putResponse =
                httpClient.sendAsync(putRequest, HttpResponse.BodyHandlers.ofString());
        String putResult = putResponse.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
        System.out.println(putResult);
        System.out.println(json1);
        return true;
    }

    public static int getPlayerCountFromLobby (int lobbyID) throws Exception {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/lobbies/" + lobbyID + "/NoP"))
                .setHeader("User-Agent", "Product Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> getResponse =
                httpClient.sendAsync(getRequest, HttpResponse.BodyHandlers.ofString());
        String getResult = getResponse.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
        return 0;
    }

    public static List<String> getPlayerIdFromLobby (int lobbyID) throws Exception {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/lobbies/" + lobbyID))
                .setHeader("User-Agent", "Product Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> getResponse =
                httpClient.sendAsync(getRequest, HttpResponse.BodyHandlers.ofString());
        String getResult = getResponse.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
        return null;
    }

//    @Override
//    public Product getProductById(int id) {
//        HttpRequest request = HttpRequest.newBuilder()
//                .GET()
//                .uri(URI.create("http://localhost:8080/products/" + id))
//                .setHeader("User-Agent", "Product Client")
//                .header("Content-Type", "application/json")
//                .build();
//        CompletableFuture<HttpResponse<String>> response =
//                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
//        String result = null;
//        try {
//            result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Gson gson = new Gson();
//        Product product = gson.fromJson(result, Product.class);
//        return product;
//    }
//
//    @Override
//    public boolean addProduct(Product p) {
//        Gson gson = new Gson();
//        String json = gson.toJson(p);
//        HttpRequest request = HttpRequest.newBuilder()
//                .POST(HttpRequest.BodyPublishers.ofString(json))
//                .uri(URI.create("http://localhost:8080/products"))
//                .setHeader("User-Agent", "Product Client")
//                .header("Content-Type", "application/json")
//                .build();
//        CompletableFuture<HttpResponse<String>> response =
//                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
//        String result = null;
//        try {
//            result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result != null;
//    }
//
//    @Override
//    public boolean updateProduct(int id, Product p) {
//        Gson gson = new Gson();
//        String json = gson.toJson(p);
//        HttpRequest request = HttpRequest.newBuilder()
//                .PUT(HttpRequest.BodyPublishers.ofString(json))
//                .uri(URI.create("http://localhost:8080/products/" + id))
//                .setHeader("User-Agent", "Product Client")
//                .header("Content-Type", "application/json")
//                .build();
//        CompletableFuture<HttpResponse<String>> response =
//                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
//        String result = null;
//        try {
//            result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result != null;
//    }
//
//    @Override
//    public boolean deleteProductById(int id) {
//        HttpRequest request = HttpRequest.newBuilder()
//                .DELETE()
//                .uri(URI.create("http://localhost:8080/products/" + id))
//                .setHeader("User-Agent", "Product Client")
//                .header("Content-Type", "application/json")
//                .build();
//        CompletableFuture<HttpResponse<String>> response =
//                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
//        String result = null;
//        try {
//            result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result != null;
//    }
}
