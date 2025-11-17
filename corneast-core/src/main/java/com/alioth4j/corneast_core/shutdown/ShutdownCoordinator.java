package com.alioth4j.corneast_core.shutdown;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The unified coordinator to execute shutdown tasks.
 *
 * @author Alioth Null
 */
@Component
public class ShutdownCoordinator {

    private static final Logger log = LoggerFactory.getLogger(ShutdownCoordinator.class);

    /* shutdown tasks injected by Spring Framework */
    @Autowired
    private List<ShutdownTask> shutdownTaskList;

    /**
     * Executes shutdown tasks before the container destroys.
     */
    @PreDestroy
    public void shutdown() {
        for (ShutdownTask shutdownTask : shutdownTaskList) {
            try {
                log.info("Start shutting down {}", shutdownTask.getComponentName());
                shutdownTask.shutdown();
                log.info("End shutting down {}", shutdownTask.getComponentName());
            } catch (Exception e) {
                log.warn("Error shutting down {}", shutdownTask.getComponentName(), e);
            }
        }
    }

}
