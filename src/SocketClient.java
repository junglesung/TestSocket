import java.io.*;
import java.net.*;

/**
 * Created by 990430 on 2016/5/26.
 */
public class SocketClient {
    private static String SERVER_IP = "127.0.0.1";
    private static String MESSAGE = "Hello\n";
    public static void main(String[] args) {
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(SERVER_IP, SocketServer.PORT);
            Socket socket = null;
            for (int i = 0; i < 20; i++) {  // 20 sec
                socket = new Socket();
                try {
                    socket.connect(inetSocketAddress, 1000);
                } catch (SocketTimeoutException e) {
                    System.out.println("Timeout " + String.valueOf(i));
                    continue;
                }
                break;
            }
            if (!socket.isConnected()) {
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write(MESSAGE);
            bufferedWriter.flush();
            String hello = bufferedReader.readLine();
            socket.close();

            System.out.print(hello);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
