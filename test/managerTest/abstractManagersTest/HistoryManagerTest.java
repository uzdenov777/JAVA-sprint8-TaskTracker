package managerTest.abstractManagersTest;

import manager.enums.StatusTask;
import manager.enums.TypeTask;
import manager.interfaces.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class HistoryManagerTest<T extends HistoryManager> {

    HistoryManager historyManager = createHistoryManager();
    Task taskNew;
    Task taskDone;
    Task taskProgress;
    Epic epicNotEmpty;
    Epic epicEmpty;
    Subtask subtaskFirst;
    Subtask subtaskTwo;

    public abstract T createHistoryManager();

    @BeforeEach
    public void setupManager() {
        historyManager = createHistoryManager();
        taskNew = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        taskDone = new Task("task", "task1task1", 2, StatusTask.DONE, TypeTask.TASK, "21.03.2025 12:00", 1);
        taskProgress = new Task("task", "task1task1", 3, StatusTask.IN_PROGRESS, TypeTask.TASK, "21.03.2025 12:00", 1);
        epicNotEmpty = new Epic("epic1", "epic1epic1", 4, StatusTask.NEW, TypeTask.EPIC);
        epicEmpty = new Epic("epic2", "epic2epic2", 5, StatusTask.NEW, TypeTask.EPIC);
        subtaskFirst = new Subtask("subtask1", "subtask1subtask1", 7, StatusTask.NEW, epicNotEmpty.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        subtaskTwo = new Subtask("subtask1", "subtask1subtask1", 8, StatusTask.NEW, epicNotEmpty.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
    }

    @Test
    @DisplayName("Вернет историю запросов задач, когда задачи добавлены в историю")
    public void returnNotEmptyHistory_WhenTasksViewed() {
        historyManager.add(taskNew);
        historyManager.add(epicNotEmpty);
        historyManager.add(subtaskFirst);

        List<Task> getHistoryList = historyManager.getListHistory();

        assertFalse(getHistoryList.isEmpty());
        assertTrue(getHistoryList.contains(taskNew));
        assertTrue(getHistoryList.contains(epicNotEmpty));
        assertTrue(getHistoryList.contains(subtaskFirst));
    }

    @Test
    @DisplayName("Несколько раз добавляются одни и те же задачи, но в истории не должно быть дубликатов")
    public void notDuplicateTasksInHistory_whenCallTaskSeveralTimes() {
        historyManager.add(taskNew);
        historyManager.add(epicNotEmpty);
        historyManager.add(subtaskFirst);
        historyManager.add(epicNotEmpty);
        historyManager.add(subtaskFirst);
        historyManager.add(taskNew);
        historyManager.add(taskNew);

        List<Task> getHistoryList = historyManager.getListHistory();

        assertFalse(getHistoryList.isEmpty());
        assertEquals(3, getHistoryList.size());
        assertTrue(getHistoryList.contains(taskNew));
        assertTrue(getHistoryList.contains(epicNotEmpty));
        assertTrue(getHistoryList.contains(subtaskFirst));
    }

    @Test
    @DisplayName("Удалить задачу по ID из истории")
    public void removeByIdFromHistory_WhenTaskAddedToHistory() {
        historyManager.add(taskNew);
        historyManager.add(epicNotEmpty);
        historyManager.add(subtaskFirst);

        int idRemoveTask = epicNotEmpty.getId();
        historyManager.removeById(idRemoveTask);
        List<Task> getHistory2 = historyManager.getListHistory();

        assertFalse(getHistory2.isEmpty());
        assertFalse(getHistory2.contains(epicNotEmpty));
        assertTrue(getHistory2.contains(taskNew));
        assertTrue(getHistory2.contains(subtaskFirst));
        assertEquals(2, getHistory2.size());
    }

    @Test
    @DisplayName("Должен удалить все задачи из истории в переданном листе с задачами ,которые хотим удалить")
    public void removeTaskAll_shouldRemoveAllTask_WhenTasksAddedToHistory() {
        historyManager.add(taskNew);
        historyManager.add(taskDone);
        historyManager.add(taskProgress);

        HashMap<Integer, Task> tasks = new HashMap<>();
        tasks.put(taskNew.getId(), taskNew);
        tasks.put(taskDone.getId(), taskDone);
        tasks.put(taskProgress.getId(), taskProgress);
        historyManager.removeTaskAll(tasks);
        List<Task> getHistory2 = historyManager.getListHistory();

        assertTrue(getHistory2.isEmpty());
    }

    @Test
    @DisplayName("Должен удалить все Эпики из истории в переданном листе с Эпиками ,которые хотим удалить")
    public void shouldRemoveAllEpic_WhenEpicsAddedToHistory() {
        historyManager.add(epicNotEmpty);
        historyManager.add(epicEmpty);


        HashMap<Integer, Epic> epics = new HashMap<>();
        epics.put(epicNotEmpty.getId(), epicNotEmpty);
        epics.put(epicEmpty.getId(), epicEmpty);
        historyManager.removeEpicAll(epics);
        List<Task> getHistory2 = historyManager.getListHistory();

        assertTrue(getHistory2.isEmpty());
    }

    @Test
    @DisplayName("Должен удалить все подзадачи из истории в переданном листе с подзадачи ,которые хотим удалить")
    public void shouldRemoveAllSubtask_WhenSubtasksAddedToHistory() {
        historyManager.add(subtaskFirst);
        historyManager.add(subtaskTwo);

        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        subtasks.put(subtaskFirst.getId(), subtaskFirst);
        subtasks.put(subtaskTwo.getId(), subtaskTwo);
        historyManager.removeSubtaskAll(subtasks);
        List<Task> getHistory2 = historyManager.getListHistory();

        assertTrue(getHistory2.isEmpty());
    }

    @Test
    @DisplayName("Удаляет первый элемент из истории")
    public void removeTaskFromHistory_WhenTaskIsAtTheBeginning() {
        historyManager.add(taskNew);
        historyManager.add(epicNotEmpty);
        historyManager.add(subtaskFirst);

        Task getFirstTask = historyManager.getFirst();
        assertEquals(taskNew, getFirstTask);
        historyManager.removeFirst();
        List<Task> getHistory2 = historyManager.getListHistory();
        Task getFirstTask2 = historyManager.getFirst();

        assertFalse(getHistory2.isEmpty());
        assertFalse(getHistory2.contains(taskNew));
        assertTrue(getHistory2.contains(epicNotEmpty));
        assertTrue(getHistory2.contains(subtaskFirst));
        assertEquals(taskNew, getFirstTask);
        assertEquals(epicNotEmpty, getFirstTask2);
    }

    @Test
    @DisplayName("Удаляет последний элемент из истории")
    public void removeTaskFromHistory_WhenTaskIsAtTheEnd() {
        historyManager.add(taskNew);
        historyManager.add(epicNotEmpty);
        historyManager.add(subtaskFirst);

        Task getLastTask = historyManager.getLast();
        historyManager.removeLast();
        List<Task> getHistory2 = historyManager.getListHistory();
        Task getLastTask2 = historyManager.getLast();

        assertEquals(subtaskFirst, getLastTask);
        assertFalse(getHistory2.isEmpty());
        assertFalse(getHistory2.contains(subtaskFirst));
        assertTrue(getHistory2.contains(taskNew));
        assertTrue(getHistory2.contains(epicNotEmpty));
        assertEquals(epicNotEmpty, getLastTask2);
    }
}
