import java.io.*;
import java.net.*;

/**
 * Created by 990430 on 2016/5/26.
 */
public class SocketServerThreadsMultiports implements Runnable{
    public class SocketThread implements Runnable {
        private Socket socket;

        @Override
        public void run() {
            try {
                socket.setSoTimeout(1000);  // Max read timeout 1 sec
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String hello = bufferedReader.readLine();
                bufferedWriter.write(hello);
                bufferedWriter.flush();
                socket.close();

                System.out.println(hello);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SocketThread(Socket socket) {
            this.socket = socket;
        }
    }

    public static int PORT_MIN = 10123;
    public static int PORT_MAX = 10132;  // 10 ports

    private boolean toQuit = false;
    private SocketAddress socketAddress = null;

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = null;
            Socket socket;

            // Bind a port between PORT_MIN to PORT_MAX
            for (int port = PORT_MIN; port <= PORT_MAX; port++) {
                try {
                    synchronized (this) {
                        serverSocket = new ServerSocket(port);
                    }
                } catch (BindException e) {
                    System.out.println("Port " + String.valueOf(port) + " is in use. Try other ports...");
                    continue;
                }
                System.out.println("Port " + String.valueOf(port) + " is bound");
                setSocketAddress(serverSocket.getLocalSocketAddress());
                break;
            }
            if (serverSocket == null) {
                System.out.println("No port can use");
                return;
            }

            // Wait for connection until receiving quit command
            int i = 0;
            serverSocket.setSoTimeout(1000);  // 1000 ms
            while (!isToQuit()) {
                try {
                    socket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    System.out.println("Timeout " + String.valueOf(i++));
                    continue;
                }
                new Thread(new SocketThread(socket)).start();
                i = 0;
            }
            serverSocket.close();
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

    public synchronized SocketAddress getSocketAddress() {
        return socketAddress;
    }

    private synchronized void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public static void main(String[] args) {
        // Start thread
        SocketServerThreadsMultiports socketServerThreads = new SocketServerThreadsMultiports();
        Thread thread = new Thread(socketServerThreads);
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
                socketServerThreads.setToQuit(true);
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

