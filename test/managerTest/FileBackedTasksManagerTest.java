package managerTest;

import manager.FileBackedTasksManager;
import manager.enums.StatusTask;
import manager.enums.TypeTask;
import managerTest.abstractManagersTest.TaskManagerTest;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    File readerFile = new File("saveFile1.txt");

    @Override
    public FileBackedTasksManager createTaskManager() {
        return new FileBackedTasksManager(readerFile);
    }

    @Test
    public void shouldSaveAndRestoreWhenTaskListIsEmpty() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.clearTasks();
        manager.clearEpics();
        manager.clearSubtasks();
        List<Task> allTasksEpicsSubtasks = manager.getAllTasksEpicSubtask();
        assertTrue(allTasksEpicsSubtasks.isEmpty());

        File writerFile = new File("writerFile.txt");
        FileBackedTasksManager taskManager = FileBackedTasksManager.loadFromFile(readerFile, writerFile);
        List<Task> allTasksEpicsSubtasks2 = taskManager.getAllTasksEpicSubtask();
        assertTrue(allTasksEpicsSubtasks2.isEmpty());
    }

    @Test
    public void shouldSaveAndRestoreWhenEpicHasNoSubtasks() {
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
        assertFalse(getSubtasksEpic1.isEmpty());
        assertTrue(getSubtasksEpic2.isEmpty());

        manager.getTask(task1.getId());
        manager.getSubtask(subtask1.getId());
        manager.getSubtask(subtask1.getId());
        List<Task> getHistory1 = manager.getHistory();
        assertFalse(getHistory1.isEmpty());

        File writerFile = new File("writerFile.txt");
        FileBackedTasksManager taskManager = FileBackedTasksManager.loadFromFile(readerFile, writerFile);
        List<Task> allTasksEpicsSubtasks2 = taskManager.getAllTasksEpicSubtask();
        assertFalse(allTasksEpicsSubtasks2.isEmpty());

        List<Task> getHistory2 = taskManager.getHistory();
        assertFalse(getHistory2.isEmpty());
        assertEquals(getHistory1, getHistory2);

        Optional<Epic> epicNotEmptyOptional = taskManager.getEpic(idEpicNotEmpty);
        assertTrue(epicNotEmptyOptional.isPresent());
        Epic epicNotEmpty = epicNotEmptyOptional.get();
        HashMap<Integer, Subtask> getSubtasksNotEmptyEpic = epicNotEmpty.getSubtasksMap();
        assertFalse(getSubtasksNotEmptyEpic.isEmpty());

        Optional<Epic> epicEmptyOptional = taskManager.getEpic(idEpicEmpty);
        assertTrue(epicEmptyOptional.isPresent());
        Epic epicEmpty = epicEmptyOptional.get();
        HashMap<Integer, Subtask> getSubtasksEmptyEpic = epicEmpty.getSubtasksMap();
        assertTrue(getSubtasksEmptyEpic.isEmpty());
    }

    @Test
    public void shouldSaveAndRestoreWhenHistoryIsEmpty() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Epic epic2 = new Epic("epic2", "epic2", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);

        List<Task> allTasksEpicsSubtasks1 = manager.getAllTasksEpicSubtask();
        assertFalse(allTasksEpicsSubtasks1.isEmpty());

        List<Task> getHistory1 = manager.getHistory();
        assertTrue(getHistory1.isEmpty());

        File writerFile = new File("writerFile.txt");
        FileBackedTasksManager taskManager = FileBackedTasksManager.loadFromFile(readerFile, writerFile);
        List<Task> allTasksEpicsSubtasks2 = taskManager.getAllTasksEpicSubtask();
        assertFalse(allTasksEpicsSubtasks2.isEmpty());

        List<Task> getHistory2 = taskManager.getHistory();
        assertTrue(getHistory2.isEmpty());
    }
}