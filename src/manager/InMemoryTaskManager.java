package manager;

import manager.enums.StatusTask;
import manager.interfaces.HistoryManager;
import manager.interfaces.PrioritizedManager;
import manager.interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>(); //Хранит задачи.
    private final HashMap<Integer, Epic> epics = new HashMap<>();//Хранит Epic.
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();//Хранит подзадачи.
    private final PrioritizedManager prioritizedManager = Managers.getDefaultPrioritizedManager();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int countId = 0;


    @Override
    public int getNewId() {//Генерирует уникальный ID.
        countId++;
        return countId;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getListHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedManager.getPrioritizedTasks();
    }

    @Override
    public List<Task> getListTasks() { //Получение списка всех задач
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getListEpics() { //Получение списка всех Epic
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getListSubtasks() { //Получение списка всех подзадач
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public HashMap<Integer, Task> getMapTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getMapEpics() {
        return epics;
    }

    @Override
    public HashMap<Integer, Subtask> getMapSubtasks() {
        return subtasks;
    }

    @Override
    public boolean clearTasks() { //Удаление всех задач.
        boolean isNotEmpty = !tasks.isEmpty();
        if (isNotEmpty) {
            historyManager.removeTaskAll(tasks);
            prioritizedManager.clearAllTasksFromPrioritizedAndNullLists(tasks);
            tasks.clear();
            return true;
        } else {
            System.out.println("Не создана ни одной задачи ,чтобы удалять их.");
            return false;
        }
    }

    @Override
    public boolean clearEpics() { //Удаление всех Epic.
        boolean isNotEmpty = !epics.isEmpty();
        if (isNotEmpty) {
            historyManager.removeEpicAll(epics);//Удаляет все Epics из истории задач
            historyManager.removeSubtaskAll(subtasks);//Удаляет все Subtasks из истории задач

            prioritizedManager.clearAllSubtasksFromPrioritizedAndNullLists(subtasks);// //Удаляет все Subtasks из временного периода(prioritizedTasksNotNUll)
            prioritizedManager.clearAllEpicsFromPrioritizedAndNullLists(epics);// Удаляет все Epics из временного периода (nullDateTasks)
            epics.clear();
            subtasks.clear();
            return true;
        } else {
            System.out.println("Не создана ни одной Epic-а ,чтобы удалять их.");
            return false;
        }
    }

    @Override
    public boolean clearSubtasks() { //Удаление всех подзадач.
        boolean isNotEmpty = !subtasks.isEmpty();
        if (isNotEmpty) {

            for (Epic epic : epics.values()) {
                epic.clearSubtasks();
                StatusTask.checkStatus(epic);
                prioritizedManager.removeTaskFromPrioritizedAndNullLists(epic);
                prioritizedManager.addTaskWithoutIntersection(epic);
            }
            prioritizedManager.clearAllSubtasksFromPrioritizedAndNullLists(subtasks);
            historyManager.removeSubtaskAll(subtasks);
            subtasks.clear();
            return true;
        } else {
            System.out.println("Не создана ни одной подзадачи ,чтобы удалять их.");
            return false;
        }
    }

    @Override
    public Optional<Task> getTask(int id) { //Получение Task по идентификатору.

        if (tasks.containsKey(id)) { //Проверяет если такой ID в задачах.
            Task task = tasks.get(id);
            historyManager.add(task);
        } else {
            System.out.println("Не существует такого ID:" + id + " Task");
        }
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Optional<Epic> getEpic(int id) { //Получение Epic по идентификатору.

        if (epics.containsKey(id)) { //Проверяет если такой ID в Epic.
            Epic epic = epics.get(id);
            historyManager.add(epic);
        } else {
            System.out.println("Не существует такого ID:" + id + " Epic");
        }
        return Optional.ofNullable(epics.get(id));
    }

    @Override
    public Optional<Subtask> getSubtask(int id) { //Получение Subtask по идентификатору.

        if (subtasks.containsKey(id)) { //Проверяет если такой ID в подзадачах.
            Subtask subtask = subtasks.get(id);
            historyManager.add(subtask);
        } else {
            System.out.println("Не существует такого ID:" + id + " Subtask");
        }
        return Optional.ofNullable(subtasks.get(id));
    }

    @Override
    public boolean addTask(Task taskInput) { //Добавляет полученный объект Task в соответсвующий HashMap и проверяет, если такой ID уже.
        if (Objects.isNull(taskInput)) {
            System.out.println("Переданный объект Null");
            return false;
        }

        int idTaskInput = taskInput.getId();
        boolean isTaskExist = tasks.containsKey(idTaskInput);// Проверяет на наличие задачи.
        boolean isAddTaskWithoutIntersection;

        if (isTaskExist) {
            System.out.println("Задача с таким ID уже создана. Не добавлен задача " + taskInput.getName() + " с ID:" + taskInput.getId());
            return false;
        }

        isAddTaskWithoutIntersection = prioritizedManager.addTaskWithoutIntersection(taskInput);//Проверяет можно ли вставить задачу без пересечения по времени с другими задачами.
        if (isAddTaskWithoutIntersection) {
            tasks.put(idTaskInput, taskInput);
            return true;
        } else {
            System.out.println("Обнаружено пересечение задач. Не добавлен задача " + taskInput.getName() + " с ID:" + taskInput.getId());
            return false;
        }
    }

    @Override
    public boolean addEpic(Epic epicInput) { //Добавляет полученный объект Epic в соответсвующий HashMap и проверяет, если такой ID уже.
        if (Objects.isNull(epicInput)) {
            System.out.println("Переданный объект Null");
            return false;
        }

        int idEpicInput = epicInput.getId();
        boolean isEpicExist = epics.containsKey(idEpicInput);//Проверяет на наличие Epic.

        if (isEpicExist) {
            System.out.println("Epic с таким ID уже создан. Не добавлен " + epicInput.getName() + " с ID:" + epicInput.getId());
            return false;
        } else {
            //Вставляет Epic в nullDateTasks потому, что создаются все эпики без подзадач и у эпика тогда startTime == null.
            prioritizedManager.addTaskWithoutIntersection(epicInput);
            epics.put(idEpicInput, epicInput);
            return true;
        }
    }

    @Override
    public boolean addSubtask(Subtask subtaskInput) { //Добавляет полученный объект Subtask в соответсвующий HashMap и проверяет, если такой ID уже.
        if (Objects.isNull(subtaskInput)) {
            System.out.println("Переданный объект Null");
            return false;
        }

        int idSubtaskInput = subtaskInput.getId();
        boolean isSubtaskExist = subtasks.containsKey(idSubtaskInput); // Проверяет на наличие подзадачи.

        if (isSubtaskExist) {
            System.out.println("Подзадача с таким ID уже создана. Не добавлена подзадача " + subtaskInput.getName() + " с ID:" + subtaskInput.getId());
            return false;
        }

        int idEpic = subtaskInput.getIdEpic();
        boolean isEpicExist = epics.containsKey(idEpic); //Если есть Epic, к которому подзадача принадлежит.
        boolean isAddTaskWithoutIntersection = false; //Можно ли вставить задачу без пересечения по времени с другими задачами.

        if (isEpicExist) {
            isAddTaskWithoutIntersection = prioritizedManager.addTaskWithoutIntersection(subtaskInput); //Проверяет можно ли вставить задачу без пересечения по времени с другими задачами.
        }

        if (isAddTaskWithoutIntersection) {
            subtasks.put(idSubtaskInput, subtaskInput);
            Epic epic = epics.get(idEpic);
            epic.addSubtask(subtaskInput); //Добавляет подзадачу в список определенного Epic.
            StatusTask.checkStatus(epic); //Проверяет статус Epic после добавления в него подзадачи.
            prioritizedManager.removeTaskFromPrioritizedAndNullLists(epic);// Удаляем из-за того что эпики с подзадачами не хранятся в приоритетах.
            return true;
        }

        if (!isEpicExist) {
            System.out.println("Такого Epic не существует для добавления в него подзадачи. Не добавлена подзадача " + subtaskInput.getName() + " с ID:" + subtaskInput.getId());
            return false;
        }

        System.out.println("Обнаружено пересечение задач. Не добавлена подзадача " + subtaskInput.getName() + " с ID:" + subtaskInput.getId());
        return false;
    }

    @Override
    public boolean updateTask(Task taskInput) { //Обновление Task. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        int idTaskInput = taskInput.getId();
        boolean isTaskExist = tasks.containsKey(idTaskInput);
        boolean isUpdateTaskWithoutIntersection;

        if (isTaskExist) {
            Task oldTask = tasks.get(idTaskInput);
            isUpdateTaskWithoutIntersection = prioritizedManager.updateTaskWithoutIntersection(taskInput, oldTask);
        } else {
            System.out.println("Такой Задачи не существует для обновления");
            return false;
        }

        if (isUpdateTaskWithoutIntersection) {
            tasks.put(idTaskInput, taskInput);
            return true;
        } else {
            System.out.println("Обнаружено пересечение для обновления задачи.");
            return false;
        }
    }

    @Override
    public boolean updateEpic(Epic epicInput) { // Обновление Epic. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        int idEpicInput = epicInput.getId();
        boolean isEpicExist = epics.containsKey(idEpicInput);

        if (isEpicExist) {
            HashMap<Integer, Subtask> epicInputSubtasksMap = epicInput.getSubtasksMap();

            boolean isEmptyEpicInputSubtasksMap = epicInputSubtasksMap.isEmpty();
            if (isEmptyEpicInputSubtasksMap) {
                updateNewEpicWithoutSubtasks(epicInput);
            } else {
                updateNewEpicWithSubtasks(epicInput, epicInputSubtasksMap);
            }
            return true;
        } else {
            System.out.println("Такого Epic не существует для обновления");
            return false;
        }
    }

    private void updateNewEpicWithoutSubtasks(Epic epicInput) {
        Epic oldEpic = epics.get(epicInput.getId());
        HashMap<Integer, Subtask> oldEpicSubtask = oldEpic.getSubtasksMap();
        if (!oldEpicSubtask.isEmpty()) {
            List<Integer> idSubtasksOldEpic = new ArrayList<>(oldEpicSubtask.keySet());
            for (Integer subtaskId : idSubtasksOldEpic) {
                removeSubtaskById(subtaskId);
            }
        }
        prioritizedManager.updateEpicWithoutIntersection(epicInput, oldEpic);
        epics.put(epicInput.getId(), epicInput);
    }

    private void updateNewEpicWithSubtasks(Epic epicInput, HashMap<Integer, Subtask> newSubtasks) {
        for (Subtask newSubtask : newSubtasks.values()) {
            int idSubtaskInput = newSubtask.getId();
            if (subtasks.containsKey(idSubtaskInput)) {
                updateSubtask(newSubtask);
            } else {
                addSubtask(newSubtask);
            }
        }

        Epic oldEpic = epics.get(epicInput.getId());
        prioritizedManager.updateEpicWithoutIntersection(epicInput, oldEpic);
        epics.put(epicInput.getId(), epicInput);
    }

    @Override
    public boolean updateSubtask(Subtask subtaskInput) { // Обновление Subtask. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        int idSubtaskInput = subtaskInput.getId();
        boolean isSubtaskExist = subtasks.containsKey(idSubtaskInput);
        boolean isUpdateSubtaskWithoutIntersection;
        boolean isEpicExist = epics.containsKey(subtaskInput.getIdEpic());

        if (!isEpicExist) {
            System.out.println("Не существует Epic с ID:" + subtaskInput.getIdEpic() + " .Не возможно обновление подзадачи " + subtaskInput.getName() + " с ID:" + idSubtaskInput);
            return false;
        }

        if (isSubtaskExist) {
            Subtask oldSubtask = subtasks.get(idSubtaskInput);
            isUpdateSubtaskWithoutIntersection = prioritizedManager.updateSubtaskWithoutIntersection(subtaskInput, oldSubtask);
        } else {
            System.out.println("Такой подзадачи не существует для обновления");
            return false;
        }

        if (isUpdateSubtaskWithoutIntersection) {
            subtasks.put(idSubtaskInput, subtaskInput);
            int idEpic = subtaskInput.getIdEpic();
            Epic epicSubtaskInput = epics.get(idEpic);

            prioritizedManager.removeTaskFromPrioritizedAndNullLists(epicSubtaskInput);//на всякий удаляем эпик потому что его не должно быть во временном отрывке
            epicSubtaskInput.addSubtask(subtaskInput); //Добавляет подзадачу в список Epic.
            StatusTask.checkStatus(epicSubtaskInput); //Проверяет статус Epic после обновления его подзадачи.
            return true;
        } else {
            System.out.println("Обнаружено пересечение задач ,во время обновлении подзадачи. Не добавлена подзадача " + subtaskInput.getName() + " c id:" + subtaskInput.getId());
            return false;
        }
    }

    @Override
    public boolean removeTaskById(int id) { //Удаление Task по идентификатору.
        boolean containsTask = tasks.containsKey(id);
        if (containsTask) {
            historyManager.removeById(id);//удаляет Task по ID в истории задач
            prioritizedManager.removeTaskFromPrioritizedAndNullLists(tasks.get(id));
            tasks.remove(id);
            return true;
        } else {
            System.out.println("Такой задачи не существует");
            return false;
        }

    }

    @Override
    public boolean removeEpicById(int id) { //Удаление Epic по идентификатору и его подзадач.
        boolean containsEpic = epics.containsKey(id);

        if (containsEpic) {
            Epic epicDelete = epics.get(id);
            HashMap<Integer, Subtask> subtasksEpicMap = epicDelete.getSubtasksMap();
            for (Subtask subtask : subtasksEpicMap.values()) {//удаляет подзадачи Epic-а по ID в истории задач
                int subtaskId = subtask.getId();
                historyManager.removeById(subtaskId);
            }

            for (Subtask subtask : subtasksEpicMap.values()) {
                int idSubtask = subtask.getId();
                subtasks.remove(idSubtask);

            }
            prioritizedManager.clearAllSubtasksFromPrioritizedAndNullLists(subtasksEpicMap); // Удаляет все подзадачи из временного диапазона.
            prioritizedManager.removeTaskFromPrioritizedAndNullLists(epicDelete);// Удаляет Epic из временного диапазона.
            historyManager.removeById(id);//удаляет Epic по ID в истории задач
            epics.remove(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeSubtaskById(int id) { //Удаление Subtask по идентификатору.
        boolean containsSubtask = subtasks.containsKey(id);

        if (containsSubtask) {
            Subtask subtaskDelete = subtasks.get(id);
            int epicIdOfSubtask = subtaskDelete.getIdEpic(); //сохраняет ID Epic пока не удалил Subtask.

            Epic epicSubtaskDelete = epics.get(epicIdOfSubtask);
            epicSubtaskDelete.removeSubtaskById(id);//Удаление Subtask по идентификатору в самом Epic.
            StatusTask.checkStatus(epicSubtaskDelete);

            prioritizedManager.removeTaskFromPrioritizedAndNullLists(subtaskDelete);// Удаляет подзадачу из временного диапазона.
            historyManager.removeById(id);//удаляет подзадачу по ID в истории задач
            subtasks.remove(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public HashMap<Integer, Subtask> getMapSubtasksByEpicId(int id) { //Получение списка всех подзадач определённого Epic.

        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            return epic.getSubtasksMap();
        }

        System.out.println("Такого Epic нету");
        return new HashMap<>();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public List<Task> getAllTasksEpicSubtask() {
        List<Task> allTasksEpicSubtask = new ArrayList<>();
        allTasksEpicSubtask.addAll(getListTasks());
        allTasksEpicSubtask.addAll(getListEpics());
        allTasksEpicSubtask.addAll(getListSubtasks());
        return allTasksEpicSubtask;
    }
}

