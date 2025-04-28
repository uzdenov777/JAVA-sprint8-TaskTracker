package managerTest;

import api.KVServer;
import manager.HttpTaskManager;
import manager.Managers;
import manager.enums.StatusTask;
import manager.enums.TypeTask;
import manager.interfaces.TaskManager;
import managerTest.abstractManagersTest.TaskManagerTest;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    KVServer kvServer = new KVServer();
    String kvServerUrl = "http://localhost:8078";

    @Override
    public HttpTaskManager createTaskManager() {
        kvServer.start();
        return Managers.getDefault(kvServerUrl);
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    @Test
    public void shouldRestoreEmptyManagerIfNoTasksAdded() {
        List<Task> tasks = manager.getAllTasksEpicSubtask();
        Assertions.assertEquals(0, tasks.size());

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        Assertions.assertEquals(0, prioritizedTasks.size());

        List<Task> history = manager.getHistory();
        Assertions.assertEquals(0, history.size());
    }

    @Test
    public void shouldRestoreEmptyManagerAfterDeletionOfAllTasks() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        List<Task> allTasksEpicsSubtasks1 = manager.getAllTasksEpicSubtask();
        Assertions.assertEquals(3, allTasksEpicsSubtasks1.size());

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        Assertions.assertEquals(2, prioritizedTasks.size());//будет 2 потому что эпики у которых есть подзадачи в список приорит не попадает

        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask1.getId());
        List<Task> history = manager.getHistory();
        Assertions.assertEquals(3, history.size());

        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        List<Task> allTasksEpicsSubtasks2 = manager.getAllTasksEpicSubtask();
        assertTrue(allTasksEpicsSubtasks2.isEmpty());

        TaskManager manager2 = Managers.getDefault(kvServerUrl);

        List<Task> allTasksEpicsSubtasks3 = manager2.getAllTasksEpicSubtask();
        assertTrue(allTasksEpicsSubtasks3.isEmpty());

        List<Task> prioritizedTasks3 = manager2.getPrioritizedTasks();
        Assertions.assertEquals(0, prioritizedTasks3.size());

        List<Task> history3 = manager2.getHistory();
        Assertions.assertEquals(0, history3.size());
    }

    @Test
    public void shouldRestoreAllPreviouslyAddedTasks() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        List<Task> allTasksEpicsSubtasks1 = manager.getAllTasksEpicSubtask();
        Assertions.assertEquals(3, allTasksEpicsSubtasks1.size());

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        Assertions.assertEquals(2, prioritizedTasks.size());//будет 2 потому что эпики у которых есть подзадачи в список приорит не попадает

        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask1.getId());
        List<Task> history = manager.getHistory();
        Assertions.assertEquals(3, history.size());

        TaskManager manager2 = Managers.getDefault(kvServerUrl);
        List<Task> allTasksEpicsSubtasks3 = manager2.getAllTasksEpicSubtask();
        assertFalse(allTasksEpicsSubtasks3.isEmpty());

        List<Task> prioritizedTasks3 = manager2.getPrioritizedTasks();
        Assertions.assertEquals(2, prioritizedTasks3.size());

        List<Task> history3 = manager2.getHistory();
        Assertions.assertEquals(3, history3.size());
    }
}
