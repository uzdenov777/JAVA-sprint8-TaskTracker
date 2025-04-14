package managerTest.abstractManagersTest;

import manager.enums.StatusTask;
import manager.enums.TypeTask;
import manager.interfaces.PrioritizedManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
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
    public void returnTrueAddTaskWithoutIntersection_WhenTasksByPriorityEmpty() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        boolean isAdd = manager.addTaskWithoutIntersection(task1);
        assertTrue(isAdd);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(task1));
    }

    @Test
    public void returnTrueAddWithoutIntersection_WhenNotIntersection() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        boolean isAdd = manager.addTaskWithoutIntersection(task1);
        boolean isSubtaskAdded = manager.addTaskWithoutIntersection(subtask1);

        assertTrue(isAdd);
        assertTrue(isSubtaskAdded);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(task1));
        assertTrue(getPrioritizedTasks.contains(subtask1));
    }

    @Test
    public void returnTrueAddWithoutIntersection_WhenPossibleBetweenTwoTasksWithoutIntersection() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", 3, StatusTask.NEW, 23, TypeTask.SUBTASK, "22.03.2025 12:00", 1);
        boolean isAdd = manager.addTaskWithoutIntersection(task1);
        boolean isSubtaskAdded = manager.addTaskWithoutIntersection(subtask1);
        boolean isSubtaskAdded2 = manager.addTaskWithoutIntersection(subtask2);

        assertTrue(isAdd);
        assertTrue(isSubtaskAdded);
        assertTrue(isSubtaskAdded2);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(task1));
        assertTrue(getPrioritizedTasks.contains(subtask1));
        assertTrue(getPrioritizedTasks.contains(subtask2));
    }

    @Test
    public void returnTrueAddWithoutIntersection_WhenAddNotEmptyEpicButWillNotAddEpicToThePriorityList() {//Удалит Эпик из списков приоритетов когда в него добавлена подзадача
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        boolean isAdd = manager.addTaskWithoutIntersection(task1);
        boolean isAddEpic = manager.addTaskWithoutIntersection(epic1);

        epic1.addSubtask(subtask1);//это методы вызываются в другом классе
        manager.removeTaskFromPrioritizedAndNullLists(epic1);// симуляция

        boolean isSubtaskAdded = manager.addTaskWithoutIntersection(subtask1);

        assertTrue(isAdd);
        assertTrue(isAddEpic);
        assertTrue(isSubtaskAdded);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(task1));
        assertTrue(getPrioritizedTasks.contains(subtask1));

        assertFalse(getPrioritizedTasks.contains(epic1));
    }

    @Test
    public void returnTrueAddWithoutIntersection_WhenAddEmptyEpic() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        boolean isAdd = manager.addTaskWithoutIntersection(task1);
        boolean isAddEpic = manager.addTaskWithoutIntersection(epic1);

        assertTrue(isAdd);
        assertTrue(isAddEpic);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(task1));
        assertTrue(getPrioritizedTasks.contains(epic1));
    }

    @Test
    public void returnFalseAddWithoutIntersection_NewTaskStartedEarlierThanTheEndOfThePreviousTask() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 30);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:15", 1);
        boolean isAdd = manager.addTaskWithoutIntersection(task1);
        boolean isSubtaskAdded = manager.addTaskWithoutIntersection(subtask1);

        assertTrue(isAdd);
        assertFalse(isSubtaskAdded);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(task1));
        assertFalse(getPrioritizedTasks.contains(subtask1));
    }

    @Test
    public void returnFalseAddWithoutIntersection_NewTaskEndsLaterThanNextTaskStart() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 30);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 11:55", 10);
        boolean isAdd = manager.addTaskWithoutIntersection(task1);
        boolean isSubtaskAdded = manager.addTaskWithoutIntersection(subtask1);

        assertTrue(isAdd);
        assertFalse(isSubtaskAdded);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(task1));
        assertFalse(getPrioritizedTasks.contains(subtask1));
    }

    @Test
    public void returnFalseAddWithoutIntersection_WhenPossibleBetweenTwoTasksIntersection_NewTaskEndsLaterThanNextTaskStart() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 15);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", 3, StatusTask.NEW, 23, TypeTask.SUBTASK, "21.03.2025 12:20", 15);
        boolean isAdd = manager.addTaskWithoutIntersection(task1);
        boolean isSubtaskAdded = manager.addTaskWithoutIntersection(subtask1);
        boolean isSubtaskAdded2 = manager.addTaskWithoutIntersection(subtask2);

        assertTrue(isAdd);
        assertTrue(isSubtaskAdded);
        assertFalse(isSubtaskAdded2);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(task1));
        assertTrue(getPrioritizedTasks.contains(subtask1));
        assertFalse(getPrioritizedTasks.contains(subtask2));
    }

    @Test
    public void returnFalseAddWithoutIntersection_TheNewTaskStartedEarlierThanTheEndOfThePreviousTask() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 15);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", 3, StatusTask.NEW, 23, TypeTask.SUBTASK, "21.03.2025 12:10", 15);
        boolean isAdd = manager.addTaskWithoutIntersection(task1);
        boolean isSubtaskAdded = manager.addTaskWithoutIntersection(subtask1);
        boolean isSubtaskAdded2 = manager.addTaskWithoutIntersection(subtask2);

        assertTrue(isAdd);
        assertTrue(isSubtaskAdded);
        assertFalse(isSubtaskAdded2);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(task1));
        assertTrue(getPrioritizedTasks.contains(subtask1));
        assertFalse(getPrioritizedTasks.contains(subtask2));
    }

    @Test
    public void returnTrueUpdateTaskWithoutIntersection_WhenOneTaskOnTheList() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 15);
        manager.addTaskWithoutIntersection(task1);

        Task task2 = new Task("task2", "task2", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 15);
        boolean isUpdate = manager.updateTaskWithoutIntersection(task2, task1);

        assertTrue(isUpdate);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(task2));

        assertFalse(getPrioritizedTasks.contains(task1));
    }

    @Test
    public void returnTrueUpdateTaskWithoutIntersection_WhenNotIntersectionOccurs() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "25.03.2025 12:00", 1);
        manager.addTaskWithoutIntersection(task1);
        manager.addTaskWithoutIntersection(subtask1);
        manager.addTaskWithoutIntersection(subtask2);


        Subtask newSubtask = new Subtask("newUpdate", "newUpdate", subtask1.getId(), StatusTask.NEW, 22, TypeTask.SUBTASK, "22.03.2025 12:05", 30);
        boolean isUpdate = manager.updateSubtaskWithoutIntersection(newSubtask, subtask1);
        assertTrue(isUpdate);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(task1));
        assertTrue(getPrioritizedTasks.contains(newSubtask));
        assertTrue(getPrioritizedTasks.contains(subtask2));

        assertFalse(getPrioritizedTasks.contains(subtask1));
    }


    @Test
    public void returnFalseUpdateTaskWithoutIntersection_TheNewTaskStartedEarlierThanTheEndOfThePreviousTask() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 10);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", 4, StatusTask.NEW, 22, TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        manager.addTaskWithoutIntersection(task1);
        manager.addTaskWithoutIntersection(subtask1);
        manager.addTaskWithoutIntersection(subtask2);

        int idUpdateTask = subtask1.getId();
        Subtask newSubtask = new Subtask("newUpdate", "newUpdate", idUpdateTask, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:05", 1);
        boolean isUpdate = manager.updateSubtaskWithoutIntersection(newSubtask, subtask1);
        assertFalse(isUpdate);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(task1));
        assertTrue(getPrioritizedTasks.contains(subtask1));
        assertTrue(getPrioritizedTasks.contains(subtask2));

        assertFalse(getPrioritizedTasks.contains(newSubtask));
    }

    @Test
    public void returnFalseUpdateTaskWithoutIntersection_NewTaskEndsLaterThanNextTaskStart() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 10);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", 4, StatusTask.NEW, 22, TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        manager.addTaskWithoutIntersection(task1);
        manager.addTaskWithoutIntersection(subtask1);
        manager.addTaskWithoutIntersection(subtask2);

        int idUpdateTask = subtask1.getId();
        Subtask newSubtask = new Subtask("newUpdate", "newUpdate", idUpdateTask, StatusTask.NEW, 22, TypeTask.SUBTASK, "24.03.2025 11:55", 10);
        boolean isUpdate = manager.updateSubtaskWithoutIntersection(newSubtask, subtask1);
        assertFalse(isUpdate);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(task1));
        assertTrue(getPrioritizedTasks.contains(subtask1));
        assertTrue(getPrioritizedTasks.contains(subtask2));

        assertFalse(getPrioritizedTasks.contains(newSubtask));
    }

    @Test
    public void shouldUpdateOldEmptyEpic_WhenNewEpicEmpty() {
        Epic epic1 = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        boolean isAdd = manager.addTaskWithoutIntersection(epic1);
        assertTrue(isAdd);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(epic1));

        Epic newEpic = new Epic("newEpic", "newEpic", epic1.getId(), StatusTask.NEW, TypeTask.EPIC);
        manager.updateEpicWithoutIntersection(newEpic, epic1);

        List<Task> getPrioritizedTasks2 = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks2.contains(newEpic));

        assertFalse(getPrioritizedTasks2.contains(epic1));
    }

    @Test
    public void shouldUpdateOldEmptyEpic_WhenNewEpicNotEmpty() {
        Epic epic1 = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        boolean isAdd = manager.addTaskWithoutIntersection(epic1);
        assertTrue(isAdd);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks.contains(epic1));

        Epic newEpic = new Epic("newEpic", "newEpic", epic1.getId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        newEpic.addSubtask(subtask1);
        manager.updateEpicWithoutIntersection(newEpic, epic1);
        manager.addTaskWithoutIntersection(subtask1);

        List<Task> getPrioritizedTasks2 = manager.getPrioritizedTasks();
        assertFalse(getPrioritizedTasks2.contains(newEpic));
        assertFalse(getPrioritizedTasks2.contains(epic1));

        assertTrue(getPrioritizedTasks2.contains(subtask1));
    }

    @Test
    public void shouldUpdateNotEmptyOldEpic_WhenNewEpicEmpty() {
        Epic epic1 = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        manager.addTaskWithoutIntersection(epic1);

        //симуляция того что происходит в приложении когда добавляется подзадача к эпику
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        epic1.addSubtask(subtask1);
        manager.removeTaskFromPrioritizedAndNullLists(epic1);
        manager.addTaskWithoutIntersection(subtask1);


        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertFalse(getPrioritizedTasks.contains(epic1));
        assertTrue(getPrioritizedTasks.contains(subtask1));

        Epic newEpic = new Epic("newEpic", "newEpic", epic1.getId(), StatusTask.NEW, TypeTask.EPIC);
        manager.removeTaskFromPrioritizedAndNullLists(subtask1);//симуляция снова
        manager.updateEpicWithoutIntersection(newEpic, epic1);

        List<Task> getPrioritizedTasks2 = manager.getPrioritizedTasks();
        assertTrue(getPrioritizedTasks2.contains(newEpic));

        assertFalse(getPrioritizedTasks2.contains(epic1));
        assertFalse(getPrioritizedTasks2.contains(subtask1));
    }

    @Test
    public void shouldUpdateNotEmptyOldEpic_WhenNewEpicNotEmpty() {
        Epic epic1 = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        manager.addTaskWithoutIntersection(epic1);

        //симуляция того что происходит в приложении когда добавляется подзадача к эпику
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 55, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        epic1.addSubtask(subtask1);
        manager.removeTaskFromPrioritizedAndNullLists(epic1);
        manager.addTaskWithoutIntersection(subtask1);

        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertFalse(getPrioritizedTasks.contains(epic1));
        assertTrue(getPrioritizedTasks.contains(subtask1));

        Epic newEpic = new Epic("newEpic", "newEpic", epic1.getId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask newSubtask = new Subtask("newSubtask", "newSubtask", 55, StatusTask.NEW, newEpic.getId(), TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        newEpic.addSubtask(newSubtask);

        //имитация того что происходит когда у нового эпика есть подзадача относительно prioritizedManager
        manager.updateSubtaskWithoutIntersection(newSubtask, subtask1);
        manager.updateEpicWithoutIntersection(newEpic, epic1);

        List<Task> getPrioritizedTasks2 = manager.getPrioritizedTasks();
        assertFalse(getPrioritizedTasks2.contains(newEpic));
        assertFalse(getPrioritizedTasks2.contains(epic1));
        assertFalse(getPrioritizedTasks2.contains(subtask1));

        assertTrue(getPrioritizedTasks2.contains(newSubtask));
    }

    @Test
    public void shouldRemoveTaskFromPrioritizedAndNullLists() {
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 10);
        Epic epicEmpty = new Epic("newEpic", "newEpic", 88, StatusTask.NEW, TypeTask.EPIC);
        Epic epic1 = new Epic("epic1", "epic1epic1", 2, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        manager.addTaskWithoutIntersection(task1);
        manager.addTaskWithoutIntersection(epicEmpty);
        manager.addTaskWithoutIntersection(subtask1);


        List<Task> getPrioritizedTasks = manager.getPrioritizedTasks();
        assertFalse(getPrioritizedTasks.contains(epic1));

        assertTrue(getPrioritizedTasks.contains(task1));
        assertTrue(getPrioritizedTasks.contains(epicEmpty));
        assertTrue(getPrioritizedTasks.contains(subtask1));

        manager.removeTaskFromPrioritizedAndNullLists(epic1);
        List<Task> getPrioritizedTasks2 = manager.getPrioritizedTasks();
        assertFalse(getPrioritizedTasks2.contains(epic1));

        assertTrue(getPrioritizedTasks2.contains(task1));
        assertTrue(getPrioritizedTasks2.contains(epicEmpty));
        assertTrue(getPrioritizedTasks2.contains(subtask1));

        manager.removeTaskFromPrioritizedAndNullLists(epicEmpty);
        List<Task> getPrioritizedTasks3 = manager.getPrioritizedTasks();
        assertFalse(getPrioritizedTasks3.contains(epic1));
        assertFalse(getPrioritizedTasks3.contains(epicEmpty));

        assertTrue(getPrioritizedTasks3.contains(task1));
        assertTrue(getPrioritizedTasks3.contains(subtask1));

        manager.removeTaskFromPrioritizedAndNullLists(subtask1);
        List<Task> getPrioritizedTasks4 = manager.getPrioritizedTasks();
        assertFalse(getPrioritizedTasks4.contains(epic1));
        assertFalse(getPrioritizedTasks4.contains(epicEmpty));
        assertFalse(getPrioritizedTasks4.contains(subtask1));

        assertTrue(getPrioritizedTasks4.contains(task1));

        manager.removeTaskFromPrioritizedAndNullLists(task1);
        List<Task> getPrioritizedTasks5 = manager.getPrioritizedTasks();
        assertFalse(getPrioritizedTasks5.contains(epic1));
        assertFalse(getPrioritizedTasks5.contains(epicEmpty));
        assertFalse(getPrioritizedTasks5.contains(subtask1));
        assertFalse(getPrioritizedTasks5.contains(task1));

        assertTrue(getPrioritizedTasks5.isEmpty());
    }

    @Test
    public void shouldClearAll(){
        Task task1 = new Task("task", "task1task1", 1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 10);
        Task task2 = new Task("task", "task1task1", 2, StatusTask.NEW, TypeTask.TASK, "21.04.2025 12:00", 10);
        Epic epicEmpty = new Epic("newEpic", "newEpic", 88, StatusTask.NEW, TypeTask.EPIC);
        Epic epicEmpty2 = new Epic("epic1", "epic1epic1", 22, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", 3, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2025 12:30", 1);
        Subtask subtask2 = new Subtask("subtask1", "subtask1subtask1", 34, StatusTask.NEW, 22, TypeTask.SUBTASK, "21.03.2125 12:30", 1);

        Map<Integer ,Task> tasks = new HashMap<>();
        tasks.put(task1.getId(), task1);
        tasks.put(task2.getId(), task2);

        Map<Integer ,Epic> epics = new HashMap<>();
        epics.put(epicEmpty.getId(), epicEmpty);
        epics.put(epicEmpty2.getId(), epicEmpty2);

        Map<Integer ,Subtask> subtasks = new HashMap<>();
        subtasks.put(subtask1.getId(), subtask1);
        subtasks.put(subtask2.getId(), subtask2);

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

        manager.clearAllTasksFromPrioritizedAndNullLists(tasks);
        List<Task> getPrioritizedTasks2 = manager.getPrioritizedTasks();
        assertFalse(getPrioritizedTasks2.contains(task1));
        assertFalse(getPrioritizedTasks2.contains(task2));

        assertTrue(getPrioritizedTasks2.contains(epicEmpty));
        assertTrue(getPrioritizedTasks2.contains(epicEmpty2));
        assertTrue(getPrioritizedTasks2.contains(subtask1));
        assertTrue(getPrioritizedTasks2.contains(subtask2));

        manager.clearAllEpicsFromPrioritizedAndNullLists(epics);
        List<Task> getPrioritizedTasks3 = manager.getPrioritizedTasks();
        assertFalse(getPrioritizedTasks3.contains(task1));
        assertFalse(getPrioritizedTasks3.contains(task2));
        assertFalse(getPrioritizedTasks3.contains(epicEmpty));
        assertFalse(getPrioritizedTasks3.contains(epicEmpty2));

        assertTrue(getPrioritizedTasks3.contains(subtask1));
        assertTrue(getPrioritizedTasks3.contains(subtask2));

        manager.clearAllSubtasksFromPrioritizedAndNullLists(subtasks);
        List<Task> getPrioritizedTasks4 = manager.getPrioritizedTasks();
        assertFalse(getPrioritizedTasks4.contains(task1));
        assertFalse(getPrioritizedTasks4.contains(task2));
        assertFalse(getPrioritizedTasks4.contains(epicEmpty));
        assertFalse(getPrioritizedTasks4.contains(epicEmpty2));
        assertFalse(getPrioritizedTasks4.contains(subtask1));
        assertFalse(getPrioritizedTasks4.contains(subtask2));
    }

    @Test
    public void getListPrioritizedTasks(){
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
