package modelTest;

import manager.Managers;
import manager.enums.StatusTask;
import manager.enums.TypeTask;
import manager.interfaces.TaskManager;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    TaskManager manager;

    @BeforeEach
    public void setManager() {
        manager = Managers.getInMemoryTaskManager();
    }

    @Test
    @DisplayName("Должен успешно добавить задачу")
    public void addTask_returnTrueIsAddTask() {
        Task taskFirst = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);

        boolean isAdd = manager.addTask(taskFirst);

        assertTrue(isAdd);
    }

    @Test
    @DisplayName("Должен успешно добавить две задачи у которых нет пересечения")
    public void addTask_returnTrueIsTwoAddTaskWithTwoDifferentDates() {//Добавление двух разных задач с разными датами
        Task taskFirst = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Task taskOther = new Task("task2", "task2task2", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "20.03.2025 12:00", 30);

        boolean isAddTaskFirst = manager.addTask(taskFirst);
        boolean isAddTaskOther = manager.addTask(taskOther);

        assertTrue(isAddTaskFirst);
        assertTrue(isAddTaskOther);
    }

    @Test
    @DisplayName("Не должен успешно добавить две задачи у которых есть пересечение")
    public void addTask_returnTrueAndFalseIsTwoAddTask_WithTwoIdenticalDates() {//Пересечение
        Task taskFirst = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "20.03.2025 12:00", 30);
        Task taskOther = new Task("task2", "task2task2", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "20.03.2025 12:10", 30);

        boolean isAddTaskFirst = manager.addTask(taskFirst);
        boolean isAddTaskOther = manager.addTask(taskOther);

        assertTrue(isAddTaskFirst);
        assertFalse(isAddTaskOther);
    }

    @Test
    @DisplayName("Нельзя добавить две задачи с одним ID")
    public void addTask_returnFalseAddTaskTheSameID() {
        int id = manager.getNewId();
        Task taskFirst = new Task("task", "task1task1", id, StatusTask.NEW, TypeTask.TASK, "20.03.2025 11:00", 1);
        Task taskOther = new Task("task2", "task2task2", id, StatusTask.NEW, TypeTask.TASK, "20.03.2025 12:00", 1);

        boolean isAddTaskFirst = manager.addTask(taskFirst);
        boolean isAddTaskOther = manager.addTask(taskOther);

        assertTrue(isAddTaskFirst);
        assertFalse(isAddTaskOther);
    }

    @Test
    @DisplayName("Не должен добавить задачу, в задачах и подзадачах продолжительность должна быть от 1 минуты")
    public void shouldThrowExceptionDuration0() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 0)
        );

        assertEquals("Продолжительность задачи принимается от 1 минуты.", exception.getMessage());
    }

    @Test
    @DisplayName("Не должен добавить задачу, в задачах и подзадачах дата начала должна быть и валидный формат ДД.ММ.ГГГГ ЧЧ:ММ")
    public void shouldThrowExceptionDataNullAndInvalidFormat() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, null, 1)
        );
        IllegalArgumentException exception1 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "2004.25.12", 1)
        );

        assertEquals("Передан неверный формат даты или null для Task и Subtask. Валидный формат для Task, Subtask -> ДД.ММ.ГГГГ ЧЧ:ММ", exception.getMessage());
        assertEquals("Передан неверный формат даты или null для Task и Subtask. Валидный формат для Task, Subtask -> ДД.ММ.ГГГГ ЧЧ:ММ", exception1.getMessage());
    }

    @Test
    @DisplayName("Успешно удаляет заранее добавленную задачу по ID")
    public void removeTaskById_returnEmptyLIstTasksAddAfterRemoveTask() {
        Task taskFirst = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "20.03.2025 12:00", 1);

        manager.addTask(taskFirst);
        manager.removeTaskById(taskFirst.getId());
        List<Task> tasks = manager.getListTasks();

        assertTrue(tasks.isEmpty());
    }

    @Test
    @DisplayName("Успешно очищает список всех заранее добавленную задач")
    public void clearTasks_returnEmptyListAllTasks() {
        Task taskFirst = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "20.03.2025 11:00", 1);
        Task taskOther = new Task("task2", "task2task2", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "20.03.2025 12:00", 1);

        manager.addTask(taskFirst);
        manager.addTask(taskOther);
        manager.clearTasks();
        List<Task> tasks = manager.getListTasks();

        assertTrue(tasks.isEmpty());
    }

    @Test
    @DisplayName("Проверяет правильно ли вычисляется окончание задачи = начала задача+продолжительность")
    public void returnEndTime2025_03_20T11_01() {
        Task taskFirst = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "20.03.2025 11:00", 1);

        manager.addTask(taskFirst);
        LocalDateTime endTime = taskFirst.getEndTime();
        LocalDateTime expectedEndTime = LocalDateTime.of(2025, 3, 20, 11, 1);

        assertEquals(expectedEndTime, endTime);
    }
}