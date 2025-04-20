package manager;

public class HttpTaskManager extends FileBackedTasksManager{
    String URIServer;

    public HttpTaskManager(String URIServer) {
      super();
      this.URIServer = URIServer;
    }
}
