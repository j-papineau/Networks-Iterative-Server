import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scnr = new Scanner(System.in);
        System.out.println("Enter Port to Open Socket:");
        int port = scnr.nextInt();

        //open socket
        ServerSocket serverSocket = openSocket(port);
        System.out.println("Socket opened on port " + port);
        //socket opened start listening
        while(true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("client connected");

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream out = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(out, true);

                String inputCmd = reader.readLine();

                //execute command
                Process p = Runtime.getRuntime().exec(inputCmd);
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                System.out.println("Exec cmd: " + inputCmd);
                while ((line = stdInput.readLine()) != null) {
                    writer.println(line);
                }


                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

    }

    public static ServerSocket openSocket(int port){
        try {
            return new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}