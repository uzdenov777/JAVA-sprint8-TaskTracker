import manager.FileBackedTasksManager;
import manager.Managers;
import manager.enums.StatusTask;
import manager.enums.TypeTask;
import manager.interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        File file1 = new File("saveFile1.txt");
        TaskManager manager = Managers.getFileBackedTasksManager(file1);
        int idEpicNotEmpty = 777;
        int idEpicEmpty = 888;

        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", idEpicNotEmpty, StatusTask.NEW, TypeTask.EPIC);
        Epic epic2 = new Epic("epic2", "epic2", idEpicEmpty, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);

        HashMap<Integer, Subtask> getSubtasksEpic1 = epic1.getSubtasksMap();
        HashMap<Integer, Subtask> getSubtasksEpic2 = epic2.getSubtasksMap();
//        assertFalse(getSubtasksEpic1.isEmpty());
//        assertTrue(getSubtasksEpic2.isEmpty());

        File writerFile = new File("writerFile.txt");
        FileBackedTasksManager taskManager = FileBackedTasksManager.loadFromFile(file1, writerFile);
        List<Task> allTasksEpicsSubtasks2 = taskManager.getAllTasksEpicSubtask();
//        assertFalse(allTasksEpicsSubtasks2.isEmpty());
    }
}