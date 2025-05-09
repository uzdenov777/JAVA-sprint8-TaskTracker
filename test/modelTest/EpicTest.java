package modelTest;

import manager.Managers;
import manager.enums.StatusTask;
import manager.enums.TypeTask;
import manager.interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager manager;

    @BeforeEach
    public void setManager() {
        manager = Managers.getInMemoryTaskManager();
    }

    @Test
    @DisplayName("Возвращает пустой список подзадач у Эпика сразу после момента добавления его")
    public void returnMapSubtasksEmpty() {
        Epic epicFirst = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);

        manager.addEpic(epicFirst);
        HashMap<Integer, Subtask> subtaskHashMap = epicFirst.getSubtasksMap();

        assertTrue(subtaskHashMap.isEmpty());
    }

    @Test
    @DisplayName("Проверяет что при добавлении подзадач со статусом NEW у эпика тоже будет статус NEW")
    public void returnNewStatusEpicAllSubtasksStatusNew() {
        Epic epicFirst = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskOne = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epicFirst.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtaskTwo = new Subtask("clear", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epicFirst.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        Subtask subtaskThree = new Subtask("subtask3", "subtask3subtask3", manager.getNewId(), StatusTask.NEW, epicFirst.getId(), TypeTask.SUBTASK, "27.03.2025 12:00", 30);

        manager.addEpic(epicFirst);
        manager.addSubtask(subtaskOne);
        manager.addSubtask(subtaskTwo);
        manager.addSubtask(subtaskThree);

        assertEquals(StatusTask.NEW, epicFirst.getStatus());
    }

    @Test
    @DisplayName("Проверяет что при добавлении подзадач со статусом DONE у Эпика тоже будет статус DONE")
    public void returnDoneStatusEpicAllSubtasksStatusDone() {
        Epic epicFirst = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskOne = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.DONE, epicFirst.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtaskTwo = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.DONE, epicFirst.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        Subtask subtaskThree = new Subtask("subtask3", "subtask3subtask3", manager.getNewId(), StatusTask.DONE, epicFirst.getId(), TypeTask.SUBTASK, "27.03.2025 12:00", 30);

        manager.addEpic(epicFirst);
        manager.addSubtask(subtaskOne);
        manager.addSubtask(subtaskTwo);
        manager.addSubtask(subtaskThree);

        assertEquals(StatusTask.DONE, epicFirst.getStatus());
    }

    @Test
    @DisplayName("Проверяет что при добавлении подзадач со статусами NEW и DONE у Эпика будет статус IN_PROGRESS")
    public void returnInProgressStatusEpicAllSubtasksStatusNewAndDone() {
        Epic epicFirst = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskOne = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epicFirst.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtaskTwo = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.DONE, epicFirst.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        Subtask subtaskThree = new Subtask("subtask3", "subtask3subtask3", manager.getNewId(), StatusTask.DONE, epicFirst.getId(), TypeTask.SUBTASK, "27.03.2025 12:00", 30);

        manager.addEpic(epicFirst);
        manager.addSubtask(subtaskOne);
        manager.addSubtask(subtaskTwo);
        manager.addSubtask(subtaskThree);

        assertEquals(StatusTask.IN_PROGRESS, epicFirst.getStatus());
    }

    @Test
    @DisplayName("Проверяет что при добавлении подзадач со статусом IN_PROGRESS у Эпика тоже будет статус IN_PROGRESS")
    public void returnInProgressStatusEpicAllSubtasksStatusInProgress() {
        Epic epicFirst = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskOne = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.IN_PROGRESS, epicFirst.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtaskTwo = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.IN_PROGRESS, epicFirst.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        Subtask subtaskThree = new Subtask("subtask3", "subtask3subtask3", manager.getNewId(), StatusTask.IN_PROGRESS, epicFirst.getId(), TypeTask.SUBTASK, "19.03.2025 12:00", 30);

        manager.addEpic(epicFirst);
        manager.addSubtask(subtaskOne);
        manager.addSubtask(subtaskTwo);
        manager.addSubtask(subtaskThree);

        assertEquals(StatusTask.IN_PROGRESS, epicFirst.getStatus());
    }

    @Test
    @DisplayName("Должен вернуть startTime у Пустого Эпика будет null")
    public void returnNullStartTimeEpicEmptySubtasks() {
        Epic epicFirst = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);

        manager.addEpic(epicFirst);

        assertNull(epicFirst.getStartTime());
    }

    @Test
    @DisplayName("Должен вернуть startTime у Эпика такой же как у его самой ранней его подзадачи")
    public void returnNotNullStartTimeEpic() {
        Epic epicFirst = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskOne = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(),
                StatusTask.IN_PROGRESS, epicFirst.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        manager.addEpic(epicFirst);
        manager.addSubtask(subtaskOne);

        assertNotNull(epicFirst.getStartTime());
        assertEquals(epicFirst.getStartTime(), subtaskOne.getStartTime());
        assertEquals("24.03.2025 12:00", epicFirst.getStartTimeToString());
    }

    @Test
    @DisplayName("Должен вернуть endTime у Пустого Эпика будет null")
    public void returnNullEndTimeEpicEmptySubtasks() {
        Epic epicFirst = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);

        manager.addEpic(epicFirst);

        assertNull(epicFirst.getEndTime());
    }

    @Test
    @DisplayName("Должен вернуть endTime у Эпика такой же как у самой поздней его подзадачи")
    public void returnNotNullEndTimeEpic() {
        Epic epicFirst = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskOne = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(),
                StatusTask.IN_PROGRESS, epicFirst.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        manager.addEpic(epicFirst);
        manager.addSubtask(subtaskOne);

        assertNotNull(epicFirst.getEndTime());
        assertEquals(epicFirst.getStartTime(), subtaskOne.getStartTime());
        assertEquals("24.03.2025 12:00", epicFirst.getStartTimeToString());
    }

    @Test
    @DisplayName("Должен вернуть duration 0 у Пустого эпика")
    public void returnDuration0EpicEmptySubtasks() {
        Epic epicFirst = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);

        manager.addEpic(epicFirst);

        assertEquals(0, epicFirst.getDurationToLong());
    }


    @Test
    @DisplayName("Должен вернуть duration 2 у Эпика, продолжительность = сумме продолжительности всех его подзадач")
    public void returnDuration1Epic() {
        Epic epicFirst = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskOne = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(),
                StatusTask.IN_PROGRESS, epicFirst.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtaskTwo = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(),
                StatusTask.IN_PROGRESS, epicFirst.getId(), TypeTask.SUBTASK, "25.03.2025 12:00", 1);

        manager.addEpic(epicFirst);
        manager.addSubtask(subtaskOne);
        manager.addSubtask(subtaskTwo);

        assertEquals(2, epicFirst.getDurationToLong());
        assertEquals(1, subtaskOne.getDurationToLong());
        assertEquals(1, subtaskTwo.getDurationToLong());
    }
}