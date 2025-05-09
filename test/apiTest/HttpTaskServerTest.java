package apiTest;

import api.HttpTaskServer;
import api.KVServer;
import api.gson.CreateGson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.HttpTaskManager;
import manager.enums.StatusTask;
import manager.enums.TypeTask;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private final KVServer kvServer = new KVServer();
    private HttpTaskServer httpTaskServer;
    private final String taskServerUrl = "http://localhost:8080";
    private String API_TOKEN;
    private HttpTaskManager taskManager;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = CreateGson.createGson();

    @BeforeEach
    public void startServers() {
        kvServer.start();
        String kvServerUrl = "http://localhost:8078";
        httpTaskServer = new HttpTaskServer(kvServerUrl);
        httpTaskServer.start();
        taskManager = httpTaskServer.getManager();
        register();
    }

    @AfterEach
    public void stopServers() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    private void register() {
        try {
            URI uri = URI.create(taskServerUrl + "/register");
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            API_TOKEN = response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void fillTaskManager() {
        Task task = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для регистрацию GET эндпоинт http://localhost:8080/register")
    public void getStatusCode200_ValidPathRegister() {
        try {
            URI uri = URI.create(taskServerUrl + "/register");
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для получения всех задач, энд.-GET /tasks/task?API_TOKEN=***")
    public void returnListAllTasks_StatusCode200() {
        try {
            URI uri = URI.create(taskServerUrl + "/tasks/task" + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для получения всех Эпиков, энд.-GET /tasks/epic?API_TOKEN=***")
    public void returnListAllEpics_StatusCode200() {
        try {
            URI uri = URI.create(taskServerUrl + "/tasks/epic" + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для получения всех подзадач, энд.-GET /tasks/subtask?API_TOKEN=***")
    public void returnListAllSubtask_StatusCode200() {
        try {
            URI uri = URI.create(taskServerUrl + "/tasks/subtask" + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для получения несущ. задачи по ID, энд.-GET /tasks/task?API_TOKEN=***&id=idTask")
    public void returnTaskById_StatusCode404_NotAddingTask() {
        try {
            int idTask = 1;
            URI uri = URI.create(taskServerUrl + "/tasks/task" + "?API_TOKEN=" + API_TOKEN + "&id=" + idTask);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            int statusCode = response.statusCode();
            assertEquals(404, statusCode);
            assertEquals("Задачи с таким ID:" + idTask + " не существует.", body);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для получения несущ. Эпик по ID, энд.-GET /tasks/epic?API_TOKEN=***&id=epicId")
    public void returnEpicById_StatusCode404_NotAddingEpic() {
        try {
            int epicId = 1;
            URI uri = URI.create(taskServerUrl + "/tasks/epic" + "?API_TOKEN=" + API_TOKEN + "&id=" + epicId);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            int statusCode = response.statusCode();
            assertEquals(404, statusCode);
            assertEquals("Epic с таким ID:" + epicId + " не существует.", body);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для получения несущ. подзадачи по ID, энд.-GET /tasks/subtask?API_TOKEN=***&id=subtaskId")
    public void returnSubtaskById_StatusCode404_NotAddingSubtask() {
        try {
            int subtaskId = 1;
            URI uri = URI.create(taskServerUrl + "/tasks/subtask" + "?API_TOKEN=" + API_TOKEN + "&id=" + subtaskId);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            int statusCode = response.statusCode();
            assertEquals(404, statusCode);
            assertEquals("Подзадачи с таким ID:" + subtaskId + " не существует.", body);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для получения задачи по ID, энд.-GET /tasks/task?API_TOKEN=***&id=taskId")
    public void returnTaskById_StatusCode200_AddingTask() {
        fillTaskManager();
        try {
            int taskId = 1;
            URI uri = URI.create(taskServerUrl + "/tasks/task" + "?API_TOKEN=" + API_TOKEN + "&id=" + taskId);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            Task taskResponse = gson.fromJson(body, Task.class);
            Task taskGetManager = taskManager.getTask(taskId).get();
            int statusCode = response.statusCode();

            assertEquals(200, statusCode);
            assertEquals(taskResponse, taskGetManager);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для получения Эпика по ID, энд.-GET /tasks/epic?API_TOKEN=***&id=epicId")
    public void returnEpicById_StatusCode200_AddingEpic() {
        fillTaskManager();
 try {
            int epicId = 2;
            URI uri = URI.create(taskServerUrl + "/tasks/epic" + "?API_TOKEN=" + API_TOKEN + "&id=" + epicId);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            Epic taskResponse = gson.fromJson(body, Epic.class);
            Epic taskGetManager = taskManager.getEpic(epicId).get();
            int statusCode = response.statusCode();

            assertEquals(200, statusCode);
            assertEquals(taskResponse, taskGetManager);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для получения подзадачи по ID, энд.-GET /tasks/subtask?API_TOKEN=***&id=subtaskId")
    public void returnSubtaskById_StatusCode200_AddingSubtask() {
        fillTaskManager();
        try {
            int subtaskId = 3;
            URI uri = URI.create(taskServerUrl + "/tasks/subtask" + "?API_TOKEN=" + API_TOKEN + "&id=" + subtaskId);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            Subtask taskResponse = gson.fromJson(body, Subtask.class);
            Subtask taskGetManager = taskManager.getSubtask(subtaskId).get();
            int statusCode = response.statusCode();

            assertEquals(200, statusCode);
            assertEquals(taskResponse, taskGetManager);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для добавления или обновления задачи, эндп.-POST+taskToJson /tasks/task?API_TOKEN=***")
    public void returnStatusCode200_POST_AddingTaskEpicSubtask() {
        Task task = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        String taskJson = gson.toJson(task);

        try {
            URI uri = URI.create(taskServerUrl + "/tasks/task" + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для добавления или обновления Эпика, эндп.-POST+epicToJson /tasks/epic?API_TOKEN=***")
    public void returnStatusCode200_POST_AddingEpic() {
        Epic epic = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        String epicJson = gson.toJson(epic);

        try {
            URI uri = URI.create(taskServerUrl + "/tasks/epic" + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    @DisplayName("Отправляем запрос на сервер для добавления или обновления подзадачи, эндп.-POST+subtaskToJson /tasks/subtask?API_TOKEN=***")
    public void returnStatusCode200_POST_AddingSubtask() {
        Epic epic = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        taskManager.addEpic(epic);
        String subtaskJson = gson.toJson(subtask);
        try {
            URI uri = URI.create(taskServerUrl + "/tasks/subtask" + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для удаления всех задач, эндп.-DELETE /tasks/task?API_TOKEN=***")
    public void returnStatusCode200_DELETE_ClearAllTasks() {
        fillTaskManager();
        try {
            URI uri = URI.create(taskServerUrl + "/tasks/task" + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для удаления все Эпики, эндп.-DELETE /tasks/epic?API_TOKEN=***")
    public void returnStatusCode200_DELETE_ClearAllEpics() {
        fillTaskManager();
        try {
            URI uri = URI.create(taskServerUrl + "/tasks/epic" + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для удаления всех подзадач, эндп.-DELETE /tasks/subtask?API_TOKEN=***")
    public void returnStatusCode200_DELETE_ClearAllSubtasks() {
        fillTaskManager();
        try {
            URI uri = URI.create(taskServerUrl + "/tasks/subtask" + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для удаления не сущ. задачи по ID, эндп.-DELETE /tasks/task?API_TOKEN=***&id=idTask")
    public void returnStatusCode404_DELETE_removeByIDTask_NotExistID() {
        int notExistID = 777;
        try {
            URI uri = URI.create(taskServerUrl + "/tasks/task" + "?API_TOKEN=" + API_TOKEN + "&id=" + notExistID);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            int statusCode = response.statusCode();
            assertEquals(404, statusCode);
            assertEquals("Задачи по ID:" + notExistID + " не найдена для удаления.", body);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для удаления не сущ. Эпика по ID, эндп.-DELETE /tasks/epic?API_TOKEN=***&id=notExistID")
    public void returnStatusCode404_DELETE_removeByIDEpic_NotExistID() {
        int notExistID = 777;
        try {
            URI uri = URI.create(taskServerUrl + "/tasks/epic" + "?API_TOKEN=" + API_TOKEN + "&id=" + notExistID);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            int statusCode = response.statusCode();
            assertEquals(404, statusCode);
            assertEquals("Epic по ID:" + notExistID + " не найден для удаления.", body);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для удаления не сущ. подзадачи по ID, эндп.-DELETE /tasks/subtask?API_TOKEN=***&id=notExistID")
    public void returnStatusCode404_DELETE_removeByIDSubtask_NotExistID() {
        int notExistID = 777;
        try {
            URI uri = URI.create(taskServerUrl + "/tasks/subtask" + "?API_TOKEN=" + API_TOKEN + "&id=" + notExistID);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            int statusCode = response.statusCode();
            assertEquals(404, statusCode);
            assertEquals("Подзадачи по ID:" + notExistID + " не найдена для удаления.", body);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для удаления задачи по ID, эндп.-DELETE /tasks/task?API_TOKEN=***&id=idTask")
    public void returnStatusCode200_DELETE_removeByIDTask_ExistID() {
        fillTaskManager();
        try {
            int idTask = 1;
            URI uri = URI.create(taskServerUrl + "/tasks/task" + "?API_TOKEN=" + API_TOKEN + "&id=" + idTask);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
            assertEquals("Задача по ID:" + idTask + " удалена.", body);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для удаления подзадачи по ID, эндп.-DELETE /tasks/subtask?API_TOKEN=***&id=idSubtask")
    public void returnStatusCode200_DELETE_removeByIDSubtask_ExistID() {
        fillTaskManager();
        try {
            int idSubtask = 3;
            URI uri = URI.create(taskServerUrl + "/tasks/subtask" + "?API_TOKEN=" + API_TOKEN + "&id=" + idSubtask);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
            assertEquals("Подзадача по ID:" + idSubtask + " удалена.", body);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для удаления Эпика по ID, эндп.-DELETE /tasks/epic?API_TOKEN=***&id=idEpic")
    public void returnStatusCode200_DELETE_removeByIDEpic_ExistID() {
        fillTaskManager();
        try {
            int idEpic = 2;
            URI uri = URI.create(taskServerUrl + "/tasks/epic" + "?API_TOKEN=" + API_TOKEN + "&id=" + idEpic);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
            assertEquals("Epic по ID:" + idEpic + " удален.", body);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для для получения листа с подзадачами определенного Эпика по пути GET /tasks/epic/subtask?API_TOKEN=***&id=idEpic")
    public void returnEpicSubtaskListByID_StatusCode200() {
        fillTaskManager();
        try {
            int idEpic = 2;
            URI uri = URI.create(taskServerUrl + "/tasks/epic/subtask" + "?API_TOKEN=" + API_TOKEN + "&id=" + idEpic);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            List<Subtask> subtaskListGetResponse = gson.fromJson(body, new TypeToken<ArrayList<Subtask>>() {
            }.getType());
            List<Subtask> subtasksListGetManager = taskManager.getListSubtasksByEpicId(idEpic);
            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
            assertEquals(subtasksListGetManager.size(), subtaskListGetResponse.size());
            assertEquals(subtasksListGetManager, subtaskListGetResponse);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для получения листа с историей по пути GET /tasks/history")
    public void returnHistoryList_StatusCode200() {
        fillTaskManager();

        try {
            URI uri = URI.create(taskServerUrl + "/tasks/history" + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            List<Task> historyGetResponse = gson.fromJson(body, new TypeToken<ArrayList<Task>>() {
            }.getType());
            List<Task> historyGetManager = taskManager.getHistory();
            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
            assertEquals(historyGetManager.size(), historyGetResponse.size());
            assertEquals(historyGetManager, historyGetResponse);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер для получения листа с приоритетами по пути GET /tasks")
    public void returnPrioritizedTaskList_StatusCode200() {
        fillTaskManager();

        try {
            URI uri = URI.create(taskServerUrl + "/tasks" + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            List<Task> prioritizedTasksGetResponse = gson.fromJson(body, new TypeToken<ArrayList<Task>>() {
            }.getType());
            List<Task> prioritizedTasksGetManager = taskManager.getPrioritizedTasks();
            int statusCode = response.statusCode();
            assertEquals(200, statusCode);
            assertEquals(prioritizedTasksGetManager.size(), prioritizedTasksGetResponse.size());
            assertEquals(prioritizedTasksGetManager, prioritizedTasksGetResponse);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Отправляем запрос на сервер не сущ. путь tasks/main, получаем 503")
    public void returnStatusCode503() {
        try {
            URI uri = URI.create(taskServerUrl + "/tasks/main" + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            assertEquals(503, statusCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
