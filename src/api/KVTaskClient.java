package api;

import com.google.gson.Gson;
import model.Task;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


public class KVTaskClient {
    private final String url;
    private final HttpClient httpClient;
    private String apiTokenServer;
    private Gson gson;

    public KVTaskClient() {
        url = "http://localhost:8080";
        httpClient = HttpClient.newHttpClient();
        gson = new Gson();
        register();
    }

    private void register() {
        String URLRegister = url + "/register";
        try {
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(URLRegister)).build();
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

    public void addTask(Task task) {
        try {
            String URLAdd = url + "/addTask";
            URI uri = URI.create(URLAdd+"?API_TOKEN="+apiTokenServer);
            String toGsonTask = gson.toJson(task);
            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(toGsonTask)).uri(uri).build();
            HttpResponse<String> response = httpClient.send(request,HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            String responseBody = response.body();
        }catch (Exception e){
            System.out.println("Произошла ошибка при отправке запроса на сервер для добавлении задачи");
        }
    }

    public String getApiTokenServer() {
        return apiTokenServer;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public String getUrl() {
        return url;
    }
}
