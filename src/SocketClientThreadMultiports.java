import java.io.*;
import java.net.*;

/**
 * Created by 990430 on 2016/5/26.
 */
public class SocketClientThreadMultiPorts implements Runnable {
    public static int PORT_MIN = 10121;
    public static int PORT_MAX = 10124;  // 10 ports
    private static String SERVER_IP = "127.0.0.1";
    private static String MESSAGE = "Hello\n";

    private boolean toQuit = false;

    @Override
    public void run() {
        try {
            Socket socket = null;
            for (int port = PORT_MIN; port <= PORT_MAX && !isToQuit(); port++) {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(SERVER_IP, port);
                socket = new Socket();
                try {
                    socket.connect(inetSocketAddress, 1000);
                } catch (SocketTimeoutException e) {
                    System.out.println("Port " + String.valueOf(port) + " has no response. Try other ports...");
                    continue;
                } catch (ConnectException e) {
                    System.out.println("Port " + String.valueOf(port) + " has no response. Try other ports...");
                    continue;
                }
                System.out.println("Port " + String.valueOf(port) + " is connected");
                break;
            }
            if (socket == null || !socket.isConnected()) {
                System.out.println("Connect failed. Please retry later");
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write(MESSAGE);
            bufferedWriter.flush();
            String hello = bufferedReader.readLine();
            socket.close();

            System.out.println(hello);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean isToQuit() {
        return toQuit;
    }

    public synchronized void setToQuit(boolean toQuit) {
        this.toQuit = toQuit;
    }

    public static void main(String[] args) {
        // Start thread
        SocketClientThreadMultiPorts socketClientThreadMultiPorts = new SocketClientThreadMultiPorts();
        Thread thread = new Thread(socketClientThreadMultiPorts);
        thread.start();

        // Wait for user quit command 'q'
        while (true) {
            int c;
            try {
                c = System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
                c = -1;
            }
            if (c == -1 || (char) c == 'q') {
                socketClientThreadMultiPorts.setToQuit(true);
                break;
            }
        }

        // Wait for the thread to stop
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Exit
        System.out.println("Bye bye");
    }
}
