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
import manager.enums.StatusTask;
import manager.enums.TypeEndpoint;
import manager.enums.TypeTask;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;


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
                    addTask(httpExchange);
                    System.out.println(endpoint);
                    break;
                case POST_ADD_EPIC:
                    addEpic(httpExchange);
                    System.out.println(endpoint);
                    break;
                case POST_ADD_SUBTASK:
                    addSubtask(httpExchange);
                    System.out.println(endpoint);
                    break;
                case GET_ALL_TASKS:
                    getAllTasks(httpExchange);
                    System.out.println(endpoint);
                    break;
                case GET_ALL_EPICS:
                    getAllEpics(httpExchange);
                    System.out.println(endpoint);
                    break;
                case GET_ALL_SUBTASKS:
                    getAllSubtasks(httpExchange);
                    System.out.println(endpoint);
                    break;
                case GET_TASK_BY_ID:
                    getTask(httpExchange);
                    System.out.println(endpoint);
                    break;
                case GET_EPIC_BY_ID:
                    getEpic(httpExchange);
                    System.out.println(endpoint);
                    break;
                case GET_SUBTASK_BY_ID:
                    getSubtask(httpExchange);
                    System.out.println(endpoint);
                    break;
                case DELETE_ALL_TASKS:
                    clearTasks(httpExchange);
                    System.out.println(endpoint);
                    break;
                case DELETE_ALL_EPICS:
                    clearEpics(httpExchange);
                    System.out.println(endpoint);
                    break;
                case DELETE_ALL_SUBTASKS:
                    clearSubtasks(httpExchange);
                    System.out.println(endpoint);
                    break;
                case DELETE_TASK_BY_ID:
                    removeTask(httpExchange);
                    System.out.println(endpoint);
                    break;
                case DELETE_EPIC_BY_ID:
                    removeEpic(httpExchange);
                    System.out.println(endpoint);
                    break;
                case DELETE_SUBTASK_BY_ID:
                    removeSubtask(httpExchange);
                    System.out.println(endpoint);
                    break;
                case GET_EPIC_SUBTASKS:
                    getEpicSubtasks(httpExchange);
                    System.out.println(endpoint);
                    break;
                case GET_HISTORY:
                    getHistory(httpExchange);
                    System.out.println(endpoint);
                    break;
                case GET_Prioritized_TASKS:
                    getPrioritizedTasks(httpExchange);
                    System.out.println(endpoint);
                    break;
                case UNKNOWN:
                    System.out.println(endpoint);
                    httpExchange.sendResponseHeaders(503, 0);
                    break;
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка на сервере, путь: /tasks");
            httpExchange.sendResponseHeaders(500, 0);
        } finally {
            httpExchange.close();
        }
    }


    private void addTask(HttpExchange httpExchange) throws IOException {
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

    private void addEpic(HttpExchange httpExchange) throws IOException {
        try (InputStream inputStream = httpExchange.getRequestBody()) {
            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(requestBody);
            if (!jsonElement.isJsonObject()) {
                System.out.println("Переданный Epic не соответствует формату JSON");
                httpExchange.sendResponseHeaders(405, 0);
                return;
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Epic epic = gson.fromJson(jsonObject, Epic.class);

            boolean epicExists = manager.addEpic(epic);// С помощью добавления проверяется, была ли Epic ранее добавлена уже
            boolean epicUpdated;
            if (epicExists) {
                System.out.println("Epic успешно создан и добавлен в приложение");
                httpExchange.sendResponseHeaders(200, 0);
                return;
            } else {
                epicUpdated = manager.updateEpic(epic);
            }

            if (epicUpdated) {
                System.out.println("Epic успешно обновлен");
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                System.out.println("Epic не получилось добавить или обновить. Проверьте Epic");
                httpExchange.sendResponseHeaders(405, 0);
            }
        }
    }

    private void addSubtask(HttpExchange httpExchange) throws IOException {
        try (InputStream inputStream = httpExchange.getRequestBody()) {
            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(requestBody);
            if (!jsonElement.isJsonObject()) {
                System.out.println("Переданная подзадача не соответствует формату JSON");
                httpExchange.sendResponseHeaders(405, 0);
                return;
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Subtask subtask = gson.fromJson(jsonObject, Subtask.class);

            boolean subtaskExists = manager.addSubtask(subtask);// С помощью добавления проверяется, была ли подзадача ранее добавлена уже
            boolean subtaskUpdated;
            if (subtaskExists) {
                System.out.println("Подзадача успешно создана и добавлена в приложение");
                httpExchange.sendResponseHeaders(200, 0);
                return;
            } else {
                subtaskUpdated = manager.updateSubtask(subtask);
            }

            if (subtaskUpdated) {
                System.out.println("Подзадача успешно обновлена");
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                System.out.println("Подзадачу не получилось добавить или обновить. Проверьте подзадачу");
                httpExchange.sendResponseHeaders(405, 0);
            }
        }
    }

    private void getAllTasks(HttpExchange httpExchange) throws IOException {
        try {
            List<Task> tasks = manager.getListTasks();
            String tasksToJson = gson.toJson(tasks);
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream outputStream = httpExchange.getResponseBody()) {
                outputStream.write(tasksToJson.getBytes());
            }
        } catch (IOException e) {
            httpExchange.sendResponseHeaders(500, 0);
        }
    }

    private void getAllEpics(HttpExchange httpExchange) throws IOException {
        try {
            List<Epic> epics = manager.getListEpics();
            String epicsToJson = gson.toJson(epics);
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream outputStream = httpExchange.getResponseBody()) {
                outputStream.write(epicsToJson.getBytes());
            }
        } catch (IOException e) {
            httpExchange.sendResponseHeaders(500, 0);
        }
    }

    private void getAllSubtasks(HttpExchange httpExchange) throws IOException {
        try {
            List<Subtask> subtasks = manager.getListSubtasks();
            String subtasksToJson = gson.toJson(subtasks);
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream outputStream = httpExchange.getResponseBody()) {
                outputStream.write(subtasksToJson.getBytes());
            }
        } catch (IOException e) {
            httpExchange.sendResponseHeaders(500, 0);
        }
    }

    private void getTask(HttpExchange httpExchange) throws IOException {
        try {
            URI uri = httpExchange.getRequestURI();
            String query = uri.getQuery();
            String[] parsedQuery = query.split("&");
            String taskIdToString = parsedQuery[1].split("=")[1];
            int taskId = Integer.parseInt(taskIdToString);
            Optional<Task> optionalTask = manager.getTask(taskId);
            if (optionalTask.isPresent()) {
                httpExchange.sendResponseHeaders(200, 0);
                Task task = optionalTask.get();
                String taskToJson = gson.toJson(task);
                try (OutputStream outputStream = httpExchange.getResponseBody()) {
                    outputStream.write(taskToJson.getBytes());
                }
            } else {
                String notExistTask = "Задачи с таким ID:" + taskId + " не существует.";
                httpExchange.sendResponseHeaders(404, 0);
                try (OutputStream outputStream = httpExchange.getResponseBody()) {
                    outputStream.write(notExistTask.getBytes());
                }
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка на сервере при возвращении задачи");
            httpExchange.sendResponseHeaders(500, 0);
        }
    }

    private void getEpic(HttpExchange httpExchange) throws IOException {
        try {
            URI uri = httpExchange.getRequestURI();
            String query = uri.getQuery();
            String[] parsedQuery = query.split("&");
            String epicIdToString = parsedQuery[1].split("=")[1];
            int epicId = Integer.parseInt(epicIdToString);
            Optional<Epic> optionalEpic = manager.getEpic(epicId);
            if (optionalEpic.isPresent()) {
                httpExchange.sendResponseHeaders(200, 0);
                Epic epic = optionalEpic.get();
                String epicToJson = gson.toJson(epic);
                try (OutputStream outputStream = httpExchange.getResponseBody()) {
                    outputStream.write(epicToJson.getBytes());
                }
            } else {
                String notExistTask = "Epic с таким ID:" + epicId + " не существует.";
                httpExchange.sendResponseHeaders(404, 0);
                try (OutputStream outputStream = httpExchange.getResponseBody()) {
                    outputStream.write(notExistTask.getBytes());
                }
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка на сервере при возвращении Epic");
            httpExchange.sendResponseHeaders(500, 0);
        }
    }

    private void getSubtask(HttpExchange httpExchange) throws IOException {
        try {
            URI uri = httpExchange.getRequestURI();
            String query = uri.getQuery();
            String[] parsedQuery = query.split("&");
            String subtaskIdToString = parsedQuery[1].split("=")[1];
            int subtaskId = Integer.parseInt(subtaskIdToString);
            Optional<Subtask> optionalSubtask = manager.getSubtask(subtaskId);
            if (optionalSubtask.isPresent()) {
                httpExchange.sendResponseHeaders(200, 0);
                Subtask subtask = optionalSubtask.get();
                String subtaskToJson = gson.toJson(subtask);
                try (OutputStream outputStream = httpExchange.getResponseBody()) {
                    outputStream.write(subtaskToJson.getBytes());
                }
            } else {
                String notExistTask = "Подзадачи с таким ID:" + subtaskId + " не существует.";
                httpExchange.sendResponseHeaders(404, 0);
                try (OutputStream outputStream = httpExchange.getResponseBody()) {
                    outputStream.write(notExistTask.getBytes());
                }
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка на сервере при возвращении подзадачи");
            httpExchange.sendResponseHeaders(500, 0);
        }
    }

    private void clearTasks(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        manager.clearTasks();
    }

    private void clearEpics(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        manager.clearEpics();
    }

    private void clearSubtasks(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        manager.clearSubtasks();
    }

    private void removeTask(HttpExchange httpExchange) {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            String[] parsedQuery = query.split("&");
            String taskIdToString = parsedQuery[1].split("=")[1];
            int taskId = Integer.parseInt(taskIdToString);

            boolean isRemoveTask = manager.removeTaskById(taskId);
            if (isRemoveTask) {
                String result = "Задача по ID:" + taskId + " удалена.";
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream outputStream = httpExchange.getResponseBody()) {
                    outputStream.write(result.getBytes());
                }
            } else {
                String result = "Задачи по ID:" + taskId + " не найдена для удаления.";
                httpExchange.sendResponseHeaders(404, 0);
                try (OutputStream outputStream = httpExchange.getResponseBody()) {
                    outputStream.write(result.getBytes());
                }
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка на сервере при удалении задачи по ID");
        }
    }

    private void removeEpic(HttpExchange httpExchange) {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            String[] parsedQuery = query.split("&");
            String epicIdToString = parsedQuery[1].split("=")[1];
            int epicId = Integer.parseInt(epicIdToString);

            boolean isRemoveEpic = manager.removeEpicById(epicId);
            if (isRemoveEpic) {
                String result = "Epic по ID:" + epicId + " удален.";
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream outputStream = httpExchange.getResponseBody()) {
                    outputStream.write(result.getBytes());
                }
            } else {
                String result = "Epic по ID:" + epicId + " не найден для удаления.";
                httpExchange.sendResponseHeaders(404, 0);
                try (OutputStream outputStream = httpExchange.getResponseBody()) {
                    outputStream.write(result.getBytes());
                }
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка на сервере при удалении Epic по ID");
        }
    }

    private void removeSubtask(HttpExchange httpExchange) {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            String[] parsedQuery = query.split("&");
            String subtaskIdToString = parsedQuery[1].split("=")[1];
            int subtaskId = Integer.parseInt(subtaskIdToString);

            boolean isRemoveSubtask = manager.removeSubtaskById(subtaskId);
            if (isRemoveSubtask) {
                String result = "Подзадача по ID:" + subtaskId + " удалена.";
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream outputStream = httpExchange.getResponseBody()) {
                    outputStream.write(result.getBytes());
                }
            } else {
                String result = "Подзадачи по ID:" + subtaskId + " не найдена для удаления.";
                httpExchange.sendResponseHeaders(404, 0);
                try (OutputStream outputStream = httpExchange.getResponseBody()) {
                    outputStream.write(result.getBytes());
                }
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка на сервере при удалении подзадачи по ID");
        }
    }

    private void getEpicSubtasks(HttpExchange httpExchange) {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            String[] parsedQuery = query.split("&");
            String epicIdToString = parsedQuery[1].split("=")[1];
            int epicId = Integer.parseInt(epicIdToString);
            List<Subtask> epicSubtasks = manager.getListSubtasksByEpicId(epicId);
            httpExchange.sendResponseHeaders(200, 0);
            String epicSubtasksToJson = gson.toJson(epicSubtasks);
            try (OutputStream outputStream = httpExchange.getResponseBody()) {
                outputStream.write(epicSubtasksToJson.getBytes());
            }
        } catch (Exception e) {
            System.out.println("Возникла проблема на сервере при возвращении подзадач определённого Epic-а.");
        }
    }

    private void getHistory(HttpExchange httpExchange) {
        try {
            List<Task> history = manager.getHistory();
            String historyToJson = gson.toJson(history);
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream outputStream = httpExchange.getResponseBody()) {
                outputStream.write(historyToJson.getBytes());
            }
        } catch (Exception e) {
            System.out.println("Возникала проблема на сервере при запросе истории задач.");
        }
    }

    private void getPrioritizedTasks(HttpExchange httpExchange) {
        try {
            List<Task> prioritizedTasks = manager.getPrioritizedTasks();
            String prioritizedTasksToJson = gson.toJson(prioritizedTasks);
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream outputStream = httpExchange.getResponseBody()) {
                outputStream.write(prioritizedTasksToJson.getBytes());
            }
        } catch (Exception e) {
            System.out.println("Возникла проблема на сервере при возвращении задач по приоритету.");
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
}