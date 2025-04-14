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

import java.util.HashMap;

class EpicTest {

    TaskManager manager;

    @BeforeEach
    public void setManager() {
        manager = Managers.getDefault();
    }

    @Test
    public void return0SubtasksEmpty() {
        Epic epic = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);

        manager.addEpic(epic);
        HashMap<Integer, Subtask> subtaskHashMap = epic.getSubtasksMap();

        Assertions.assertEquals(0, subtaskHashMap.size());
    }

    @Test
    public void returnNewStatusEpicAllSubtasksStatusNew() {
        Epic epic = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        Subtask subtask3 = new Subtask("subtask3", "subtask3subtask3", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "27.03.2025 12:00", 30);

        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        Assertions.assertEquals(StatusTask.NEW, epic.getStatus());
    }

    @Test
    public void returnDoneStatusEpicAllSubtasksStatusDone() {
        Epic epic = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.DONE, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.DONE, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        Subtask subtask3 = new Subtask("subtask3", "subtask3subtask3", manager.getNewId(), StatusTask.DONE, epic.getId(), TypeTask.SUBTASK, "27.03.2025 12:00", 30);

        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        Assertions.assertEquals(StatusTask.DONE, epic.getStatus());
    }

    @Test
    public void returnInProgressStatusEpicAllSubtasksStatusNewAndDone() {
        Epic epic = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.DONE, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        Subtask subtask3 = new Subtask("subtask3", "subtask3subtask3", manager.getNewId(), StatusTask.DONE, epic.getId(), TypeTask.SUBTASK, "27.03.2025 12:00", 30);

        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        Assertions.assertEquals(StatusTask.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void returnInProgressStatusEpicAllSubtasksStatusInProgress() {
        Epic epic = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.IN_PROGRESS, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.IN_PROGRESS, epic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        Subtask subtask3 = new Subtask("subtask3", "subtask3subtask3", manager.getNewId(), StatusTask.IN_PROGRESS, epic.getId(), TypeTask.SUBTASK, "19.03.2025 12:00", 30);

        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        Assertions.assertEquals(StatusTask.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void returnNullStartTimeEpicEmptySubtasks() {
        Epic epic = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);

        manager.addEpic(epic);

        Assertions.assertNull(epic.getStartTime());
    }

    @Test
    public void returnNotNullStartTimeEpic() {
        Epic epic = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(),
                StatusTask.IN_PROGRESS, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        manager.addEpic(epic);
        manager.addSubtask(subtask1);

        Assertions.assertNotNull(epic.getStartTime());
        Assertions.assertEquals(epic.getStartTime(), subtask1.getStartTime());
        Assertions.assertEquals("24.03.2025 12:00", epic.getStartTimeToString());
    }

    @Test
    public void returnNullEndTimeEpicEmptySubtasks() {
        Epic epic = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);

        manager.addEpic(epic);

        Assertions.assertNull(epic.getEndTime());
    }

    @Test
    public void returnNotNullEndTimeEpic() {
        Epic epic = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(),
                StatusTask.IN_PROGRESS, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        manager.addEpic(epic);
        manager.addSubtask(subtask1);

        Assertions.assertNotNull(epic.getEndTime());
        Assertions.assertEquals(epic.getStartTime(), subtask1.getStartTime());
        Assertions.assertEquals("24.03.2025 12:00", epic.getStartTimeToString());
    }

    @Test
    public void returnDuration0EpicEmptySubtasks() {
        Epic epic = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);

        manager.addEpic(epic);

        Assertions.assertEquals(0, epic.getDurationToLong());
    }


    @Test
    public void returnDuration1Epic() {
        Epic epic = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(),
                StatusTask.IN_PROGRESS, epic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        manager.addEpic(epic);
        manager.addSubtask(subtask1);

        Assertions.assertEquals( subtask1.getDurationToLong(), epic.getDurationToLong());
        Assertions.assertEquals(1, epic.getDurationToLong());
        Assertions.assertEquals(1, subtask1.getDurationToLong());
    }
}