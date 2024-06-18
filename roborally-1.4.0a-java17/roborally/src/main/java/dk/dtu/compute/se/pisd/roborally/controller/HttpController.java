package dk.dtu.compute.se.pisd.roborally.controller;

import com.google.gson.Gson;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Lobby;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class HttpController {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static String addLobbyToServer (Board board) throws Exception {
        Lobby lobby = new Lobby(board.maxPlayers, 0,true, false);
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

        Gson gson = new Gson();
        List<Lobby> lobbies = gson.fromJson(result, List.class);
        System.out.println(lobbies);
        return lobbies;
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
