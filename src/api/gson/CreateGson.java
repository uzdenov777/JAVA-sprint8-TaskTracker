package api.gson;

import com.google.gson.*;
import model.Epic;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class CreateGson {

    public static Gson createGson() {
        JsonSerializer<LocalDateTime> localDateTimeSerializer = (src, typeOfSrc, context) ->// адаптеры для LocalDateTime
                new JsonPrimitive(src.toString());

        JsonDeserializer<LocalDateTime> localDateTimeDeserializer = (json, typeOfT, context) ->
                LocalDateTime.parse(json.getAsString());


        JsonSerializer<Duration> durationSerializer = (src, typeOfSrc, context) -> // адаптеры для Duration
                new JsonPrimitive(src.toMinutes());

        JsonDeserializer<Duration> durationDeserializer = (json, typeOfT, context) ->
                Duration.ofMinutes(json.getAsLong());


        RuntimeTypeAdapterFactory<Task> adapterFactory = RuntimeTypeAdapterFactory // полиморфизм
                .of(Task.class, "taskTypeForJson") // поле, по которому Gson будет определять тип
                .registerSubtype(Task.class, "TASK")
                .registerSubtype(Epic.class, "EPIC")
                .registerSubtype(Subtask.class, "SUBTASK");


        return new GsonBuilder()        // сборка готового Gson
                .registerTypeAdapter(LocalDateTime.class, localDateTimeSerializer)
                .registerTypeAdapter(LocalDateTime.class, localDateTimeDeserializer)
                .registerTypeAdapter(Duration.class, durationSerializer)
                .registerTypeAdapter(Duration.class, durationDeserializer)
                .registerTypeAdapterFactory(adapterFactory)
                .setPrettyPrinting()
                .create();
    }
}
