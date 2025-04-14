package managerTest.abstractManagersTest;

import manager.enums.StatusTask;
import manager.enums.TypeTask;
import manager.interfaces.HistoryManager;
import manager.interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected TaskManager manager = createTaskManager();

    public abstract T createTaskManager();

    @BeforeEach
    public void setupManager() {
        manager = createTaskManager();
    }

    @Test
    public void get1And2And3IntTestNewID() {
        int taskId = manager.getNewId();
        assertEquals(1, taskId);

        int subtaskId = manager.getNewId();
        assertEquals(2, subtaskId);

        int epicId = manager.getNewId();
        assertEquals(3, epicId);
    }

    @Test
    public void getListEmptyHistory() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        manager.addTask(task1);

        List<Task> history = manager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    public void get3ListSizeHistory() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);


        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask1.getId());
        List<Task> history = manager.getHistory();


        assertEquals(3, history.size());

        assertTrue(history.contains(task1));
        assertTrue(history.contains(epic1));
        assertTrue(history.contains(subtask1));
    }

    @Test
    public void notGetTasksThatWereNotRequestedHistory() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);


        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        manager.getTask(task1.getId());
        List<Task> history = manager.getHistory();

        assertEquals(1, history.size());
        assertTrue(history.contains(task1));

        assertFalse(history.contains(epic1));
        assertFalse(history.contains(subtask1));
    }

    @Test
    public void getListEmptyPrioritizedTasks_WhenNoTasksAdd() {
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertNotNull(prioritizedTasks);
        assertTrue(prioritizedTasks.isEmpty());
    }

    @Test
    public void getPrioritizedTasksSize3() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);


        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(2, prioritizedTasks.size());
        assertTrue(prioritizedTasks.contains(task1));
        assertTrue(prioritizedTasks.contains(subtask1));

        assertFalse(prioritizedTasks.contains(epic1));
    }

    @Test
    public void returnTwoTaskInPrioritiesThatWasAdded() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        manager.addTask(task1);
        manager.addEpic(epic1);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(2, prioritizedTasks.size());
        assertTrue(prioritizedTasks.contains(task1));
        assertTrue(prioritizedTasks.contains(epic1));//возвращает Эпик потому что нет подзадач в нем

        assertFalse(prioritizedTasks.contains(subtask1));
    }

    @Test
    public void getListTasksEpicsSubtasksSizeEach0() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);


        List<Task> listTasks = manager.getListTasks();
        List<Epic> listEpics = manager.getListEpics();
        List<Subtask> listSubtasks = manager.getListSubtasks();

        assertTrue(listTasks.isEmpty());
        assertTrue(listEpics.isEmpty());
        assertTrue(listSubtasks.isEmpty());
    }

    @Test
    public void getListTasksEpicsSubtasksSizeEach1() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        List<Task> listTasks = manager.getListTasks();
        List<Epic> listEpics = manager.getListEpics();
        List<Subtask> listSubtasks = manager.getListSubtasks();

        assertTrue(listTasks.contains(task1));
        assertTrue(listEpics.contains(epic1));
        assertTrue(listSubtasks.contains(subtask1));

        assertEquals(1, listTasks.size());
        assertEquals(1, listEpics.size());
        assertEquals(1, listSubtasks.size());
    }

    @Test
    public void getMapTasksEpicsSubtasksSizeEach0() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        Map<Integer, Task> taskMap = manager.getMapTasks();
        Map<Integer, Epic> epicMap = manager.getMapEpics();
        Map<Integer, Subtask> subtaskMap = manager.getMapSubtasks();

        assertTrue(taskMap.isEmpty());
        assertTrue(epicMap.isEmpty());
        assertTrue(subtaskMap.isEmpty());
    }

    @Test
    public void getMapTasksEpicsSubtasksSizeEach1() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        Map<Integer, Task> taskMap = manager.getMapTasks();
        Map<Integer, Epic> epicMap = manager.getMapEpics();
        Map<Integer, Subtask> subtaskMap = manager.getMapSubtasks();

        Task taskExtracted = taskMap.get(task1.getId());
        Epic epicExtracted = epicMap.get(epic1.getId());
        Subtask extractedSubtask = subtaskMap.get(subtask1.getId());

        assertEquals(task1, taskExtracted);
        assertEquals(epic1, epicExtracted);
        assertEquals(subtask1, extractedSubtask);

        assertTrue(taskMap.containsKey(task1.getId()));
        assertTrue(epicMap.containsKey(epic1.getId()));
        assertTrue(subtaskMap.containsKey(subtask1.getId()));

        assertEquals(1, taskMap.size());
        assertEquals(1, epicMap.size());
        assertEquals(1, subtaskMap.size());
    }

    @Test
    public void clearAllMapsAfterAddTasksEpicsAndSubtasks() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        Map<Integer, Task> taskMap = manager.getMapTasks();
        Map<Integer, Epic> epicMap = manager.getMapEpics();
        Map<Integer, Subtask> subtaskMap = manager.getMapSubtasks();

        assertFalse(taskMap.isEmpty());
        assertFalse(epicMap.isEmpty());
        assertFalse(subtaskMap.isEmpty());

        manager.clearTasks();
        manager.clearEpics();
        manager.clearSubtasks();

        assertTrue(taskMap.isEmpty());
        assertTrue(epicMap.isEmpty());
        assertTrue(subtaskMap.isEmpty());
    }

    @Test
    public void getTasksById_shouldReturnEmptyOptional_whenTaskDoesNotAdd() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        Optional<Task> taskOptional = manager.getTask(task1.getId());
        Optional<Epic> epicOptional = manager.getEpic(epic1.getId());
        Optional<Subtask> subtaskOptional = manager.getSubtask(subtask1.getId());

        assertTrue(taskOptional.isEmpty());
        assertTrue(epicOptional.isEmpty());
        assertTrue(subtaskOptional.isEmpty());
    }

    @Test
    public void getTasksById_shouldReturnPresentOptional_whenTaskDoesAdd() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        Optional<Task> taskOptional = manager.getTask(task1.getId());
        Optional<Epic> epicOptional = manager.getEpic(epic1.getId());
        Optional<Subtask> subtaskOptional = manager.getSubtask(subtask1.getId());

        assertTrue(taskOptional.isPresent());
        assertTrue(epicOptional.isPresent());
        assertTrue(subtaskOptional.isPresent());
    }

    @Test
    public void returnTrueAddTaskEpicSubtask_whenRightTaskDoesAdd() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        boolean isAddTask = manager.addTask(task1);
        boolean isAddEpic = manager.addEpic(epic1);
        boolean isAddSubtask = manager.addSubtask(subtask1);

        assertTrue(isAddTask);
        assertTrue(isAddEpic);
        assertTrue(isAddSubtask);
    }

    @Test
    public void returnFalseAddTaskEpicSubtask_whenIDBusyTaskDoesAdd() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        int idTask1 = task1.getId();
        int idEpic1 = epic1.getId();
        int idSubtask1 = subtask1.getId();

        Task task2 = new Task("task", "task1task1", idTask1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic2 = new Epic("epic1", "epic1epic1", idEpic1, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask2 = new Subtask("subtask1", "subtask1subtask1", idSubtask1, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        boolean isAddTask = manager.addTask(task2);
        boolean isAddEpic = manager.addEpic(epic2);
        boolean isAddSubtask = manager.addSubtask(subtask2);

        assertFalse(isAddTask);
        assertFalse(isAddEpic);
        assertFalse(isAddSubtask);
    }

    @Test
    public void returnFalseAddSubtask_whenAddToNotExistentEpic() {
        int idEpicNotExistent = 777;
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, idEpicNotExistent, TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        boolean isAddSubtask = manager.addSubtask(subtask1);
        assertFalse(isAddSubtask);
    }

    @Test
    public void returnTrueUpdateTask_whenCorrectTaskDoesUpdate() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        manager.addTask(task1);

        int idTask1 = task1.getId();
        Task task2 = new Task("taskUpdate", "taskUpdate", idTask1, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);

        boolean isUpdateTask = manager.updateTask(task2);
        assertTrue(isUpdateTask);

        Optional<Task> getOptionalTask = manager.getTask(idTask1);
        assertTrue(getOptionalTask.isPresent());

        Task getUpdateTask = getOptionalTask.get();
        assertEquals(task2, getUpdateTask);
    }

    @Test
    public void returnFalseUpdateTask_whenNoCorrectTaskDoesUpdate() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        boolean isUpdateTask = manager.updateTask(task1);

        assertFalse(isUpdateTask);
    }

    @Test
    public void returnTrueOnlyUpdateSubtask_whenThereIsSubtaskByIDAndItsEpic() {
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        int idSubtask1 = subtask1.getId();
        Subtask subtask2 = new Subtask("subtask1", "UpdateSubtask", idSubtask1, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        boolean isUpdateSubtask = manager.updateSubtask(subtask2);
        assertTrue(isUpdateSubtask);

        Optional<Subtask> getOptionalSubtask = manager.getSubtask(idSubtask1);
        assertTrue(getOptionalSubtask.isPresent());

        Subtask getSubtask = getOptionalSubtask.get();
        assertEquals(subtask2, getSubtask);
    }

    @Test
    public void returnFalseOnlyUpdateSubtask_whenSubtasksByIDNotExist() {
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        int notExistIDSubtask2 = 789;
        Subtask subtask2 = new Subtask("subtask1", "subtask1subtask1", notExistIDSubtask2, StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        boolean isUpdateSubtask = manager.updateSubtask(subtask2);
        assertFalse(isUpdateSubtask);
    }

    @Test
    public void returnFalseOnlyUpdateSubtask_whenSubtaskNotHaveItsEpic() {
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        int idEpicNotExistent = 777;
        int idSubtask1 = subtask1.getId();
        Subtask subtask2 = new Subtask("subtask1", "subtask1subtask1", idSubtask1, StatusTask.NEW, idEpicNotExistent, TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        boolean isUpdateSubtask = manager.updateSubtask(subtask2);
        assertFalse(isUpdateSubtask);
    }

    @Test
    public void returnTrueUpdateEpic_whenEpicExist() {
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        manager.addEpic(epic1);

        int idEpic1 = epic1.getId();
        Epic epic2 = new Epic("epic1", "UpdateEpic", idEpic1, StatusTask.DONE, TypeTask.EPIC);

        boolean isUpdateEpic = manager.updateEpic(epic2);
        assertTrue(isUpdateEpic);

        Optional<Epic> optionalGetUpdateEpic = manager.getEpic(idEpic1);
        assertTrue(optionalGetUpdateEpic.isPresent());

        Epic getUpdateEpic = optionalGetUpdateEpic.get();
        assertEquals(epic2, getUpdateEpic);
    }

    @Test
    public void returnTrueUpdateEmptyEpic_WhenNewEpicWithSubtasks() {
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        manager.addEpic(epic1);
        HashMap<Integer, Subtask> getSubtasksEpic1 = epic1.getSubtasksMap();
        assertTrue(getSubtasksEpic1.isEmpty());


        int idEpic1 = epic1.getId();
        Epic epic2 = new Epic("epic1", "epicUpdate", idEpic1, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic2.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic2.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        epic2.addSubtask(subtask1);
        epic2.addSubtask(subtask2);
        HashMap<Integer, Subtask> getSubtasksEpic2 = epic2.getSubtasksMap();
        assertFalse(getSubtasksEpic2.isEmpty());

        boolean isUpdate = manager.updateEpic(epic2);
        assertTrue(isUpdate);

        Optional<Epic> optionalGetUpdateEpic = manager.getEpic(idEpic1);
        assertTrue(optionalGetUpdateEpic.isPresent());

        Epic getUpdateEpic = optionalGetUpdateEpic.get();
        assertEquals(epic2, getUpdateEpic);
    }

    @Test
    public void returnTrueUpdateEpicWithSubtasks_WhenNewEpicEmptyEpic() {
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.DONE, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        HashMap<Integer, Subtask> getSubtasksEpic1 = epic1.getSubtasksMap();
        assertFalse(getSubtasksEpic1.isEmpty());

        int idEpic1 = epic1.getId();
        Epic epic2 = new Epic("epic1", "epicUpdate", idEpic1, StatusTask.NEW, TypeTask.EPIC);
        HashMap<Integer, Subtask> getSubtasksEpic2 = epic2.getSubtasksMap();
        assertTrue(getSubtasksEpic2.isEmpty());

        boolean isUpdate = manager.updateEpic(epic2);
        assertTrue(isUpdate);

        Optional<Epic> optionalGetUpdateEpic = manager.getEpic(idEpic1);
        assertTrue(optionalGetUpdateEpic.isPresent());

        Epic getUpdateEpic = optionalGetUpdateEpic.get();
        assertEquals(epic2, getUpdateEpic);
    }

    @Test
    public void returnFalseUpdateEpic_whenEpicIDNotExist() {
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        boolean isUpdate = manager.updateEpic(epic1);

        assertFalse(isUpdate);
    }

    @Test
    public void returnTrueRemoveTaskById_WhenIdTaskExist() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        boolean isAdd = manager.addTask(task1);
        assertTrue(isAdd);

        boolean isRemove = manager.removeTaskById(task1.getId());
        assertTrue(isRemove);
    }

    @Test
    public void returnTrueRemoveTaskById_WhenIdTaskNotExist() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        boolean isAdd = manager.addTask(task1);
        assertTrue(isAdd);

        int IdNotExist = 875;
        boolean isRemove = manager.removeTaskById(IdNotExist);
        assertFalse(isRemove);
    }

    @Test
    public void returnTrueRemoveEpicById_WhenIdEpicExist() {
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        boolean isAdd = manager.addEpic(epic1);
        assertTrue(isAdd);

        boolean isRemove = manager.removeEpicById(epic1.getId());
        assertTrue(isRemove);
    }

    @Test
    public void returnTrueRemoveEpicById_WhenEpicNotEmpty() {
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        boolean isAddEpic = manager.addEpic(epic1);
        boolean isAddSubtask1 = manager.addSubtask(subtask1);
        boolean isAddSubtask2 = manager.addSubtask(subtask2);
        assertTrue(isAddEpic && isAddSubtask1 && isAddSubtask2);

        HashMap<Integer, Subtask> getSubtaskEpic = epic1.getSubtasksMap();
        assertTrue(getSubtaskEpic.containsKey(subtask1.getId()));
        assertTrue(getSubtaskEpic.containsKey(subtask2.getId()));
        assertEquals(2, getSubtaskEpic.size());

        HashMap<Integer, Task> getTasks = manager.getMapTasks();
        HashMap<Integer, Epic> getEpics = manager.getMapEpics();
        HashMap<Integer, Subtask> getSubtasks = manager.getMapSubtasks();

        assertTrue(getTasks.isEmpty());

        assertFalse(getEpics.isEmpty());
        assertFalse(getSubtasks.isEmpty());

        assertTrue(getEpics.containsKey(epic1.getId()));
        assertTrue(getSubtasks.containsKey(subtask1.getId()));
        assertTrue(getSubtasks.containsKey(subtask2.getId()));


        boolean isRemoveEpic1 = manager.removeEpicById(epic1.getId());
        assertTrue(isRemoveEpic1);

        HashMap<Integer, Task> getTasks2 = manager.getMapTasks();
        HashMap<Integer, Epic> getEpics2 = manager.getMapEpics();
        HashMap<Integer, Subtask> getSubtasks2 = manager.getMapSubtasks();

        assertTrue(getTasks2.isEmpty());

        assertTrue(getEpics2.isEmpty());
        assertTrue(getSubtasks2.isEmpty());

        assertFalse(getEpics2.containsKey(epic1.getId()));
        assertFalse(getSubtasks2.containsKey(subtask1.getId()));
        assertFalse(getSubtasks2.containsKey(subtask2.getId()));
    }

    @Test
    public void returnFalseRemoveEmptyEpicById_WhenIdEpicNotExist() {
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        boolean isAdd = manager.addEpic(epic1);
        assertTrue(isAdd);

        int idNotExist = 787;
        boolean isRemove = manager.removeEpicById(idNotExist);
        assertFalse(isRemove);

        HashMap<Integer, Epic> getEpics = manager.getMapEpics();
        assertTrue(getEpics.containsKey(epic1.getId()));
        assertFalse(getEpics.containsKey(idNotExist));
    }

    @Test
    public void returnFalseRemoveNotEmptyEpicById_WhenIdEpicNotExist() {
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        boolean isAddEpic1 = manager.addEpic(epic1);
        boolean isAddSubtask1 = manager.addSubtask(subtask1);
        boolean isAddSubtask2 = manager.addSubtask(subtask2);
        assertTrue(isAddEpic1);
        assertTrue(isAddSubtask1);
        assertTrue(isAddSubtask2);

        int idNotExist = 787;
        boolean isRemove = manager.removeEpicById(idNotExist);
        assertFalse(isRemove);

        HashMap<Integer, Epic> getEpics = manager.getMapEpics();
        HashMap<Integer, Subtask> getSubtasks = manager.getMapSubtasks();

        assertTrue(getEpics.containsKey(epic1.getId()));
        assertFalse(getEpics.containsKey(idNotExist));

        assertTrue(getSubtasks.containsKey(subtask1.getId()));
        assertTrue(getSubtasks.containsKey(subtask2.getId()));
    }

    @Test
    public void returnTrueRemoveSubtaskById_WhenIdSubtaskExist() {
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        boolean isAddEpic1 = manager.addEpic(epic1);
        boolean isAddSubtask1 = manager.addSubtask(subtask1);
        boolean isAddSubtask2 = manager.addSubtask(subtask2);
        assertTrue(isAddEpic1);
        assertTrue(isAddSubtask1);
        assertTrue(isAddSubtask2);

        boolean isRemove = manager.removeSubtaskById(subtask1.getId());
        assertTrue(isRemove);

        HashMap<Integer, Epic> getEpics = manager.getMapEpics();
        HashMap<Integer, Subtask> getSubtasks = manager.getMapSubtasks();
        assertTrue(getEpics.containsKey(epic1.getId()));
        assertTrue(getSubtasks.containsKey(subtask2.getId()));

        assertFalse(getSubtasks.containsKey(subtask1.getId()));

        HashMap<Integer, Subtask> getSubtaskEpic = epic1.getSubtasksMap();
        assertTrue(getSubtaskEpic.containsKey(subtask2.getId()));

        assertFalse(getSubtaskEpic.containsKey(subtask1.getId()));
    }

    @Test
    public void returnFalseRemoveSubtaskById_WhenIdSubtaskNotExist() {
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        boolean isAddEpic1 = manager.addEpic(epic1);
        boolean isAddSubtask1 = manager.addSubtask(subtask1);
        boolean isAddSubtask2 = manager.addSubtask(subtask2);
        assertTrue(isAddEpic1);
        assertTrue(isAddSubtask1);
        assertTrue(isAddSubtask2);

        int idNotExist = 787;
        boolean isRemove = manager.removeSubtaskById(idNotExist);
        assertFalse(isRemove);

        HashMap<Integer, Epic> getEpics = manager.getMapEpics();
        HashMap<Integer, Subtask> getSubtasks = manager.getMapSubtasks();
        assertTrue(getEpics.containsKey(epic1.getId()));
        assertTrue(getSubtasks.containsKey(subtask1.getId()));
        assertTrue(getSubtasks.containsKey(subtask2.getId()));

        HashMap<Integer, Subtask> getSubtaskEpic = epic1.getSubtasksMap();
        assertTrue(getSubtaskEpic.containsKey(subtask1.getId()));
        assertTrue(getSubtaskEpic.containsKey(subtask2.getId()));
    }

    @Test
    public void getEmptyMapSubtasksByEpicId_WhenEpicNotExit() {
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        boolean isAddEpic1 = manager.addEpic(epic1);
        boolean isAddSubtask1 = manager.addSubtask(subtask1);
        boolean isAddSubtask2 = manager.addSubtask(subtask2);
        assertTrue(isAddEpic1);
        assertTrue(isAddSubtask1);
        assertTrue(isAddSubtask2);

        int idNotExist = 787;
        Map<Integer, Subtask> mapSubtasksByEpicId = manager.getMapSubtasksByEpicId(idNotExist);
        assertTrue(mapSubtasksByEpicId.isEmpty());
    }

    @Test
    public void getNotEmptyMapSubtasksByEpicId_WhenEpicExit() {
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        boolean isAddEpic1 = manager.addEpic(epic1);
        boolean isAddSubtask1 = manager.addSubtask(subtask1);
        boolean isAddSubtask2 = manager.addSubtask(subtask2);
        assertTrue(isAddEpic1);
        assertTrue(isAddSubtask1);
        assertTrue(isAddSubtask2);

        int idExist = epic1.getId();
        Map<Integer, Subtask> mapSubtasksByEpicId = manager.getMapSubtasksByEpicId(idExist);

        assertFalse(mapSubtasksByEpicId.isEmpty());

        assertTrue(mapSubtasksByEpicId.containsKey(subtask1.getId()));
        assertTrue(mapSubtasksByEpicId.containsKey(subtask2.getId()));
    }

    @Test
    public void getEmptyMapSubtasksByEpicId_WhenEpicEmpty() {
        Epic epic2 = new Epic("epic2", "epic2", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic2.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epic2.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        boolean isAddEpic2 = manager.addEpic(epic2);
        boolean isAddSubtask1 = manager.addSubtask(subtask1);
        boolean isAddSubtask2 = manager.addSubtask(subtask2);
        boolean isAddEpic1 = manager.addEpic(epic1);
        assertTrue(isAddEpic2 && isAddSubtask1 && isAddSubtask2 && isAddEpic1);

        int idExist = epic1.getId();
        Map<Integer, Subtask> mapSubtasksByEpicId = manager.getMapSubtasksByEpicId(idExist);
        assertTrue(mapSubtasksByEpicId.isEmpty());
    }

    @Test
    public void getHistoryManager() {
        HistoryManager historyManager = manager.getHistoryManager();
        boolean notNull = Objects.nonNull(historyManager);
        assertTrue(notNull);

        boolean isHistoryManager = historyManager instanceof HistoryManager;
        assertTrue(isHistoryManager);
    }

    @Test
    public void getEmptyListAllTasksEpicsSubtasks_WhenNotAddTaskEpicSubtask() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);

        List<Task> listAllTaskEpicsSubtasks = manager.getAllTasksEpicSubtask();
        assertTrue(listAllTaskEpicsSubtasks.isEmpty());
    }

    @Test
    public void getListAllTasksEpicsSubtasks_WhenEachOneAddTaskEpicSubtask() {
        Task task1 = new Task("task", "task1task1", manager.getNewId(), StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epic1 = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        Subtask subtask1 = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, epic1.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        List<Task> listAllTaskEpicsSubtasks = manager.getAllTasksEpicSubtask();
        assertFalse(listAllTaskEpicsSubtasks.isEmpty());

        assertTrue(listAllTaskEpicsSubtasks.contains(task1));
        assertTrue(listAllTaskEpicsSubtasks.contains(epic1));
        assertTrue(listAllTaskEpicsSubtasks.contains(subtask1));
    }
}