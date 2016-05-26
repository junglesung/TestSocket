import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by 990430 on 2016/5/26.
 */
public class SocketServerThreads implements Runnable{
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

    public static int PORT = 10123;

    private boolean toQuit = false;

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            serverSocket.setSoTimeout(1000);  // 1000 ms
            Socket socket;
            int i = 0;
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
        SocketServerThreads socketServerThreads = new SocketServerThreads();
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

