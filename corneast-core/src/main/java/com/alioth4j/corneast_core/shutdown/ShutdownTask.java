package com.alioth4j.corneast_core.shutdown;

/**
 * Interface of shutdown tasks, for shutting down service components elegantly.
 *
 * @author Alioth Null
 */
public interface ShutdownTask {

    /**
     * Shutdown service component.
     */
    void shutdown();

    /**
     * Gets the name of this component.
     * @return name of this component
     */
    String getComponentName();

}
