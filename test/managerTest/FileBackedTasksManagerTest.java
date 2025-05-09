package managerTest;

import manager.FileBackedTasksManager;
import manager.enums.StatusTask;
import manager.enums.TypeTask;
import managerTest.abstractManagersTest.TaskManagerTest;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    String nameReaderFile = "saveFile1.txt";

    @Override
    public FileBackedTasksManager createTaskManager() {

        return new FileBackedTasksManager(nameReaderFile);
    }

    @Test
    @DisplayName("Успешно восстанавливает пустой манагер с пустого файла")
    public void shouldSaveAndRestoreWhenTaskListIsEmpty() {
        Task taskNew = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epicFirst = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskDone = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.DONE, epicFirst.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        manager.addTask(taskNew);
        manager.addEpic(epicFirst);
        manager.addSubtask(subtaskDone);
        manager.clearTasks();
        manager.clearEpics();
        manager.clearSubtasks();
        List<Task> allTasksEpicsSubtasksOld = manager.getAllTasksEpicSubtask();
        String writerFile = "writerFile.txt";
        FileBackedTasksManager taskManager = FileBackedTasksManager.loadFromFile(nameReaderFile, writerFile);
        List<Task> allTasksEpicsSubtasksNew = taskManager.getAllTasksEpicSubtask();

        assertTrue(allTasksEpicsSubtasksOld.isEmpty());
        assertTrue(allTasksEpicsSubtasksNew.isEmpty());
    }

    @Test
    @DisplayName("Проверяем как восстанавливается состояние пустого эпика и еще эпика с подзадачами")
    public void shouldSaveAndRestoreWhenEpicHasNoSubtasks() {
        int idEpicNotEmpty = 777;
        int idEpicEmpty = 888;
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epicNotEmptyOld = new Epic("epic1", "epic1epic1", idEpicNotEmpty, StatusTask.NEW, TypeTask.EPIC);
        Epic epicEmptyOld = new Epic("epic2", "epic2", idEpicEmpty, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskDone = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.DONE, epicNotEmptyOld.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        manager.addTask(task1);
        manager.addEpic(epicNotEmptyOld);
        manager.addEpic(epicEmptyOld);
        manager.addSubtask(subtaskDone);
        manager.getTask(task1.getId());
        manager.getSubtask(subtaskDone.getId());

        String writerFile = "writerFile.txt";
        FileBackedTasksManager taskManager = FileBackedTasksManager.loadFromFile(nameReaderFile, writerFile);
        List<Task> allTasksEpicsSubtasks2 = taskManager.getAllTasksEpicSubtask();
        List<Task> getHistory2 = taskManager.getHistory();
        Epic epicNotEmptyNew = taskManager.getEpic(idEpicNotEmpty).get();
        HashMap<Integer, Subtask> getSubtasksNotEmptyEpic = epicNotEmptyNew.getSubtasksMap();
        Epic epicEmptyNew = taskManager.getEpic(idEpicEmpty).get();
        HashMap<Integer, Subtask> getSubtasksEmptyEpic = epicEmptyNew.getSubtasksMap();

        assertFalse(allTasksEpicsSubtasks2.isEmpty());
        assertFalse(getHistory2.isEmpty());
        assertFalse(getSubtasksNotEmptyEpic.isEmpty());
        assertTrue(getSubtasksEmptyEpic.isEmpty());
    }

    @Test
    @DisplayName("Успешное восстановление пустой истории, когда задачи добавлены ,но не запрошены")
    public void shouldSaveAndRestoreWhenHistoryIsEmpty() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        manager.addTask(task1);

        String writerFile = "writerFile.txt";
        FileBackedTasksManager taskManager = FileBackedTasksManager.loadFromFile(nameReaderFile, writerFile);
        List<Task> allTasksEpicsSubtasks = taskManager.getAllTasksEpicSubtask();
        List<Task> getHistory = taskManager.getHistory();

        assertFalse(allTasksEpicsSubtasks.isEmpty());
        assertTrue(getHistory.isEmpty());
    }
}