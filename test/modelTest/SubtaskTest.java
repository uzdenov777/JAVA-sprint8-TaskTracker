package modelTest;

import manager.Managers;
import manager.enums.StatusTask;
import manager.enums.TypeTask;
import manager.interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class SubtaskTest {
    TaskManager manager;
    Epic epic;

    @BeforeEach
    public void setManager() {
        manager = Managers.getDefault();
        epic = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        manager.addEpic(epic);
    }

    @Test
    public void returnTrueIsAddTask() {
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        boolean isAdd = manager.addSubtask(subtask1);

        Assertions.assertTrue(isAdd);
    }

    @Test
    public void returnTrueIsTwoAddTaskWithTwoDifferentDates() {
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);

        boolean isAdd = manager.addSubtask(subtask1);
        boolean isAdd2 = manager.addSubtask(subtask2);
        Assertions.assertTrue(isAdd);
        Assertions.assertTrue(isAdd2);
    }

    @Test
    public void returnTrueAndFalseIsTwoAddTaskWithTwoIdenticalDates() {
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);

        boolean isAdd = manager.addSubtask(subtask1);
        boolean isAdd2 = manager.addSubtask(subtask2);

        Assertions.assertTrue(isAdd);
        Assertions.assertFalse(isAdd2);
    }

    @Test
    public void returnFalseAddTaskTheSameID() {
        int id = manager.getNewId();
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", id, StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", id, StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);

        boolean addTrue = manager.addSubtask(subtask1);
        boolean addFalse = manager.addSubtask(subtask2);

        Assertions.assertTrue(addTrue);
        Assertions.assertFalse(addFalse);
    }

    @Test
    public void shouldThrowExceptionDuration0() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 0)
        );

        Assertions.assertEquals("Продолжительность задачи принимается от 1 минуты.", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionDataNullAndInvalidFormat() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, null, 1)
        );
        Assertions.assertEquals("Передан неверный формат даты или null для Task и Subtask. Валидный формат для Task, Subtask -> ДД.ММ.ГГГГ ЧЧ:ММ", exception.getMessage());

        IllegalArgumentException exception1 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "2004.25.12", 1)
        );
        Assertions.assertEquals("Передан неверный формат даты или null для Task и Subtask. Валидный формат для Task, Subtask -> ДД.ММ.ГГГГ ЧЧ:ММ", exception1.getMessage());
    }

    @Test
    public void return0AddAfterRemoveTask() {
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 1);

        manager.addSubtask(subtask1);
        manager.removeSubtaskById(subtask1.getId());

        int size = manager.getListSubtasks().size();

        Assertions.assertEquals(0, size);
    }

    @Test
    public void returnSize0ClearTasks() {
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);


        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.clearSubtasks();

        int size = manager.getListTasks().size();
        Assertions.assertEquals(0, size);
    }

    @Test
    public void returnEndTime2025_03_20T11_01() {
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 11:00", 1);
        manager.addSubtask(subtask1);
        LocalDateTime endTime = subtask1.getEndTime();
        LocalDateTime expectedEndTime = LocalDateTime.of(2025, 3, 20, 11, 1);
        Assertions.assertEquals(expectedEndTime, endTime);
    }

    @Test
    public void returnIdEpic(){
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 11:00", 1);
        manager.addSubtask(subtask1);

        int idEpicExpected = epic.getId();
        int idEpic = subtask1.getIdEpic();

        Assertions.assertEquals(idEpicExpected, idEpic);
    }
}