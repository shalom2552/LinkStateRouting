import java.util.HashMap;
import java.util.Map;

public class MessagesQueue {

    private Map<Integer, Map<Integer, Double>> queue = new HashMap<>();

    /**
     * @param message
     */
    public synchronized void addMessage(Message message) {
        queue.put(message.getSenderId(), message.getWeightVector());
    }

    /**
     * @param message
     * @return
     */
    public synchronized boolean notReceived(Message message) {
        return !queue.containsKey(message.getSenderId());
    }

    public synchronized int size() {
        return queue.size();
    }

    public synchronized Map<Integer, Map<Integer, Double>> getQueue() {
        return queue;
    }
}
