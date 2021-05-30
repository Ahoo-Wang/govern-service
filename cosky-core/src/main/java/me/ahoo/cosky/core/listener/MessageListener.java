package me.ahoo.cosky.core.listener;

/**
 * @author ahoo wang
 */
@FunctionalInterface
public interface MessageListener {
    void onMessage(Topic topic, String channel, String message);
}
