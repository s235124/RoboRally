package dk.dtu.compute.se.pisd.roborally.controller;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Lobby;
import dk.dtu.compute.se.pisd.roborally.model.Player;

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

    public static Lobby getLobbyById(Integer lobbyId) throws Exception {
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
        HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/lobbies/" + lobbyID))
                .setHeader("User-Agent", "Product Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> getResponse =
                httpClient.sendAsync(getRequest, HttpResponse.BodyHandlers.ofString());

        String getResult = getResponse.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
        System.out.println("GET Response: " + getResult);

        Gson gson = new Gson();
        Lobby newLobby = gson.fromJson(getResult, Lobby.class);

        if (newLobby.getPlayers() != null) {
                if (newLobby.getPlayers().size() >= newLobby.getMaxPlayerCount())
                        System.out.println("Lobby is full");
                        return false;
        }

        newLobby.addPlayer(player);

        String json = gson.toJson(newLobby);
        System.out.println("Updated Lobby JSON: " + json);

        HttpRequest putRequest = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/lobbies/" + lobbyID))
                .setHeader("User-Agent", "Product Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> putResponse =
                httpClient.sendAsync(putRequest, HttpResponse.BodyHandlers.ofString());
        String putResult = putResponse.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
        System.out.println(putResult);
        return true;
    }
    public static String getProduct() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/products/100"))
                .setHeader("User-Agent", "Product Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
        System.out.println("PUT Response: " + result);
        return result;
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
