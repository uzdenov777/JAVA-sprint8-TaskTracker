package managerTest.abstractManagersTest;

import manager.enums.StatusTask;
import manager.enums.TypeTask;
import manager.interfaces.PrioritizedManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public abstract class PrioritizedManagerTest<T extends PrioritizedManager> {

    PrioritizedManager manager;

    public abstract T createPrioritizedManager();

    @BeforeEach
    public void setupManager() {
        manager = createPrioritizedManager();
    }

    @Test
    @DisplayName("Успешно добавит первую задачу в список приоритетов")
    public void addTaskWithoutIntersection_returnTrueAddTaskWithoutIntersection_WhenTasksByPriorityEmpty() {
        Task taskNew = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);

        boolean isAdd = manager.addTaskWithoutIntersection(taskNew);
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(isAdd);
        assertTrue(getPrioritizedTasks.contains(taskNew));
    }

    @Test
    @DisplayName("добавил успешно вторую задачу ,которая началась после первой")
    public void addTaskWithoutIntersection_returnTrueAddWithoutIntersection_WhenNotIntersection() {
        Task taskNew = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Subtask subtaskDone = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.DONE, 22, TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        boolean isAdd = manager.addTaskWithoutIntersection(taskNew);
        boolean isSubtaskAdded = manager.addTaskWithoutIntersection(subtaskDone);
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(isAdd);
        assertTrue(isSubtaskAdded);
        assertTrue(getPrioritizedTasks.contains(taskNew));
        assertTrue(getPrioritizedTasks.contains(subtaskDone));
    }

    @Test
    @DisplayName("Добавить Успешно задачу между двумя другими")
    public void addTaskWithoutIntersection_returnTrueAddWithoutIntersection_WhenPossibleBetweenTwoTasksWithoutIntersection() {
        Task taskNew = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Subtask subtaskDone = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.DONE, 22, TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtaskNew = new Subtask("subtask2", "subtask2", 3, StatusTask.NEW, 23, TypeTask.SUBTASK, "22.03.2025 12:00", 1);

        boolean isAdd = manager.addTaskWithoutIntersection(taskNew);
        boolean isSubtaskAdded = manager.addTaskWithoutIntersection(subtaskDone);
        boolean isSubtaskAdded2 = manager.addTaskWithoutIntersection(subtaskNew);
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(isAdd);
        assertTrue(isSubtaskAdded);
        assertTrue(isSubtaskAdded2);
        assertTrue(getPrioritizedTasks.contains(taskNew));
        assertTrue(getPrioritizedTasks.contains(subtaskDone));
        assertTrue(getPrioritizedTasks.contains(subtaskNew));
    }

    @Test
    @DisplayName("Добавляем все успешно ,но в списке приоритетов не будет epicFirst ,потому что у нее есть подзадача")
    public void addTaskWithoutIntersection_returnTrueAddWithoutIntersection_WhenAddNotEmptyEpicButWillNotAddEpicToThePriorityList() {//Удалит Эпик из списков приоритетов когда в него добавлена подзадача
        Task taskNew = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epicFirst = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskDone = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.DONE, epicFirst.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        boolean isAdd = manager.addTaskWithoutIntersection(taskNew);
        boolean isAddEpic = manager.addTaskWithoutIntersection(epicFirst);
        epicFirst.addSubtask(subtaskDone);//это методы вызываются в другом классе
        manager.removeTaskFromPrioritizedAndNullLists(epicFirst);// симуляция
        boolean isSubtaskAdded = manager.addTaskWithoutIntersection(subtaskDone);
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(isAdd);
        assertTrue(isAddEpic);
        assertTrue(isSubtaskAdded);
        assertTrue(getPrioritizedTasks.contains(taskNew));
        assertTrue(getPrioritizedTasks.contains(subtaskDone));
        assertFalse(getPrioritizedTasks.contains(epicFirst));
    }

    @Test
    @DisplayName("Добавит успешно все задачи в список приоритетов в том числе Пустой эпик")
    public void addTaskWithoutIntersection_returnTrueAddWithoutIntersection_WhenAddEmptyEpic() {
        Task taskNew = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epicEmpty = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);

        boolean isAdd = manager.addTaskWithoutIntersection(taskNew);
        boolean isAddEpic = manager.addTaskWithoutIntersection(epicEmpty);
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(isAdd);
        assertTrue(isAddEpic);
        assertTrue(getPrioritizedTasks.contains(taskNew));
        assertTrue(getPrioritizedTasks.contains(epicEmpty));
    }

    @Test
    @DisplayName("Не добавит subtaskNew в список приоритетов ,новая задача началась раньше, чем окончание предыдущей задачи")
    public void addTaskWithoutIntersection_returnFalseAddWithoutIntersection_NewTaskStartedEarlierThanTheEndOfThePreviousTask() {
        Task taskFirst = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 30);
        Subtask subtaskNew = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:15", 1);

        boolean isAdd = manager.addTaskWithoutIntersection(taskFirst);
        boolean isSubtaskAdded = manager.addTaskWithoutIntersection(subtaskNew);
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(isAdd);
        assertFalse(isSubtaskAdded);
        assertTrue(getPrioritizedTasks.contains(taskFirst));
        assertFalse(getPrioritizedTasks.contains(subtaskNew));
    }

    @Test
    @DisplayName("Не добавлена подзадача subtaskNew, новая задача заканчивается позже, чем начинается следующая задача")
    public void addTaskWithoutIntersection_returnFalseAddWithoutIntersection_NewTaskEndsLaterThanNextTaskStart() {
        Task taskFirst = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 30);
        Subtask subtaskNew = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 11:55", 10);

        boolean isAdd = manager.addTaskWithoutIntersection(taskFirst);
        boolean isSubtaskAdded = manager.addTaskWithoutIntersection(subtaskNew);
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(isAdd);
        assertFalse(isSubtaskAdded);
        assertTrue(getPrioritizedTasks.contains(taskFirst));
        assertFalse(getPrioritizedTasks.contains(subtaskNew));
    }

    @Test
    @DisplayName("Не получиться добавить subtaskDone между двумя другими,новая задача заканчивается позже, чем начало следующей задачи")
    public void returnFalseAddWithoutIntersection_WhenPossibleBetweenTwoTasksIntersection_NewTaskEndsLaterThanNextTaskStart() {
        Task taskFirst = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 15);
        Subtask subtaskNew = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        Subtask subtaskDone = new Subtask("subtask2", "subtask2", 3, StatusTask.DONE, 23, TypeTask.SUBTASK, "21.03.2025 12:20", 15);

        boolean isAdd = manager.addTaskWithoutIntersection(taskFirst);
        boolean isSubtaskAdded = manager.addTaskWithoutIntersection(subtaskNew);
        boolean isSubtaskAdded2 = manager.addTaskWithoutIntersection(subtaskDone);
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(isAdd);
        assertTrue(isSubtaskAdded);
        assertFalse(isSubtaskAdded2);
        assertTrue(getPrioritizedTasks.contains(taskFirst));
        assertTrue(getPrioritizedTasks.contains(subtaskNew));
        assertFalse(getPrioritizedTasks.contains(subtaskDone));
    }

    @Test
    @DisplayName("Не добавиться subtaskDone между двумя другими, потому что новая задача начинается раньше ,чем заканчивается предыдущая задача")
    public void addTaskWithoutIntersection_returnFalseAddWithoutIntersection_TheNewTaskStartedEarlierThanTheEndOfThePreviousTask() {
        Task taskFirst = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 15);
        Subtask subtaskDone = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.DONE, 22, TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        Subtask subtaskNew = new Subtask("subtask2", "subtask2", 3, StatusTask.NEW, 23, TypeTask.SUBTASK, "21.03.2025 12:10", 15);

        boolean isAdd = manager.addTaskWithoutIntersection(taskFirst);
        boolean isSubtaskAdded = manager.addTaskWithoutIntersection(subtaskDone);
        boolean isSubtaskAdded2 = manager.addTaskWithoutIntersection(subtaskNew);
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(isAdd);
        assertTrue(isSubtaskAdded);
        assertFalse(isSubtaskAdded2);
        assertTrue(getPrioritizedTasks.contains(taskFirst));
        assertTrue(getPrioritizedTasks.contains(subtaskDone));
        assertFalse(getPrioritizedTasks.contains(subtaskNew));
    }

    @Test
    @DisplayName("Должен успешно обновить единственную задачу")
    public void updateTaskWithoutIntersection_returnTrueUpdateTaskWithoutIntersection_WhenOneTaskOnTheList() {
        Task taskOld = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 15);
        Task taskNew = new Task("task2", "task2", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 15);

        manager.addTaskWithoutIntersection(taskOld);
        boolean isUpdate = manager.updateTaskWithoutIntersection(taskNew, taskOld);
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(isUpdate);
        assertTrue(getPrioritizedTasks.contains(taskNew));
        assertFalse(getPrioritizedTasks.contains(taskOld));
    }

    @Test
    @DisplayName("Обновит подзадачу subtaskFirst на newSubtask, когда нет пересечения между задачами")
    public void updateSubtaskWithoutIntersection_returnTrueUpdateTaskWithoutIntersection_WhenNotIntersectionOccurs() {
        Task taskNew = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Subtask subtaskOld = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtaskTwo = new Subtask("subtask2", "subtask2", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "25.03.2025 12:00", 1);
        Subtask newSubtask = new Subtask("newUpdate", "newUpdate", subtaskOld.getId(), StatusTask.NEW, 22, TypeTask.SUBTASK, "22.03.2025 12:05", 30);

        manager.addTaskWithoutIntersection(taskNew);
        manager.addTaskWithoutIntersection(subtaskOld);
        manager.addTaskWithoutIntersection(subtaskTwo);
        boolean isUpdate = manager.updateSubtaskWithoutIntersection(newSubtask, subtaskOld);
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(isUpdate);
        assertTrue(getPrioritizedTasks.contains(taskNew));
        assertTrue(getPrioritizedTasks.contains(newSubtask));
        assertTrue(getPrioritizedTasks.contains(subtaskTwo));
        assertFalse(getPrioritizedTasks.contains(subtaskOld));
    }


    @Test
    @DisplayName("Не обновит subtaskOld на newSubtask, новая задача началась раньше, чем окончание предыдущей задачи")
    public void updateSubtaskWithoutIntersection_returnFalseUpdateTaskWithoutIntersection_TheNewTaskStartedEarlierThanTheEndOfThePreviousTask() {
        Task taskFirst = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 10);
        Subtask subtaskOld = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        Subtask subtaskTwo = new Subtask("subtask2", "subtask2", 4, StatusTask.NEW, 22, TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask newSubtask = new Subtask("newUpdate", "newUpdate", subtaskOld.getId(), StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:05", 1);

        manager.addTaskWithoutIntersection(taskFirst);
        manager.addTaskWithoutIntersection(subtaskOld);
        manager.addTaskWithoutIntersection(subtaskTwo);
        boolean isUpdate = manager.updateSubtaskWithoutIntersection(newSubtask, subtaskOld);
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();

        assertFalse(isUpdate);
        assertTrue(getPrioritizedTasks.contains(taskFirst));
        assertTrue(getPrioritizedTasks.contains(subtaskOld));
        assertTrue(getPrioritizedTasks.contains(subtaskTwo));
        assertFalse(getPrioritizedTasks.contains(newSubtask));
    }

    @Test
    @DisplayName("Не обновит subtaskOld на newSubtask, новая задача заканчивается позже, чем начало следующей задачи")
    public void updateSubtaskWithoutIntersection_returnFalseUpdateTaskWithoutIntersection_NewTaskEndsLaterThanNextTaskStart() {
        Task taskFirst = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 10);
        Subtask subtaskOld = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        Subtask subtaskTwo = new Subtask("subtask2", "subtask2", 4, StatusTask.NEW, 22, TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask newSubtask = new Subtask("newUpdate", "newUpdate", subtaskOld.getId(), StatusTask.NEW, 22, TypeTask.SUBTASK, "24.03.2025 11:55", 10);

        manager.addTaskWithoutIntersection(taskFirst);
        manager.addTaskWithoutIntersection(subtaskOld);
        manager.addTaskWithoutIntersection(subtaskTwo);
        boolean isUpdate = manager.updateSubtaskWithoutIntersection(newSubtask, subtaskOld);
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();

        assertFalse(isUpdate);
        assertTrue(getPrioritizedTasks.contains(taskFirst));
        assertTrue(getPrioritizedTasks.contains(subtaskOld));
        assertTrue(getPrioritizedTasks.contains(subtaskTwo));
        assertFalse(getPrioritizedTasks.contains(newSubtask));
    }

    @Test
    @DisplayName("Успешно обновляет пустой Эпик на новый пустой Эпик")
    public void updateEpicWithoutIntersection_shouldUpdateOldEmptyEpic_WhenNewEpicEmpty() {
        Epic epicOld = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        Epic newEpic = new Epic("newEpic", "newEpic", epicOld.getId(), StatusTask.NEW, TypeTask.EPIC);

        boolean isAdd = manager.addTaskWithoutIntersection(epicOld);
        List<Task> getPrioritizedTasksOld = manager.getPrioritizedTasks();
        manager.updateEpicWithoutIntersection(newEpic, epicOld);
        List<Task> getPrioritizedTasksNew = manager.getPrioritizedTasks();

        assertTrue(isAdd);
        assertTrue(getPrioritizedTasksOld.contains(epicOld));
        assertTrue(getPrioritizedTasksNew.contains(newEpic));
        assertFalse(getPrioritizedTasksNew.contains(epicOld));
    }

    @Test
    @DisplayName("Обновит пустой epicOld на не пустой newEpic и также учтет это и нового Эпика не будет в списке приоритетов ,а его подзадача будет")
    public void updateEpicWithoutIntersection_shouldUpdateOldEmptyEpic_WhenNewEpicNotEmpty() {
        Epic epicOld = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        Epic newEpic = new Epic("newEpic", "newEpic", epicOld.getId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskFirst = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:30", 1);

        boolean isAdd = manager.addTaskWithoutIntersection(epicOld);
        List<Task> getPrioritizedTasksOld = manager.getPrioritizedTasks();
        newEpic.addSubtask(subtaskFirst);
        manager.updateEpicWithoutIntersection(newEpic, epicOld);
        manager.addTaskWithoutIntersection(subtaskFirst);
        List<Task> getPrioritizedTasksNew = manager.getPrioritizedTasks();

        assertTrue(isAdd);
        assertTrue(getPrioritizedTasksOld.contains(epicOld));
        assertFalse(getPrioritizedTasksNew.contains(newEpic));
        assertFalse(getPrioritizedTasksNew.contains(epicOld));
        assertTrue(getPrioritizedTasksNew.contains(subtaskFirst));
    }

    @Test
    @DisplayName("На пустой Эпик, обновиться не пустой Эпик и удалит его подзадачу, на пустой Эпик")
    public void shouldUpdateNotEmptyOldEpic_WhenNewEpicEmpty() {
        Epic epic1 = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        Epic newEpic = new Epic("newEpic", "newEpic", epic1.getId(), StatusTask.NEW, TypeTask.EPIC);

        manager.addTaskWithoutIntersection(epic1);
        //симуляция того что происходит в приложении когда добавляется подзадача к эпику
        epic1.addSubtask(subtask1);
        manager.removeTaskFromPrioritizedAndNullLists(epic1);
        manager.addTaskWithoutIntersection(subtask1);
        //
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        manager.removeTaskFromPrioritizedAndNullLists(subtask1);//симуляция снова
        manager.updateEpicWithoutIntersection(newEpic, epic1);
        List<Task> getPrioritizedTasks2 = manager.getPrioritizedTasks();

        assertFalse(getPrioritizedTasks.contains(epic1));
        assertTrue(getPrioritizedTasks.contains(subtask1));
        assertTrue(getPrioritizedTasks2.contains(newEpic));
        assertFalse(getPrioritizedTasks2.contains(epic1));
        assertFalse(getPrioritizedTasks2.contains(subtask1));
    }

    @Test
    @DisplayName("Обновит Эпик с подзадачей на Эпик с подзадачей и учтет все нюансы с подзадачами")
    public void updateEpicWithoutIntersection_shouldUpdateNotEmptyOldEpic_WhenNewEpicNotEmpty() {
        Epic epicOld = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        Epic newEpic = new Epic("newEpic", "newEpic", epicOld.getId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskOld = new Subtask("subtask1", "subtask1subtask1", 55, StatusTask.NEW, epicOld.getId(), TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        Subtask newSubtask = new Subtask("newSubtask", "newSubtask", 55, StatusTask.NEW, newEpic.getId(), TypeTask.SUBTASK, "21.03.2025 12:30", 1);

        manager.addTaskWithoutIntersection(epicOld);
        //симуляция того что происходит в приложении когда добавляется подзадача к эпику
        epicOld.addSubtask(subtaskOld);
        manager.removeTaskFromPrioritizedAndNullLists(epicOld);
        manager.addTaskWithoutIntersection(subtaskOld);
        //
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        newEpic.addSubtask(newSubtask);
        //имитация того что происходит когда у нового эпика есть подзадача относительно prioritizedManager
        manager.updateSubtaskWithoutIntersection(newSubtask, subtaskOld);
        manager.updateEpicWithoutIntersection(newEpic, epicOld);
        //
        List<Task> getPrioritizedTasks2 = manager.getPrioritizedTasks();

        assertFalse(getPrioritizedTasks.contains(epicOld));
        assertTrue(getPrioritizedTasks.contains(subtaskOld));
        assertFalse(getPrioritizedTasks2.contains(newEpic));
        assertFalse(getPrioritizedTasks2.contains(epicOld));
        assertFalse(getPrioritizedTasks2.contains(subtaskOld));
        assertTrue(getPrioritizedTasks2.contains(newSubtask));
    }

    @Test
    @DisplayName("Удаляет задачу из списка приоритетов")
    public void removeTaskFromPrioritizedAndNullLists_shouldRemoveTaskFromPrioritizedAndNullLists() {
        Task taskFirst = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 10);

        manager.addTaskWithoutIntersection(taskFirst);
        manager.removeTaskFromPrioritizedAndNullLists(taskFirst);
        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(getPrioritizedTasks.isEmpty());
    }

    @Test
    @DisplayName("Удаляет из списка приоритетов задачи которые были переданы в Map-е")
    public void clearAllTasksFromPrioritizedAndNullLists_shouldClearAllTasks() {
        Task taskNew = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 10);
        Task taskDone = new Task("task", "task1task1", 2, StatusTask.DONE, TypeTask.TASK, "21.04.2025 12:00", 10);

        manager.addTaskWithoutIntersection(taskNew);
        manager.addTaskWithoutIntersection(taskDone);
        List<Task> getPrioritizedTasksOld = manager.getPrioritizedTasks();
        Map<Integer, Task> tasks = new HashMap<>();
        tasks.put(taskNew.getId(), taskNew);
        tasks.put(taskDone.getId(), taskDone);
        manager.clearAllTasksFromPrioritizedAndNullLists(tasks);
        List<Task> getPrioritizedTasksNew = manager.getPrioritizedTasks();

        assertTrue(getPrioritizedTasksOld.contains(taskNew));
        assertTrue(getPrioritizedTasksOld.contains(taskDone));
        assertFalse(getPrioritizedTasksNew.contains(taskNew));
        assertFalse(getPrioritizedTasksNew.contains(taskDone));
    }

    @Test
    @DisplayName("Удаляет из списка приоритетов Epic-и которые были переданы в Map-е")
    public void clearAllTasksFromPrioritizedAndNullLists_shouldClearAllEpics() {
        Epic epicEmptyFirst = new Epic("newEpic", "newEpic", 88, StatusTask.NEW, TypeTask.EPIC);
        Epic epicEmptyTwo = new Epic("epic1", "epic1epic1", 22, StatusTask.DONE, TypeTask.EPIC);

        manager.addTaskWithoutIntersection(epicEmptyFirst);
        manager.addTaskWithoutIntersection(epicEmptyTwo);
        List<Task> getPrioritizedTasksOld = manager.getPrioritizedTasks();
        Map<Integer, Epic> epics = new HashMap<>();
        epics.put(epicEmptyFirst.getId(), epicEmptyFirst);
        epics.put(epicEmptyTwo.getId(), epicEmptyTwo);
        manager.clearAllEpicsFromPrioritizedAndNullLists(epics);
        List<Task> getPrioritizedTasksNew = manager.getPrioritizedTasks();

        assertTrue(getPrioritizedTasksOld.contains(epicEmptyFirst));
        assertTrue(getPrioritizedTasksOld.contains(epicEmptyTwo));
        assertFalse(getPrioritizedTasksNew.contains(epicEmptyFirst));
        assertFalse(getPrioritizedTasksNew.contains(epicEmptyTwo));
    }

    @Test
    @DisplayName("Удаляет из списка приоритетов подзадачи которые были переданы в Map-е")
    public void clearAllTasksFromPrioritizedAndNullLists_shouldClearAll() {
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        Subtask subtask2 = new Subtask("subtask1", "subtask1subtask1", 34, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2125 12:30", 1);

        manager.addTaskWithoutIntersection(subtask1);
        manager.addTaskWithoutIntersection(subtask2);
        List<Task> getPrioritizedTasksOld = manager.getPrioritizedTasks();
        Map<Integer, Subtask> subtasks = new HashMap<>();
        subtasks.put(subtask1.getId(), subtask1);
        subtasks.put(subtask2.getId(), subtask2);
        manager.clearAllSubtasksFromPrioritizedAndNullLists(subtasks);
        List<Task> getPrioritizedTasksNew = manager.getPrioritizedTasks();

        assertTrue(getPrioritizedTasksOld.contains(subtask1));
        assertTrue(getPrioritizedTasksOld.contains(subtask2));
        assertFalse(getPrioritizedTasksNew.contains(subtask1));
        assertFalse(getPrioritizedTasksNew.contains(subtask2));
    }

    @Test
    @DisplayName("Возвращает не пустой лист приоритетов")
    public void getListPrioritizedTasks() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 10);
        Task task2 = new Task("task", "task1task1", 2, StatusTask.NEW, TypeTask.TASK, "21.04.2025 12:00", 10);
        Epic epicEmpty = new Epic("newEpic", "newEpic", 88, StatusTask.NEW, TypeTask.EPIC);
        Epic epicEmpty2 = new Epic("epic1", "epic1epic1", 22, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        Subtask subtask2 = new Subtask("subtask1", "subtask1subtask1", 34, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2125 12:30", 1);

        manager.addTaskWithoutIntersection(task1);
        manager.addTaskWithoutIntersection(task2);
        manager.addTaskWithoutIntersection(epicEmpty);
        manager.addTaskWithoutIntersection(epicEmpty2);
        manager.addTaskWithoutIntersection(subtask1);
        manager.addTaskWithoutIntersection(subtask2);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(task1));
        assertTrue(getPrioritizedTasks.contains(task2));
        assertTrue(getPrioritizedTasks.contains(epicEmpty));
        assertTrue(getPrioritizedTasks.contains(epicEmpty2));
        assertTrue(getPrioritizedTasks.contains(subtask1));
        assertTrue(getPrioritizedTasks.contains(subtask2));
    }
}