package managerTest.abstractManagersTest;

import manager.enums.StatusTask;
import manager.enums.TypeTask;
import manager.interfaces.HistoryManager;
import manager.interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected TaskManager manager;
    Task doneTask;
    Epic firstEpic;
    Subtask subtaskNew;
    Subtask subtaskProgress;

    public abstract T createTaskManager();


    @BeforeEach
    public void setupManager() {
        manager = createTaskManager();
        doneTask = new Task("task", "task1task1", manager.getNewId(), StatusTask.DONE, TypeTask.TASK, "21.03.2025 12:00", 1);
        firstEpic = new Epic("epic1", "epic1epic1", manager.getNewId(), StatusTask.NEW, TypeTask.EPIC);
        subtaskNew = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, firstEpic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        subtaskProgress = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.IN_PROGRESS, firstEpic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
    }

    @Test
    @DisplayName("Каждый вызов manager.getNewId() возвращает новый ID для задач")
    public void getNewId_shouldReturnNewID() {
        int taskId = manager.getNewId();
        int subtaskId = manager.getNewId();
        int epicId = manager.getNewId();

        assertEquals(5, taskId);
        assertEquals(6, subtaskId);
        assertEquals(7, epicId);
    }

    @Test
    @DisplayName("Возвращает пустой список истории, когда задача не была запрошена")
    public void getHistory_shouldReturnEmptyHistory() {
        manager.addTask(doneTask);
        List<Task> history = manager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("Возвращает список истории, когда задачи были запрошены")
    public void getHistory_shouldReturnNotEmptyHistory() {
        manager.addTask(doneTask);
        manager.addEpic(firstEpic);
        manager.addSubtask(subtaskNew);

        manager.getTask(doneTask.getId());
        manager.getEpic(firstEpic.getId());
        manager.getSubtask(subtaskNew.getId());
        List<Task> history = manager.getHistory();


        assertEquals(3, history.size());
        assertTrue(history.contains(doneTask));
        assertTrue(history.contains(firstEpic));
        assertTrue(history.contains(subtaskNew));
    }

    @Test
    @DisplayName("Возвращает пустой список приоритетов ,когда задачи не были добавлены")
    public void getPrioritizedTasks_shouldReturnEmptyPrioritizedTasks() {
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertNotNull(prioritizedTasks);
        assertTrue(prioritizedTasks.isEmpty());
    }

    @Test
    @DisplayName("Возвращает список приоритетов не включительно Эпик,когда задачи были добавлены, а у Эпика есть подзадачи")
    public void getPrioritizedTasks_shouldReturnPrioritizedTasks_EpicNotEmpty() {
        manager.addTask(doneTask);
        manager.addEpic(firstEpic);
        manager.addSubtask(subtaskNew);
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(2, prioritizedTasks.size());
        assertTrue(prioritizedTasks.contains(doneTask));
        assertTrue(prioritizedTasks.contains(subtaskNew));
        assertFalse(prioritizedTasks.contains(firstEpic));//не возвращает эпик потому что есть подзадачи в нем
    }

    @Test
    @DisplayName("Возвращает список приоритетов включительно Эпик,когда задачи были добавлены ,а у Эпика нет подзадач")
    public void getPrioritizedTasks_shouldReturnPrioritizedTasks_EpicEmpty() {
        manager.addTask(doneTask);
        manager.addEpic(firstEpic);
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(2, prioritizedTasks.size());
        assertTrue(prioritizedTasks.contains(doneTask));
        assertTrue(prioritizedTasks.contains(firstEpic));//возвращает Эпик потому что нет подзадач в нем
        assertFalse(prioritizedTasks.contains(subtaskNew));
    }

    @Test
    @DisplayName("Возвращает все листы задач пустыми, когда задачи не были добавлены")
    public void getListTasksEpicsSubtasks_shouldReturnEmptyLists() {
        List<Task> listTasks = manager.getListTasks();
        List<Epic> listEpics = manager.getListEpics();
        List<Subtask> listSubtasks = manager.getListSubtasks();

        assertTrue(listTasks.isEmpty());
        assertTrue(listEpics.isEmpty());
        assertTrue(listSubtasks.isEmpty());
    }

    @Test
    @DisplayName("Возвращает все листы задач ,в которых по одному элементу")
    public void getListTasksEpicsSubtasks_shouldReturnNotEmptyLists() {
        manager.addTask(doneTask);
        manager.addEpic(firstEpic);
        manager.addSubtask(subtaskNew);

        List<Task> listTasks = manager.getListTasks();
        List<Epic> listEpics = manager.getListEpics();
        List<Subtask> listSubtasks = manager.getListSubtasks();

        assertTrue(listTasks.contains(doneTask));
        assertTrue(listEpics.contains(firstEpic));
        assertTrue(listSubtasks.contains(subtaskNew));
        assertEquals(1, listTasks.size());
        assertEquals(1, listEpics.size());
        assertEquals(1, listSubtasks.size());
    }

    @Test
    @DisplayName("Возвращает все Map-ы задач пустыми, когда задачи не были добавлены")
    public void getMapTasksEpicsSubtasks_shouldReturnEmptyMap() {
        Map<Integer, Task> taskMap = manager.getMapTasks();
        Map<Integer, Epic> epicMap = manager.getMapEpics();
        Map<Integer, Subtask> subtaskMap = manager.getMapSubtasks();

        assertTrue(taskMap.isEmpty());
        assertTrue(epicMap.isEmpty());
        assertTrue(subtaskMap.isEmpty());
    }

    @Test
    @DisplayName("Возвращает Map-у задач, когда добавлена одна задача")
    public void getMapTasks_shouldReturnNotEmptyMap() {
        manager.addTask(doneTask);
        Map<Integer, Task> taskMap = manager.getMapTasks();
        Task taskExtracted = taskMap.get(doneTask.getId());

        assertEquals(doneTask, taskExtracted);
        assertTrue(taskMap.containsKey(doneTask.getId()));
        assertEquals(1, taskMap.size());

    }

    @Test
    @DisplayName("Возвращает Map-у Epic-ов, когда добавлен один Epic")
    public void getMapEpics_shouldReturnNotEmptyMap() {
        manager.addEpic(firstEpic);

        Map<Integer, Epic> epicMap = manager.getMapEpics();
        Epic epicExtracted = epicMap.get(firstEpic.getId());

        assertEquals(firstEpic, epicExtracted);
        assertTrue(epicMap.containsKey(firstEpic.getId()));
        assertEquals(1, epicMap.size());
    }

    @Test
    @DisplayName("Возвращает Map-у подзадач, когда добавлена одна подзадача")
    public void getMapSubtasks_shouldReturnNotEmptyMap() {
        manager.addEpic(firstEpic);
        manager.addSubtask(subtaskNew);

        Map<Integer, Subtask> subtaskMap = manager.getMapSubtasks();
        Subtask extractedSubtask = subtaskMap.get(subtaskNew.getId());

        assertEquals(subtaskNew, extractedSubtask);
        assertTrue(subtaskMap.containsKey(subtaskNew.getId()));
        assertEquals(1, subtaskMap.size());
    }

    @Test
    @DisplayName("Проверяем как метод по удалению всех задач очищают Map-у ,при этом в ней были задачи")
    public void clearTasks() {
        manager.addTask(doneTask);

        Map<Integer, Task> taskMap = manager.getMapTasks();
        manager.clearTasks();

        assertTrue(taskMap.isEmpty());
    }

    @Test
    @DisplayName("Проверяем как метод по удалению всех Epic-ов очищают Map-у ,при этом в ней были Epic-и")
    public void clearEpics() {
        manager.addEpic(firstEpic);

        Map<Integer, Epic> epicMap = manager.getMapEpics();
        manager.clearEpics();

        assertTrue(epicMap.isEmpty());
    }

    @Test
    @DisplayName("Проверяем как метод по удалению всех подзадач очищают Map-у ,при этом в ней были подзадачи")
    public void clearSubtasks() {
        manager.addSubtask(subtaskNew);

        Map<Integer, Subtask> subtaskMap = manager.getMapSubtasks();
        manager.clearSubtasks();

        assertTrue(subtaskMap.isEmpty());
    }

    @Test
    @DisplayName("Возвращает пустой Optional, когда задача не была добавлена")
    public void getTasks_shouldReturnEmptyOptional_whenTaskDoesNotAdd() {
        Optional<Task> task = manager.getTask(doneTask.getId());

        assertTrue(task.isEmpty());
    }

    @Test
    @DisplayName("Возвращает пустой Optional, когда Epic не была добавлен")
    public void getEpic_shouldReturnEmptyOptional_whenEpicDoesNotAdd() {
        Optional<Epic> epic = manager.getEpic(firstEpic.getId());

        assertTrue(epic.isEmpty());
    }

    @Test
    @DisplayName("Возвращает пустой Optional, когда Подзадача не была добавлена")
    public void getSubtask_shouldReturnEmptyOptional_whenSubtaskDoesNotAdd() {
        Optional<Subtask> subtask = manager.getSubtask(subtaskNew.getId());

        assertTrue(subtask.isEmpty());
    }

    @Test
    @DisplayName("Возвращает задачу по ID, когда задача была добавлена")
    public void getTask_shouldReturnPresentOptional_whenTaskDoesAdd() {
        manager.addTask(doneTask);

        Task managerTask = manager.getTask(doneTask.getId()).get();

        assertEquals(doneTask, managerTask);
    }

    @Test
    @DisplayName("Возвращает Epic по ID, когда Epic был добавлен")
    public void getEpic_shouldReturnPresentOptional_whenEpicDoesAdd() {
        manager.addEpic(firstEpic);

        Epic managerEpic = manager.getEpic(firstEpic.getId()).get();

        assertEquals(firstEpic, managerEpic);
    }

    @Test
    @DisplayName("Возвращает подзадачу по ID, когда подзадача была добавлена")
    public void getSubtask_shouldReturnPresentOptional_whenSubtaskDoesAdd() {
        manager.addEpic(firstEpic);
        manager.addSubtask(subtaskNew);

        Subtask managerSubtask = manager.getSubtask(subtaskNew.getId()).get();

        assertEquals(subtaskNew, managerSubtask);
    }

    @Test
    @DisplayName("Проверяем что задачи добавляются и возвращает true")
    public void returnTrueAddTaskEpicSubtask_whenValidTaskDoesAdd() {
        boolean isAddTask = manager.addTask(doneTask);
        boolean isAddEpic = manager.addEpic(firstEpic);
        boolean isAddSubtask = manager.addSubtask(subtaskNew);

        assertTrue(isAddTask);
        assertTrue(isAddEpic);
        assertTrue(isAddSubtask);
    }

    @Test
    @DisplayName("Проверяем что не будут добавлены задачи с уже занятыми ID")
    public void returnFalseAddTaskEpicSubtask_whenIdAdded() {
        manager.addTask(doneTask);
        manager.addEpic(firstEpic);
        manager.addSubtask(subtaskNew);

        int idDoneTask = doneTask.getId();
        int idFirstEpic = firstEpic.getId();
        int idSubtaskNew = subtaskNew.getId();
        Task task2Get = new Task("task", "task1task1", idDoneTask, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        Epic epicGet = new Epic("epic1", "epic1epic1", idFirstEpic, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskGet = new Subtask("subtask1", "subtask1subtask1", idSubtaskNew, StatusTask.NEW, firstEpic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        boolean isAddTask = manager.addTask(task2Get);
        boolean isAddEpic = manager.addEpic(epicGet);
        boolean isAddSubtask = manager.addSubtask(subtaskGet);

        assertFalse(isAddTask);
        assertFalse(isAddEpic);
        assertFalse(isAddSubtask);
    }

    @Test
    @DisplayName("Подзадача не должны быть добавлена если ее Эпик не добавлен")
    public void addSubtask_returnFalseAddSubtask_whenAddToNotExistentEpic() {
        int idEpicNotExistent = 777;

        Subtask subtask = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.NEW, idEpicNotExistent, TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        boolean isAddSubtask = manager.addSubtask(subtask);

        assertFalse(isAddSubtask);
    }

    @Test
    @DisplayName("Здесь задача успешно обновиться потому что все условия соблюдены")
    public void updateTask_returnTrueUpdateTask_whenCorrectTaskDoesUpdate() {
        manager.addTask(doneTask);

        int idDoneTask = doneTask.getId();
        Task taskNew = new Task("taskUpdate", "taskUpdate", idDoneTask, StatusTask.NEW, TypeTask.TASK, "21.03.2025 12:00", 1);
        boolean isUpdateTask = manager.updateTask(taskNew);
        Task getUpdateTask = manager.getTask(idDoneTask).get();

        assertEquals(taskNew, getUpdateTask);
        assertTrue(isUpdateTask);
    }

    @Test
    @DisplayName("Не получиться обновить задачу, которая не добавлена")
    public void updateTask_returnFalseUpdateTask_whenNoCorrectTaskDoesUpdate() {
        boolean isUpdateTask = manager.updateTask(doneTask);

        assertFalse(isUpdateTask);
    }

    @Test
    @DisplayName("Успешное обновление подзадачи, когда она существует")
    public void updateSubtask_returnTrueUpdateSubtask_whenThereIsSubtaskByID() {
        manager.addEpic(firstEpic);
        manager.addSubtask(subtaskNew);

        int idSubtaskNew = subtaskNew.getId();
        Subtask subtaskDone = new Subtask("subtask1", "UpdateSubtask", idSubtaskNew, StatusTask.DONE, firstEpic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        boolean isUpdateSubtask = manager.updateSubtask(subtaskDone);
        Subtask getSubtask = manager.getSubtask(idSubtaskNew).get();

        assertEquals(subtaskDone, getSubtask);
        assertTrue(isUpdateSubtask);
    }

    @Test
    @DisplayName("Не получиться обновить подзадачу, которая не добавлена")
    public void updateSubtask_returnFalseUpdateSubtask_whenSubtasksByIDNotExist() {
        manager.addEpic(firstEpic);
        manager.addSubtask(subtaskNew);

        int notExistIDSubtaskNew = 789;
        Subtask subtaskNew = new Subtask("subtask1", "subtask1subtask1", notExistIDSubtaskNew, StatusTask.NEW, firstEpic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        boolean isUpdateSubtask = manager.updateSubtask(subtaskNew);

        assertFalse(isUpdateSubtask);
    }

    @Test
    @DisplayName("Не получиться обновить задачу, у которой Эпик не добавлен")
    public void updateSubtask_returnFalseOnlyUpdateSubtask_whenSubtaskNotHaveItsEpic() {
        manager.addEpic(firstEpic);
        manager.addSubtask(subtaskNew);

        int idEpicNotExistent = 777;
        int idSubtaskNew = subtaskNew.getId();
        Subtask subtaskDone = new Subtask("subtask1", "subtask1subtask1", idSubtaskNew, StatusTask.DONE, idEpicNotExistent, TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        boolean isUpdateSubtask = manager.updateSubtask(subtaskDone);

        assertFalse(isUpdateSubtask);
    }

    @Test
    @DisplayName("Успешно обновится Эпик, когда он добавлен")
    public void updateEpic_returnTrueUpdateEpic_whenEpicExist() {
        manager.addEpic(firstEpic);

        int idFirstEpic = firstEpic.getId();
        Epic epicDone = new Epic("epic1", "UpdateEpic", idFirstEpic, StatusTask.DONE, TypeTask.EPIC);
        boolean isUpdateEpic = manager.updateEpic(epicDone);
        Epic getUpdateEpic = manager.getEpic(idFirstEpic).get();

        assertTrue(isUpdateEpic);
        assertEquals(epicDone, getUpdateEpic);
    }

    @Test
    @DisplayName("Успешно обновляем пустой эпик, на новую версию с подзадачами")
    public void updateEpic_returnTrueUpdateEmptyEpic_WhenNewEpicWithSubtasks() {
        manager.addEpic(firstEpic);

        int idFirstEpic = firstEpic.getId();
        Epic epicNEW = new Epic("epic1", "epicUpdate", idFirstEpic, StatusTask.NEW, TypeTask.EPIC);
        Subtask subtaskDone = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.DONE, epicNEW.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtaskNew = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, epicNEW.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        epicNEW.addSubtask(subtaskDone);
        epicNEW.addSubtask(subtaskNew);
        boolean isUpdate = manager.updateEpic(epicNEW);
        Epic getUpdateEpic = manager.getEpic(idFirstEpic).get();

        assertTrue(isUpdate);
        assertEquals(epicNEW, getUpdateEpic);
    }

    @Test
    @DisplayName("Успешно обновляем Эпик с подзадачами, на новую версию без подзадач")
    public void returnTrueUpdateEpicWithSubtasks_WhenNewEpicEmptyEpic() {
        Subtask subtaskDone = new Subtask("subtask1", "subtask1subtask1", manager.getNewId(), StatusTask.DONE, firstEpic.getId(), TypeTask.SUBTASK, "24.03.2025 12:00", 1);
        Subtask subtaskNew = new Subtask("subtask2", "subtask2subtask2", manager.getNewId(), StatusTask.NEW, firstEpic.getId(), TypeTask.SUBTASK, "20.03.2025 12:00", 30);
        manager.addEpic(firstEpic);
        manager.addSubtask(subtaskNew);
        manager.addSubtask(subtaskDone);

        int idFirstEpic = firstEpic.getId();
        Epic epicNew = new Epic("epic1", "epicUpdate", idFirstEpic, StatusTask.NEW, TypeTask.EPIC);
        boolean isUpdate = manager.updateEpic(epicNew);
        Epic getUpdateEpic = manager.getEpic(idFirstEpic).get();
        HashMap<Integer, Subtask> getSubtasksEpic = epicNew.getSubtasksMap();

        assertTrue(isUpdate);
        assertEquals(epicNew, getUpdateEpic);
        assertTrue(getSubtasksEpic.isEmpty());
    }

    @Test
    @DisplayName("Не получиться обновить эпик, когда он не добавлен")
    public void updateEpic_returnFalseUpdateEpic_whenEpicIDNotExist() {
        boolean isUpdate = manager.updateEpic(firstEpic);

        assertFalse(isUpdate);
    }

    @Test
    @DisplayName("Успешно удалит задачу по ID, когда она добавлена")
    public void removeTaskById_returnTrueRemoveTaskById_WhenIdTaskExist() {
        boolean isAdd = manager.addTask(doneTask);
        boolean isRemove = manager.removeTaskById(doneTask.getId());

        assertTrue(isAdd);
        assertTrue(isRemove);
    }

    @Test
    @DisplayName("Не получиться удалить задачу по ID, если она не добавлена")
    public void removeTaskById_returnFalseRemoveTaskById_WhenIdTaskNotExist() {
        int IdNotExist = 875;
        boolean isRemove = manager.removeTaskById(IdNotExist);

        assertFalse(isRemove);
    }

    @Test
    @DisplayName("Успешно удалит Эпик по ID, когда он добавлен")
    public void removeEpicById_returnTrueRemoveEpicById_WhenIdEpicExist() {
        boolean isAdd = manager.addEpic(firstEpic);
        boolean isRemove = manager.removeEpicById(firstEpic.getId());

        assertTrue(isAdd);
        assertTrue(isRemove);
    }

    @Test
    @DisplayName("Успешно удаляем не пустой Эпик и заодно его подзадачи, когда добавлен эпик и его подзадачи")
    public void removeEpicById_returnTrueRemoveEpicById_WhenEpicNotEmpty() {
        boolean isAddEpic = manager.addEpic(firstEpic);
        boolean isAddFirstSubtask = manager.addSubtask(subtaskNew);
        boolean isAddSubtaskProgress = manager.addSubtask(subtaskProgress);

        boolean isRemoveFirstEpic = manager.removeEpicById(firstEpic.getId());
        HashMap<Integer, Task> getTasksAfter = manager.getMapTasks();
        HashMap<Integer, Epic> getEpicsAfter = manager.getMapEpics();
        HashMap<Integer, Subtask> getSubtasksAfter = manager.getMapSubtasks();


        assertTrue(isAddEpic && isAddFirstSubtask && isAddSubtaskProgress);
        assertTrue(isRemoveFirstEpic);
        assertTrue(getTasksAfter.isEmpty());
        assertTrue(getEpicsAfter.isEmpty());
        assertTrue(getSubtasksAfter.isEmpty());
        assertFalse(getEpicsAfter.containsKey(firstEpic.getId()));
        assertFalse(getSubtasksAfter.containsKey(subtaskNew.getId()));
        assertFalse(getSubtasksAfter.containsKey(subtaskProgress.getId()));
    }

    @Test
    @DisplayName("Не получиться удалить Эпик по ID, когда он не добавлен")
    public void removeEpicById_returnFalseRemoveEpicById_WhenIdEpicNotExist() {
        int idNotExist = 787;
        boolean isRemove = manager.removeEpicById(idNotExist);

        assertFalse(isRemove);
    }

    @Test
    @DisplayName("Успешно удаляем добавленную подзадачу")
    public void removeSubtaskById_returnTrueRemoveSubtaskById_WhenIdSubtaskExist() {
        boolean isAddFirstEpic = manager.addEpic(firstEpic);
        boolean isAddSubtaskNew = manager.addSubtask(subtaskNew);

        boolean isRemove = manager.removeSubtaskById(subtaskNew.getId());
        HashMap<Integer, Subtask> getSubtaskEpic = firstEpic.getSubtasksMap();
        HashMap<Integer, Subtask> getSubtasks = manager.getMapSubtasks();

        assertTrue(isAddFirstEpic);
        assertTrue(isAddSubtaskNew);
        assertTrue(isRemove);
        assertFalse(getSubtasks.containsKey(subtaskNew.getId()));
        assertFalse(getSubtaskEpic.containsKey(subtaskNew.getId()));
    }

    @Test
    @DisplayName("Должен вернуть пустую Map-у, когда запрашиваем по не добавленному ID эпика его подзадачи")
    public void getMapSubtasksByEpicId_getEmptyMapSubtasksByEpicId_WhenEpicNotExit() {
        int idNotExist = 787;
        Map<Integer, Subtask> mapSubtasksByEpicId = manager.getMapSubtasksByEpicId(idNotExist);

        assertTrue(mapSubtasksByEpicId.isEmpty());
    }

    @Test
    @DisplayName("Возвращаем не пустую Map-у определенного эпика по ID, когда и Эпик и его подзадачи добавлены")
    public void getMapSubtasksByEpicId_getNotEmptyMapSubtasksByEpicId_WhenEpicExit() {
        boolean isAddFirstEpic = manager.addEpic(firstEpic);
        boolean isAddSubtaskNew = manager.addSubtask(subtaskNew);
        boolean isAddSubtaskProgress = manager.addSubtask(subtaskProgress);

        int idExist = firstEpic.getId();
        Map<Integer, Subtask> mapSubtasksByEpicId = manager.getMapSubtasksByEpicId(idExist);

        assertTrue(isAddFirstEpic);
        assertTrue(isAddSubtaskNew);
        assertTrue(isAddSubtaskProgress);
        assertFalse(mapSubtasksByEpicId.isEmpty());
        assertTrue(mapSubtasksByEpicId.containsKey(subtaskNew.getId()));
        assertTrue(mapSubtasksByEpicId.containsKey(subtaskProgress.getId()));
    }

    @Test
    @DisplayName("Возвращает пустую Map-у подзадач определенного Эпика по ID, когда добавлен пустой Эпик")
    public void getMapSubtasksByEpicId_getEmptyMapSubtasksByEpicId_WhenEpicEmpty() {
        boolean isAddFirstEpic = manager.addEpic(firstEpic);

        int idExist = firstEpic.getId();
        Map<Integer, Subtask> mapSubtasksByEpicId = manager.getMapSubtasksByEpicId(idExist);

        assertTrue(isAddFirstEpic);
        assertTrue(mapSubtasksByEpicId.isEmpty());
    }

    @Test
    @DisplayName("Возвращает объект экземпляра класса HistoryManager")
    public void getHistoryManager_returnHistoryManager() {
        HistoryManager historyManager = manager.getHistoryManager();

        boolean notNull = Objects.nonNull(historyManager);
        boolean isHistoryManager = historyManager instanceof HistoryManager;

        assertTrue(notNull);
        assertTrue(isHistoryManager);
    }

    @Test
    @DisplayName("Должен вернуть пустой лист в котором должны были быть все задачи, Эпики и подзадачи, когда ничего не добавлено")
    public void getAllTasksEpicSubtask_getEmptyListAllTasksEpicsSubtasks_WhenNotAddTaskEpicSubtask() {
        List<Task> listAllTaskEpicsSubtasks = manager.getAllTasksEpicSubtask();

        assertTrue(listAllTaskEpicsSubtasks.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть лист в котором должны быть все задачи, Эпики и подзадачи, когда добавлены задачи, Эпики и подзадачи")
    public void getAllTasksEpicSubtask_getNotEmptyListAllTasksEpicsSubtasks_WhenEachOneAddTaskEpicSubtask() {
        manager.addTask(doneTask);
        manager.addEpic(firstEpic);
        manager.addSubtask(subtaskNew);

        List<Task> listAllTaskEpicsSubtasks = manager.getAllTasksEpicSubtask();

        assertFalse(listAllTaskEpicsSubtasks.isEmpty());
        assertTrue(listAllTaskEpicsSubtasks.contains(doneTask));
        assertTrue(listAllTaskEpicsSubtasks.contains(firstEpic));
        assertTrue(listAllTaskEpicsSubtasks.contains(subtaskNew));
    }
}