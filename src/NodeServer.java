import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

/**
 *
 */
public class NodeServer extends Thread {
    private final int neighbourId;
    private final Socket socket;
    private final List<Neighbour> neighbours;
    private final MessagesQueue messagesQueue;

    /**
     * @param neighbourId
     * @param socket
     * @param neighbours
     * @param messagesQueue
     */
    public NodeServer(Integer neighbourId, Socket socket, List<Neighbour> neighbours, MessagesQueue messagesQueue) {
        this.neighbourId = neighbourId;
        this.socket = socket;
        this.neighbours = neighbours;
        this.messagesQueue = messagesQueue;
    }

    @Override
    /**
     *
     */
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) in.readObject();

            if (messagesQueue.notReceived(message)) {
                this.messagesQueue.addMessage(message);
                for (Neighbour neighbor : this.neighbours) {
                    if (neighbourId != neighbor.getId())
                        Node.sendMessage(neighbor.getPort1(), message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
