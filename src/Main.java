import api.HttpTaskServer;
import api.KVServer;
import manager.HttpTaskManager;
import manager.enums.StatusTask;
import manager.enums.TypeTask;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer server = new HttpTaskServer("http://localhost:8078");
        server.start();

        //        HttpTaskManager taskManager = new HttpTaskManager("http://localhost:8078");
//        Task task = new Task("task", "task1task1", taskManager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
//        Task task2 = new Task("task2", "task2", taskManager.getNewId(), StatusTask.NEW, TypeTask.TASK, "22.03.2025 12:00", 1);
//        Task task3 = new Task("task3", "task3", taskManager.getNewId(), StatusTask.NEW, TypeTask.TASK, "23.03.2025 12:00", 1);
//        Epic epic = new Epic("epic1", "epic1epic1", taskManager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
//        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", taskManager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
//        taskManager.addTask(task);
//        taskManager.addTask(task2);
//        taskManager.addTask(task3);
//        taskManager.addEpic(epic);
//        taskManager.addSubtask(subtask1);
//
//        List<Task> ss = taskManager.getPrioritizedTasks();
//        for (Task task1 : ss) {
//            System.out.println(task1);
//        }
    }
}