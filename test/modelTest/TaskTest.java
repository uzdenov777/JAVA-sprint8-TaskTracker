package modelTest;

import manager.Managers;
import manager.enums.StatusTask;
import manager.enums.TypeTask;
import manager.interfaces.TaskManager;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskTest {

    TaskManager manager;

    @BeforeEach
    public void setManager() {
        manager = Managers.getDefault();
    }

    @Test
    public void returnTrueIsAddTask() {
        Task task = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        boolean isAdd = manager.addTask(task);

        Assertions.assertTrue(isAdd);
    }

    @Test
    public void returnTrueIsTwoAddTaskWithTwoDifferentDates() {//Добавление двух разных задач с разными датами
        Task task = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Task task2 = new Task("task2", "task2task2", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "20.03.2025 12:00", 30);

        boolean isAdd = manager.addTask(task);
        boolean isAdd2 = manager.addTask(task2);
        Assertions.assertTrue(isAdd);
        Assertions.assertTrue(isAdd2);
    }

    @Test
    public void returnTrueAndFalseIsTwoAddTask_WithTwoIdenticalDates() {//Пересечение
        Task task = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "20.03.2025 12:00", 30);
        Task task2 = new Task("task2", "task2task2", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "20.03.2025 12:10", 30);

        boolean isAdd = manager.addTask(task);
        boolean isAdd2 = manager.addTask(task2);

        Assertions.assertTrue(isAdd);
        Assertions.assertFalse(isAdd2);
    }

    @Test
    public void returnFalseAddTaskTheSameID() {
        int id = manager.getNewId();
        Task task = new Task("task", "task1task1", id, StatusTask.NEW, TypeTask.TASK, "20.03.2025 11:00", 1);
        Task task2 = new Task("task2", "task2task2", id, StatusTask.NEW, TypeTask.TASK, "20.03.2025 12:00", 1);

        boolean addTrue = manager.addTask(task);
        boolean addFalse = manager.addTask(task2);

        Assertions.assertTrue(addTrue);
        Assertions.assertFalse(addFalse);
    }

    @Test
    public void shouldThrowExceptionDuration0() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 0)
        );

        Assertions.assertEquals("Продолжительность задачи принимается от 1 минуты.", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionDataNullAndInvalidFormat() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, null, 1)
        );
        Assertions.assertEquals("Передан неверный формат даты или null для Task и Subtask. Валидный формат для Task, Subtask -> ДД.ММ.ГГГГ ЧЧ:ММ", exception.getMessage());

        IllegalArgumentException exception1 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "2004.25.12", 1)
        );
        Assertions.assertEquals("Передан неверный формат даты или null для Task и Subtask. Валидный формат для Task, Subtask -> ДД.ММ.ГГГГ ЧЧ:ММ", exception1.getMessage());
    }

    @Test
    public void return0AddAfterRemoveTask() {
        Task task = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "20.03.2025 12:00", 1);
        manager.addTask(task);
        manager.removeTaskById(task.getId());

        int size = manager.getListTasks().size();

        Assertions.assertEquals(0, size);
    }

    @Test
    public void returnSize0ClearTasks() {
        Task task = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "20.03.2025 11:00", 1);
        Task task2 = new Task("task2", "task2task2", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "20.03.2025 12:00", 1);
        manager.addTask(task);
        manager.addTask(task2);
        manager.clearTasks();

        int size = manager.getListTasks().size();
        Assertions.assertEquals(0, size);
    }

    @Test
    public void returnEndTime2025_03_20T11_01() {
        Task task = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "20.03.2025 11:00", 1);
        manager.addTask(task);
        LocalDateTime endTime = task.getEndTime();
        LocalDateTime expectedEndTime = LocalDateTime.of(2025,3, 20, 11, 1);
       Assertions.assertEquals(expectedEndTime, endTime);
    }
}