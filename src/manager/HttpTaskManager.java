package manager;

import api.KVTaskClient;
import api.gson.CreateGson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.interfaces.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HttpTaskManager extends FileBackedTasksManager {
    KVTaskClient client;
    Gson gson;

    public HttpTaskManager(String kvServerUrl) {
        super();
        this.client = new KVTaskClient(kvServerUrl);
        this.gson = CreateGson.createGson();
        load();
    }

    @Override
    protected void save() {
        try {
            List<Task> allTasksList = getListTasks();
            List<Epic> allEpicsList = getListEpics();
            List<Subtask> allSubtasksList = getListSubtasks();
            List<Task> history = getHistory();

            String allTasksJson = gson.toJson(allTasksList);
            String allEpicsJson = gson.toJson(allEpicsList);
            String allSubtasksJson = gson.toJson(allSubtasksList);
            String historyJson = gson.toJson(history);

            client.put("Tasks", allTasksJson);
            client.put("Epics", allEpicsJson);
            client.put("Subtasks", allSubtasksJson);
            client.put("History", historyJson);
        } catch (Exception e) {
            System.out.println("TasksManager не получилось сохранить на сервере-хранилище.");
        }
    }

    private void load() {
        try {
            String tasksToJson = client.load("Tasks");
            String epicsToJson = client.load("Epics");
            String subtasksToJson = client.load("Subtasks");
            String historyToJson = client.load("History");

            if (!tasksToJson.isBlank()) {
                List<Task> tasksList = gson.fromJson(tasksToJson, new TypeToken<ArrayList<Task>>() {
                }.getType());
                tasksList.stream().filter(Objects::nonNull).forEach(this::addTask);
            }

            if (!epicsToJson.isBlank()) {
                List<Epic> epicsList = gson.fromJson(epicsToJson, new TypeToken<ArrayList<Epic>>() {
                }.getType());
                epicsList.stream().filter(Objects::nonNull).forEach(this::addEpic);
            }

            if (!subtasksToJson.isBlank()) {
                List<Subtask> subtaskList = gson.fromJson(subtasksToJson, new TypeToken<ArrayList<Subtask>>() {
                }.getType());
                subtaskList.stream().filter(Objects::nonNull).forEach(this::addSubtask);
            }

            if (!historyToJson.isBlank()) {
                List<Task> historyTaskList = gson.fromJson(historyToJson, new TypeToken<ArrayList<Task>>() {
                }.getType());
                HistoryManager historyManager = getHistoryManager();
                historyTaskList.stream().filter(Objects::nonNull).forEach(historyManager::add);
            }
            System.out.println("TasksManager прошел процесс восстановления.");
        } catch (IOException | InterruptedException e) {
            System.out.println("TasksManager не получилось восстановить.");
            throw new RuntimeException(e);
        }
    }
}
