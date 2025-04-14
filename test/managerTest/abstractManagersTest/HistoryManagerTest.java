package managerTest.abstractManagersTest;

import manager.enums.StatusTask;
import manager.enums.TypeTask;
import manager.interfaces.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class HistoryManagerTest<T extends HistoryManager> {

    HistoryManager historyManager = createHistoryManager();

    public abstract T createHistoryManager();

    @BeforeEach
    public void setupManager() {
        historyManager = createHistoryManager();
    }

    @Test
    public void returnEmptyHistory_WhenNoTasksViewed() {
        List<Task> getHistoryList = historyManager.getListHistory();
        assertTrue(getHistoryList.isEmpty());
    }

    @Test
    public void returnNotEmptyHistory_WhenTasksViewed() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);

        List<Task> getHistoryList = historyManager.getListHistory();
        assertFalse(getHistoryList.isEmpty());

        assertTrue(getHistoryList.contains(task1));
        assertTrue(getHistoryList.contains(epic1));
        assertTrue(getHistoryList.contains(subtask1));
    }

    @Test
    public void notDuplicateTasksInHistory_whenCallTaskSeveralTimes() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);

        historyManager.add(epic1);
        historyManager.add(subtask1);
        historyManager.add(task1);
        historyManager.add(task1);


        List<Task> getHistoryList = historyManager.getListHistory();
        assertFalse(getHistoryList.isEmpty());
        assertEquals(3, getHistoryList.size());

        assertTrue(getHistoryList.contains(task1));
        assertTrue(getHistoryList.contains(epic1));
        assertTrue(getHistoryList.contains(subtask1));
    }

    @Test
    public void removeByIdFromHistory_WhenTaskAddedToHistory() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);

        List<Task> getHistory = historyManager.getListHistory();
        assertFalse(getHistory.isEmpty());
        assertTrue(getHistory.contains(task1));
        assertTrue(getHistory.contains(epic1));
        assertTrue(getHistory.contains(subtask1));

        int idRemoveTask = epic1.getId();
        historyManager.removeById(idRemoveTask);

        List<Task> getHistory2 = historyManager.getListHistory();
        assertFalse(getHistory2.isEmpty());
        assertFalse(getHistory2.contains(epic1));

        assertTrue(getHistory2.contains(task1));
        assertTrue(getHistory2.contains(subtask1));
        assertEquals(2, getHistory2.size());
    }

    @Test
    public void notRemoveByIdFromHistory_WhenTaskNotAddedToHistory() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);

        List<Task> getHistory = historyManager.getListHistory();
        assertFalse(getHistory.isEmpty());
        assertTrue(getHistory.contains(task1));
        assertTrue(getHistory.contains(epic1));
        assertTrue(getHistory.contains(subtask1));

        int idRemoveTask = 888;
        historyManager.removeById(idRemoveTask);

        List<Task> getHistory2 = historyManager.getListHistory();
        assertFalse(getHistory2.isEmpty());

        assertEquals(getHistory, getHistory2);
        assertEquals(3, getHistory2.size());


        assertTrue(getHistory2.contains(task1));
        assertTrue(getHistory2.contains(epic1));
        assertTrue(getHistory2.contains(subtask1));
    }

    @Test
    public void shouldRemoveAllTask_WhenTasksAddedToHistory() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Task task2 = new Task("task", "task1task1", 2, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Task task3 = new Task("task", "task1task1", 3, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", 4, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 5, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(epic1);
        historyManager.add(subtask1);

        List<Task> getHistory = historyManager.getListHistory();
        assertFalse(getHistory.isEmpty());

        assertTrue(getHistory.contains(task1));
        assertTrue(getHistory.contains(task2));
        assertTrue(getHistory.contains(task3));
        assertTrue(getHistory.contains(epic1));
        assertTrue(getHistory.contains(subtask1));

        HashMap<Integer, Task> tasks = new HashMap<>();
        tasks.put(task1.getId(), task1);
        tasks.put(task2.getId(), task2);
        tasks.put(task3.getId(), task3);

        historyManager.removeTaskAll(tasks);
        List<Task> getHistory2 = historyManager.getListHistory();
        assertFalse(getHistory2.isEmpty());

        assertFalse(getHistory2.contains(task1));
        assertFalse(getHistory2.contains(task2));
        assertFalse(getHistory2.contains(task3));

        assertTrue(getHistory2.contains(epic1));
        assertTrue(getHistory2.contains(subtask1));
    }

    @Test
    public void shouldRemoveAllEpic_WhenEpicsAddedToHistory() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", 8, StatusTask.NEW, TypeTask.EPIC);
        Epic epic2 = new Epic("epic1", "epic1epic1", 6, StatusTask.NEW, TypeTask.EPIC);
        Epic epic3 = new Epic("epic1", "epic1epic1", 4, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 5, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(epic2);
        historyManager.add(epic3);
        historyManager.add(subtask1);

        List<Task> getHistory = historyManager.getListHistory();
        assertFalse(getHistory.isEmpty());

        assertTrue(getHistory.contains(task1));
        assertTrue(getHistory.contains(epic1));
        assertTrue(getHistory.contains(epic2));
        assertTrue(getHistory.contains(epic3));
        assertTrue(getHistory.contains(subtask1));

        HashMap<Integer, Epic> epics = new HashMap<>();
        epics.put(epic1.getId(), epic1);
        epics.put(epic2.getId(), epic2);
        epics.put(epic3.getId(), epic3);

        historyManager.removeEpicAll(epics);
        List<Task> getHistory2 = historyManager.getListHistory();
        assertFalse(getHistory2.isEmpty());

        assertFalse(getHistory2.contains(epic1));
        assertFalse(getHistory2.contains(epic2));
        assertFalse(getHistory2.contains(epic3));

        assertTrue(getHistory2.contains(task1));
        assertTrue(getHistory2.contains(subtask1));
    }

    @Test
    public void shouldRemoveAllSubtask_WhenSubtasksAddedToHistory() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", 8, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 5, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask1", "subtask1subtask1", 7, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask3 = new Subtask("subtask1", "subtask1subtask1", 4, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        historyManager.add(subtask3);

        List<Task> getHistory = historyManager.getListHistory();
        assertFalse(getHistory.isEmpty());

        assertTrue(getHistory.contains(task1));
        assertTrue(getHistory.contains(epic1));
        assertTrue(getHistory.contains(subtask1));
        assertTrue(getHistory.contains(subtask2));
        assertTrue(getHistory.contains(subtask3));

        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        subtasks.put(subtask1.getId(), subtask1);
        subtasks.put(subtask2.getId(), subtask2);
        subtasks.put(subtask3.getId(), subtask3);

        historyManager.removeSubtaskAll(subtasks);
        List<Task> getHistory2 = historyManager.getListHistory();
        assertFalse(getHistory2.isEmpty());

        assertFalse(getHistory2.contains(subtask1));
        assertFalse(getHistory2.contains(subtask2));
        assertFalse(getHistory2.contains(subtask3));

        assertTrue(getHistory2.contains(task1));
        assertTrue(getHistory2.contains(epic1));
    }

    @Test
    public void removeTaskFromHistory_WhenTaskIsAtTheBeginning(){
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", 8, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 5, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);

        Task getFirstTask = historyManager.getFirst();
        assertEquals(task1, getFirstTask);

        List<Task> getHistory = historyManager.getListHistory();
        assertFalse(getHistory.isEmpty());

        assertTrue(getHistory.contains(task1));
        assertTrue(getHistory.contains(epic1));
        assertTrue(getHistory.contains(subtask1));

        historyManager.removeFirst();

        List<Task> getHistory2 = historyManager.getListHistory();
        assertFalse(getHistory2.isEmpty());
        assertFalse(getHistory2.contains(task1));

        assertTrue(getHistory2.size() == 2);
        assertTrue(getHistory2.contains(epic1));
        assertTrue(getHistory2.contains(subtask1));

        Task getFirstTask2 = historyManager.getFirst();
        assertEquals(epic1, getFirstTask2);
    }

    @Test
    public void removeTaskFromHistory_WhenTaskIsAtTheEnd(){
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", 8, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 5, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);

        Task getLastTask = historyManager.getLast();
        assertEquals(subtask1, getLastTask);

        List<Task> getHistory = historyManager.getListHistory();
        assertFalse(getHistory.isEmpty());

        assertTrue(getHistory.contains(task1));
        assertTrue(getHistory.contains(epic1));
        assertTrue(getHistory.contains(subtask1));

        historyManager.removeLast();

        List<Task> getHistory2 = historyManager.getListHistory();
        assertFalse(getHistory2.isEmpty());
        assertFalse(getHistory2.contains(subtask1));

        assertTrue(getHistory2.size() == 2);
        assertTrue(getHistory2.contains(task1));
        assertTrue(getHistory2.contains(epic1));

        Task getLastTask2 = historyManager.getLast();
        assertEquals(epic1, getLastTask2);
    }
}
