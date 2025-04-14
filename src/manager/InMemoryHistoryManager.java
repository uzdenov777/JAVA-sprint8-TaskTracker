
package manager;

import manager.interfaces.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {
    private Node first;
    private Node last;
    private final HashMap<Integer, Node> historyTask = new HashMap<>(); //Хранит последние 10 просмотренных пользователем задач.

    @Override
    public void add(Task task) { //вызывается при getTask, getEpic, getSubtask
        if (Objects.isNull(task)) {
            return;
        }
        int taskId = task.getId();

        if (historyTask.containsKey(taskId)) { //задача должна быть в единственном экземпляре в истории, если есть удаляем
            Node deleteNode = historyTask.get(taskId);
            removeNode(deleteNode);
        }

        if (historyTask.size() == 10) { //Проверяет, чтобы в historyTask было не больше 10 Задач, в худшем удаляет первый элемент
            removeNode(first);
        }

        linkLast(task);
    }

    @Override
    public void removeFirst() {
        Node oldFirst = first;
        Task getOldTask = first.task;
        int idTask = getOldTask.getId();

        first = first.next;
        first.prev = null;
        historyTask.remove(idTask);
        oldFirst.next = null;
        oldFirst.prev = null;
    }

    @Override
    public void removeLast() {
        Node oldLast = last;
        Task getOldTask = last.task;
        int idTask = getOldTask.getId();

        last = last.prev;
        last.next = null;
        historyTask.remove(idTask);
        oldLast.next = null;
        oldLast.prev = null;
    }

    @Override
    public void removeById(int id) { //удаляет запись по ID
        if (historyTask.containsKey(id)) {
            Node nodeDeleted = historyTask.get(id);
            removeNode(nodeDeleted);
        }
    }

    @Override
    public void removeTaskAll(Map<Integer, Task> tasksMap) { //удаляет все Задачи

        for (Map.Entry<Integer, Task> entry : tasksMap.entrySet()) {
            Integer id = entry.getKey();
            if (historyTask.containsKey(id)) {
                Node nodeDeleted = historyTask.get(id);
                removeNode(nodeDeleted);
            }
        }
    }

    @Override
    public void removeEpicAll(Map<Integer, Epic> epicsMap) { //удаляет все Epic

        for (Map.Entry<Integer, Epic> entry : epicsMap.entrySet()) {
            Integer id = entry.getKey();
            if (historyTask.containsKey(id)) {
                Node nodeDeleted = historyTask.get(id);
                removeNode(nodeDeleted);
            }
        }
    }

    @Override
    public void removeSubtaskAll(Map<Integer, Subtask> subtasksMap) { //удаляет все подзадачи

        for (Map.Entry<Integer, Subtask> entry : subtasksMap.entrySet()) {
            int id = entry.getKey();
            if (historyTask.containsKey(id)) {
                Node nodeDeleted = historyTask.get(id);
                removeNode(nodeDeleted);
            }
        }
    }

    @Override
    public void removeNode(Node node) { // вырезает запись
        int idDeletingNode = node.task.getId();

        if (first == node) { //вырезает начало
            first = first.next;
            first.prev = null;
        } else if (last == node) { // вырезает конец
            last = last.prev;
            last.next = null;
        } else {
            Node previous = node.prev; // вырезает между - среднюю запись
            Node next = node.next;
            previous.next = next;
            next.prev = previous;
        }
        historyTask.remove(idDeletingNode);
        node.next = null;
        node.prev = null;
    }

    @Override
    public HashMap<Integer, Node> getTasksHistoryInMap() { // возвращает хэш таблицу historyTask
        return historyTask;
    }

    @Override
    public ArrayList<Task> getListHistory() { // заполняет List последовательно от старого к новому просмотру задач
        ArrayList<Task> tasks = new ArrayList<>();
        Node current = first;
        while (Objects.nonNull(current)) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    @Override
    public void linkLast(Task task) { // добавляет в конец списка
        Node nodeNew = new Node(last, task);
        if (Objects.isNull(first)) { // истина когда ни одной записи нет
            first = nodeNew;
        } else if (Objects.isNull(last)) { // истина когда есть только одна запись - first и нужно добавить вторую запись
            last = nodeNew;
            last.prev = first;
            first.next = last;
        } else {
            nodeNew.prev = last; //добавляет в конец списка
            last.next = nodeNew;
            last = nodeNew;
        }
        int taskId = task.getId();
        historyTask.put(taskId, nodeNew);
    }

    @Override
    public Task getFirst() {
        return first.task;
    }

    @Override
    public Task getLast() {
        return last.task;
    }

    public class Node {
        Node next;
        Node prev;
        Task task;

        public Node(Node prev, Task task) {
            this.prev = prev;
            this.task = task;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true; // если ссылки на один и тот же объект
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(next, node.next) &&
                    Objects.equals(prev, node.prev) &&
                    Objects.equals(task, node.task);
        }

        @Override
        public int hashCode() {
            return Objects.hash(next, prev, task);
        }
    }
}