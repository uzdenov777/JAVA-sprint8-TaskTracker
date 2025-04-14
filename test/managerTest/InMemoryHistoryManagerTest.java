package managerTest;

import manager.InMemoryHistoryManager;
import managerTest.abstractManagersTest.HistoryManagerTest;

public class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {
    @Override
    public InMemoryHistoryManager createHistoryManager() {
        return new InMemoryHistoryManager();
    }


}
