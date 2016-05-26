import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by 990430 on 2016/5/26.
 */
public class SocketServerThread implements Runnable {
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
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String hello = bufferedReader.readLine();
                bufferedWriter.write(hello);
                bufferedWriter.flush();
                socket.close();

                System.out.println(hello);
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
        SocketServerThread socketServerThread = new SocketServerThread();
        Thread thread = new Thread(socketServerThread);
        thread.start();
        while (true) {
            int c;
            try {
                c = System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
                c = -1;
            }
            if (c == -1 || (char) c == 'q') {
                socketServerThread.setToQuit(true);
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
