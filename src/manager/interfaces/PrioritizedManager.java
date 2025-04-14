package manager.interfaces;

import model.Epic;
import model.Subtask;
import model.Task;


import java.util.List;
import java.util.Map;

public interface PrioritizedManager {

    boolean addTaskWithoutIntersection(Task task);

    boolean updateTaskWithoutIntersection(Task newTask, Task oldTask);

    void updateEpicWithoutIntersection(Epic newEpic, Epic oldEpic);

    boolean updateSubtaskWithoutIntersection(Subtask newSubtask, Subtask oldTSubtask);

    void removeTaskFromPrioritizedAndNullLists(Task task);

    void clearAllTasksFromPrioritizedAndNullLists(Map<Integer, Task> inputTasksMap);

    void clearAllEpicsFromPrioritizedAndNullLists(Map<Integer, Epic> inputTasksMap);

    void clearAllSubtasksFromPrioritizedAndNullLists(Map<Integer, Subtask> inputTasksMap);

    List<Task> getPrioritizedTasks();
}
