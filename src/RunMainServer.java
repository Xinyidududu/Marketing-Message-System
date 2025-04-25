import java.io.*;
import java.net.*;

/**
 * A RunMainServer Class for CS180 project 5
 *
 * @author Abhi Chalasani, Xinyi Guan, Marissa Capelli, Snigdha Mishra
 * @version 4/29/23
 */
public class RunMainServer implements Serializable {
    //USE TO RUN THE MAIN SERVER, NOT COMPATABLE WITH THREADS YET
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(4242);
            while (true) {
                Socket socket = serverSocket.accept();
                MainServer ms = new MainServer(socket);
                Thread t = new Thread(ms);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}