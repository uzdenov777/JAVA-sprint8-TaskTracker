package model;

import manager.enums.StatusTask;
import manager.enums.TypeTask;

import java.util.Objects;

public class Subtask extends Task {
    private final int idEpic;

    public Subtask(String name, String description, int id, StatusTask status, int idEpic, TypeTask typeTask, String startTime, long duration) {
        super(name, description, id, status, typeTask, startTime, duration);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return idEpic == subtask.idEpic;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idEpic);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name +
                ", description='" + description +
                ", type=" + type +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime + '\'' +
                ", duration=" + duration + '\'' +
                ", idEpic=" + idEpic +
                '}';
    }
}
