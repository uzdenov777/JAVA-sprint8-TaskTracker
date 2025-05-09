package modelTest;

import manager.Managers;
import manager.enums.StatusTask;
import manager.enums.TypeTask;
import manager.interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {
    TaskManager manager;
    Epic epic;

    @BeforeEach
    public void setManager() {
        manager = Managers.getInMemoryTaskManager();
        epic = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        manager.addEpic(epic);
    }

    @Test
    @DisplayName("Должен успешно добавить подзадачу")
    public void addSubtask_returnTrueIsAddSubtask() {
        Subtask subtaskFirst = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        boolean isAddSubtaskFirst = manager.addSubtask(subtaskFirst);

        assertTrue(isAddSubtaskFirst);
    }

    @Test
    @DisplayName("Должно успешно добавить две подзадачи без пересечения")
    public void addSubtask_returnTrueIsTwoAddSubtaskWithTwoDifferentDates() {
        Subtask subtaskFirst = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtaskOther = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);

        boolean isAddSubtaskFirst = manager.addSubtask(subtaskFirst);
        boolean isAddSubtaskOther = manager.addSubtask(subtaskOther);

        assertTrue(isAddSubtaskFirst);
        assertTrue(isAddSubtaskOther);
    }

    @Test
    @DisplayName("должно успешно добавить subtaskFirst ,а subtaskOther не получиться из-за пересечения")
    public void addSubtask_returnTrueAndFalseIsTwoAddSubtaskWithTwoIdenticalDates() {
        Subtask subtaskFirst = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 1);
        Subtask subtaskOther = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);

        boolean isAddSubtaskFirst = manager.addSubtask(subtaskFirst);
        boolean isAddSubtaskOther = manager.addSubtask(subtaskOther);

        assertTrue(isAddSubtaskFirst);
        assertFalse(isAddSubtaskOther);
    }

    @Test
    @DisplayName("Не должен добавить subtaskOther, потому что ID уже занят subtaskFirst")
    public void addSubtask_returnFalseAddSubtaskTheSameID() {
        int id = manager.getNewId();
        Subtask subtaskFirst = new Subtask("subtask1", "subtask1subtask1", id, StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 1);
        Subtask subtaskOther = new Subtask("subtask2", "subtask2subtask2", id, StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);

        boolean isAddSubtaskFirst = manager.addSubtask(subtaskFirst);
        boolean isAddSubtaskOther = manager.addSubtask(subtaskOther);

        Assertions.assertTrue(isAddSubtaskFirst);
        assertFalse(isAddSubtaskOther);
    }

    @Test
    @DisplayName("Не должен даже инициализировать подзадачу пока ее продолжительность меньше минуты")
    public void shouldThrowExceptionDuration0() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 0)
        );

        assertEquals("Продолжительность задачи принимается от 1 минуты.", exception.getMessage());
    }

    @Test
    @DisplayName("Не должен даже инициализировать подзадачи пока неверный формат или отсутствует дата старта подзадачи ,валидный формат для Task, Subtask -> ДД.ММ.ГГГГ ЧЧ:ММ")
    public void shouldThrowExceptionDataNullAndInvalidFormat() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, null, 1)
        );
        IllegalArgumentException exception1 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "2004.25.12", 1)
        );

        assertEquals("Передан неверный формат даты или null для Task и Subtask. Валидный формат для Task, Subtask -> ДД.ММ.ГГГГ ЧЧ:ММ", exception.getMessage());
        assertEquals("Передан неверный формат даты или null для Task и Subtask. Валидный формат для Task, Subtask -> ДД.ММ.ГГГГ ЧЧ:ММ", exception1.getMessage());
    }

    @Test
    @DisplayName("Должен успешно удалить подзадачу по ID, которая ранее была добавлена")
    public void removeSubtaskById_returnEmptyListAllSubtasks_addedAfterRemoveSubtask() {
        Subtask subtaskFirst = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 1);

        manager.addSubtask(subtaskFirst);
        manager.removeSubtaskById(subtaskFirst.getId());
        List<Subtask> subtasks = manager.getListSubtasks();

        assertTrue(subtasks.isEmpty());
    }

    @Test
    @DisplayName("Должен очистить полностью список всех подзадач")
    public void clearSubtasks_returnEmptyListAllSubtasks() {
        Subtask subtaskFirst = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtaskOther = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);

        manager.addSubtask(subtaskFirst);
        manager.addSubtask(subtaskOther);
        manager.clearSubtasks();

        List<Subtask> subtasks = manager.getListSubtasks();
        assertTrue(subtasks.isEmpty());
    }

    @Test
    @DisplayName("Проверяет правильно ли вычисляется окончание подзадачи, окончание = дата начала+продолжительность")
    public void returnEndTime2025_03_20T11_01() {
        Subtask subtaskFirst = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 11:00", 1);

        manager.addSubtask(subtaskFirst);
        LocalDateTime endTime = subtaskFirst.getEndTime();
        LocalDateTime expectedEndTime = LocalDateTime.of(2025, 3, 20, 11, 1);

        assertEquals(expectedEndTime, endTime);
    }

    @Test
    @DisplayName("Должен вернуть ID Эпика к которому принадлежит подзадача, подзадача без Эпика не может существовать")
    public void returnIdEpic(){
        Subtask subtaskFirst = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 11:00", 1);
        manager.addSubtask(subtaskFirst);

        int idEpicExpected = epic.getId();
        int idEpic = subtaskFirst.getIdEpic();

        assertEquals(idEpicExpected, idEpic);
    }
}