package managerTest;

import manager.InMemoryPrioritizedManager;
import managerTest.abstractManagersTest.PrioritizedManagerTest;


public class InMemoryPrioritizedManagerTest extends PrioritizedManagerTest<InMemoryPrioritizedManager> {

    @Override
    public InMemoryPrioritizedManager createPrioritizedManager() {
        return new InMemoryPrioritizedManager();
    }

}
