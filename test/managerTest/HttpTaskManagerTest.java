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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


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
    @DisplayName("Проверили что после запуска сервера манагера восстановилось все пустым ,потому что на сервер-хранилище не были еще доб задачи")
    public void shouldRestoreEmptyManagerIfNoTasksAdded() {
        List<Task> tasks = manager.getAllTasksEpicSubtask();
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        List<Task> history = manager.getHistory();

        assertEquals(0, tasks.size());
        assertEquals(0, prioritizedTasks.size());
        assertEquals(0, history.size());
    }

    @Test
    @DisplayName("Проверяет что после удаления всех задач ,манагер восстанавливается пустым")
    public void shouldRestoreEmptyManagerAfterDeletionOfAllTasks() {
        Task taskFirst = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epicNotEmpty = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskDone = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.DONE, epicNotEmpty.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        manager.addTask(taskFirst);
        manager.addEpic(epicNotEmpty);
        manager.addSubtask(subtaskDone);
        manager.getTask(taskFirst.getId());
        manager.getEpic(epicNotEmpty.getId());
        manager.getSubtask(subtaskDone.getId());
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        List<Task> allTasksEpicsSubtasksOld = manager.getAllTasksEpicSubtask();

        TaskManager managerNew = Managers.getDefault(kvServerUrl);
        List<Task> allTasksEpicsSubtasksNew = managerNew.getAllTasksEpicSubtask();
        List<Task> prioritizedTasksNew = managerNew.getPrioritizedTasks();
        List<Task> historyNEw = managerNew.getHistory();

        assertTrue(allTasksEpicsSubtasksOld.isEmpty());
        assertTrue(allTasksEpicsSubtasksNew.isEmpty());
        assertEquals(0, prioritizedTasksNew.size());
        assertEquals(0, historyNEw.size());
    }

    @Test
    @DisplayName("Успешно восстанавливает ранее добавленные задачи")
    public void shouldRestoreAllPreviouslyAddedTasks() {
        Task taskFirst = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epicNotEmpty = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskDone = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.DONE, epicNotEmpty.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        manager.addTask(taskFirst);
        manager.addEpic(epicNotEmpty);
        manager.addSubtask(subtaskDone);
        manager.getTask(taskFirst.getId());
        manager.getEpic(epicNotEmpty.getId());
        manager.getSubtask(subtaskDone.getId());
        List<Task> allTasksEpicsSubtasksOld = manager.getAllTasksEpicSubtask();
        List<Task> prioritizedTasksOld = manager.getPrioritizedTasks();
        List<Task> historyOld = manager.getHistory();

        TaskManager managerNew = Managers.getDefault(kvServerUrl);
        List<Task> allTasksEpicsSubtasksNew = managerNew.getAllTasksEpicSubtask();
        List<Task> prioritizedTasksNew = managerNew.getPrioritizedTasks();
        List<Task> historyNew = managerNew.getHistory();

        assertEquals(allTasksEpicsSubtasksOld, allTasksEpicsSubtasksNew);
        assertEquals(prioritizedTasksOld, prioritizedTasksNew);
        assertEquals(historyNew, historyOld);
    }
}
