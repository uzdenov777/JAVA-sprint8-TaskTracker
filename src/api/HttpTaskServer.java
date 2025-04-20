package api;

import api.gson.CreateGson;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.HttpTaskManager;
import manager.Managers;
import manager.enums.TypeEndpoint;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;


public class HttpTaskServer {
    private final static int PORT = 8080;
    private final HttpServer server;
    private final String apiToken;
    private final HttpTaskManager manager;
    private final Gson gson;


    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        apiToken = generateApiToken();
        gson = CreateGson.createGson();
        manager = Managers.getDefault();
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
        server.createContext("/tasks", this::tasks);
    }

    private void register(HttpExchange httpExchange) {
        System.out.println("Принят запрос от клиента на пути /register");
        try {
            if ("GET".equals(httpExchange.getRequestMethod())) {
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(apiToken.getBytes());
                }
            } else {
                System.out.println("/register ждёт GET-запрос, а получил " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка на сервере при регистрации клиента");
        } finally {
            httpExchange.close();
        }
    }

    private void save(HttpExchange httpExchange) {
    }

    private void load(HttpExchange httpExchange) {
    }

    private void tasks(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("Принят запрос от клиента на пути /tasks");
            boolean isValidToken = isValidToken(httpExchange);
            if (!isValidToken) {
                System.out.println("Запрос клиента не прошел по API_TOKEN");
                httpExchange.sendResponseHeaders(405, 0);
                return;
            }

            TypeEndpoint endpoint = TypeEndpoint.getEndpoint(httpExchange);
            switch (endpoint) {
                case POST_ADD_TASK:
//                    addTasks(httpExchange);
                    System.out.println(endpoint);
                    break;
                case POST_ADD_EPIC:
                    System.out.println(endpoint);
                    break;
                case POST_ADD_SUBTASK:
                    System.out.println(endpoint);
                    break;
                case GET_ALL_TASKS:
                    System.out.println(endpoint);
                    break;
                case GET_ALL_EPICS:
                    System.out.println(endpoint);
                    break;
                case GET_ALL_SUBTASKS:
                    System.out.println(endpoint);
                    break;
                case GET_TASK_BY_ID:
                    System.out.println(endpoint);
                    break;
                case GET_EPIC_BY_ID:
                    System.out.println(endpoint);
                    break;
                case GET_SUBTASK_BY_ID:
                    System.out.println(endpoint);
                    break;
                case DELETE_ALL_TASKS:
                    System.out.println(endpoint);
                    break;
                case DELETE_ALL_EPICS:
                    System.out.println(endpoint);
                    break;
                case DELETE_ALL_SUBTASKS:
                    System.out.println(endpoint);
                    break;
                case DELETE_TASK_BY_ID:
                    System.out.println(endpoint);
                    break;
                case DELETE_EPIC_BY_ID:
                    System.out.println(endpoint);
                    break;
                case DELETE_SUBTASK_BY_ID:
                    System.out.println(endpoint);
                    break;
                case GET_EPIC_SUBTASKS:
                    System.out.println(endpoint);
                    break;
                case GET_HISTORY:
                    System.out.println(endpoint);
                    break;
                case GET_Prioritized_TASKS:
                    System.out.println(endpoint);
                    break;
                case UNKNOWN:
                    System.out.println(endpoint);
                    break;
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка на сервере, путь: /tasks");
            httpExchange.sendResponseHeaders(500, 0);
        } finally {
            httpExchange.close();
        }
    }

    private void addTasks(HttpExchange httpExchange) throws IOException {
        try (InputStream inputStream = httpExchange.getRequestBody()) {
            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(requestBody);
            if (!jsonElement.isJsonObject()) {
                System.out.println("Переданная задача не соответствует формату JSON");
                httpExchange.sendResponseHeaders(405, 0);
                return;
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Task task = gson.fromJson(jsonObject, Task.class);


            boolean taskExists = manager.addTask(task);// С помощью добавления проверяется, была ли задача ранее добавлена уже
            boolean taskUpdated;
            if (taskExists) {
                System.out.println("Задача успешно создана и добавлена в приложение");
                httpExchange.sendResponseHeaders(200, 0);
                return;
            } else {
                taskUpdated = manager.updateTask(task);
            }

            if (taskUpdated) {
                System.out.println("Задача успешно обновлена");
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                System.out.println("Задачу не получилось добавить или обновить. Проверьте задачу");
                httpExchange.sendResponseHeaders(405, 0);
            }
        }
    }

    private boolean isValidToken(HttpExchange httpExchange) {
        try {
            URI requestURI = httpExchange.getRequestURI();
            String[] query = requestURI.getQuery().split("&");
            if (query.length < 1) {
                System.out.println("В запросе клиента строка запроса пустая");
                return false;
            }
            String token = query[0];
            String[] key_values = token.split("=");
            String key = key_values[0];
            String value = key_values[1];
            if (key.equals("API_TOKEN") && value.equals(apiToken)) {
                return true;
            } else {
                System.out.println("В запросе клиента указан не верный формат API_TOKEN или неверное его значение");
                return false;
            }
        } catch (Exception e) {
            System.out.println("В запросе клиента указан не верный формат URI");
            return false;
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT);
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.print("Остановили сервер на порту: " + PORT);
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
//        Gson gson1 = CreateGson.createGson();
//        Task task = new Task("task", "task1task1", 22, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 50);
//        Task task1 = new Task("tas2", "task1task2", 2, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 50);
//        Task task2 = new Task("tas3", "task1task3", 12, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 50);
//        Epic epic = new Epic("epic1", "epic1epic1", 18, StatusTask.NEW, TypeTask.EPIC);
//        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 119, StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
//        epic.addSubtask(subtask1);
//        String s = gson1.toJson(task);
//        String s2 = gson1.toJson(epic);
//        String s3 = gson1.toJson(subtask1);
//
//        System.out.println(s);
//        System.out.println(s2);
//        System.out.println(s3);
//
//        List<Task> list = new ArrayList<>();
//        list.add(task);
//        list.add(task1);
//        list.add(task2);
//        list.add(epic);
//        list.add(subtask1);
//
//        String string = gson1.toJson(list);
//        System.out.println(string);
//
//        List<Task> tasks = gson1.fromJson(string, new TypeToken<List<Task>>() {}.getType());
//        System.out.println(tasks);
//        Epic epic1 = (Epic) tasks.get(4);
//        Task taskss= tasks.get(4);

    }
}

