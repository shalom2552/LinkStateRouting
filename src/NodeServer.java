import java.io.EOFException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class NodeServer extends Thread {
    private int id;
    private int neighbourId;
    private Socket socket;
    private List<Neighbour> neighbours;
    private MessagesQueue messagesQueue;

    public NodeServer(int id, Integer neighbourId, Socket socket, List<Neighbour> neighbours, MessagesQueue messagesQueue) {
        this.id = id;
        this.neighbourId = neighbourId;
        this.socket = socket;
        this.neighbours = neighbours;
        this.messagesQueue = messagesQueue;
    }


    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) in.readObject();


            if (messagesQueue.notReceived(message)) {
                this.messagesQueue.addMessage(message);
                for (Neighbour neighbor : this.neighbours) {
                    if (neighbourId != neighbor.getId())
                        sendMessage(message, neighbor.getPort1());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message, Integer port){
        try {
            Socket socket = new Socket("127.0.0.1", port);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(message);
            outputStream.flush();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            sendMessage(message, port);
        }
    }


}
