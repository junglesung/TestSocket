import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by 990430 on 2016/5/26.
 */
public class SocketServer {
    public static int PORT = 10123;
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            serverSocket.setSoTimeout(1000);  // 1000 ms
            Socket socket = null;
            for (int i = 0; i < 20; i++) {  // 20 sec
                try {
                    socket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    System.out.println("Timeout " + String.valueOf(i));
                    continue;
                }
                break;
            }
            if (socket == null) {
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String hello = bufferedReader.readLine();
            bufferedWriter.write(hello);
            bufferedWriter.flush();
            socket.close();

            System.out.print(hello);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
