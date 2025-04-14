package manager.interfaces;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface TaskManager {

    List<Task> getListTasks(); //Получение списка всех задач

    List<Epic> getListEpics(); //Получение списка всех Epic

    List<Subtask> getListSubtasks(); //Получение списка всех подзадач

    List<Task> getAllTasksEpicSubtask();

    HashMap<Integer, Task> getMapTasks();

    HashMap<Integer, Epic> getMapEpics();

    HashMap<Integer, Subtask> getMapSubtasks();

    boolean clearTasks(); //Удаление всех задач.

    boolean clearEpics(); //Удаление всех Epic.

    boolean clearSubtasks(); //Удаление всех подзадач.

    Optional<Task> getTask(int id); //Получение ask по идентификатору

    Optional<Epic> getEpic(int id); //Получение Epic по идентификатору

    Optional<Subtask> getSubtask(int id); //Получение Subtask по идентификатору

    boolean addTask(Task taskInput); //Добавляет полученный объект Task в соответсвующий HashMap и проверяет, если такой ID уже

    boolean addEpic(Epic taskInput); //Добавляет полученный объект Epic в соответсвующий HashMap и проверяет, если такой ID уже

    boolean addSubtask(Subtask taskInput); //Добавляет полученный объект Subtask в соответсвующий HashMap и проверяет, если такой ID уже

    boolean updateTask(Task taskInput); // Обновление Task. Новая версия объекта с верным идентификатором передаётся в виде параметра.

    boolean updateEpic(Epic taskInput); // Обновление Epic. Новая версия объекта с верным идентификатором передаётся в виде параметра.

    boolean updateSubtask(Subtask taskInput); // Обновление Subtask. Новая версия объекта с верным идентификатором передаётся в виде параметра.

    boolean removeTaskById(int id);//Удаление Task по идентификатору.

    boolean removeEpicById(int id); //Удаление Epic по идентификатору и его подзадачи.

    boolean removeSubtaskById(int id);//Удаление Subtask по идентификатору.

    HashMap<Integer, Subtask> getMapSubtasksByEpicId(int id); //Получение списка всех подзадач определённого Epic.

    int getNewId();

    List<Task> getHistory();

    HistoryManager getHistoryManager();

    List<Task> getPrioritizedTasks();
}