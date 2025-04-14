package manager.interfaces;

import manager.InMemoryHistoryManager.Node;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface HistoryManager {

    void add(Task task);

    void removeFirst();

    void removeLast();

    void removeById(int id);

    void removeTaskAll(Map<Integer, Task> tasksMap);

    void removeEpicAll(Map<Integer, Epic> epicsMap);

    void removeSubtaskAll(Map<Integer, Subtask> subtasksMap);

    HashMap<Integer, Node> getTasksHistoryInMap();

    ArrayList<Task> getListHistory();

    void linkLast(Task task);

    void removeNode(Node node);

    Task getFirst();

    Task getLast();
}
