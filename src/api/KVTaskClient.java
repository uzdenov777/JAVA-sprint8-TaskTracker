package api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;


public class KVTaskClient {
    private final String kvServerUrl;
    private final HttpClient httpClient;
    private String apiTokenServer;

    public KVTaskClient(String kvServerUrl) {
        this.kvServerUrl = kvServerUrl;
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        register();
    }

    private void register() {
        try {
            URI urlRegister = URI.create(kvServerUrl + "/register");
            HttpRequest request = HttpRequest.newBuilder().GET().uri(urlRegister).timeout(Duration.ofSeconds(5)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() == 200) {
                apiTokenServer = response.body();
                System.out.println("Регистрация клиента прошла успешно!");
            } else {
                System.out.println("Ожидался ответ от сервера 200, пришел: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Возникла проблема при регистрации");
        }
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        try {
            URI uriSave = URI.create(kvServerUrl + "/save/" + key + "?API_TOKEN=" + apiTokenServer);
            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json)).uri(uriSave).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Сервер-хранилище не вернул код ответа 200. Код ответа: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка у KVTaskClient во время сохранения по ключу: " + key);
        }
    }

    public String load(String key) throws IOException, InterruptedException {
        try {
            URI urlLoad = URI.create(kvServerUrl + "/load/" + key + "?API_TOKEN=" + apiTokenServer);
            HttpRequest request = HttpRequest.newBuilder().GET().uri(urlLoad).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Сервер-хранилище не вернул код ответа 200. Код ответа: " + response.statusCode());
                return "";
            }
        } catch (Exception e) {
            System.out.println("Возникла проблема во время загрузки по ключу:" + key);
            return "";
        }
    }
}
