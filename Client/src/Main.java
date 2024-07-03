import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scnr = new Scanner(System.in);
        System.out.println("Server IP?: ");
        String svrIP = scnr.nextLine();
        System.out.print("Port?: ");
        int port = scnr.nextInt();

        while(true) {
            printMenu();
            int input = scnr.nextInt();
            runOperation(input, port, svrIP, scnr);
        }

        //LINUX COMMANDS
        //date, uptime, free, netstat, who, ps -ef
    }

    public static void runOperation(int type, int port, String svrIP, Scanner scnr) throws IOException {
        String cmd;
        switch (type) {
            case 1:
                cmd = "date";
                break;
            case 2:
                cmd = "uptime";
                break;
            case 3:
                cmd = "free";
                break;
            case 4:
                cmd = "netstat";
                break;
            case 5:
                cmd = "who";
                break;
            case 6:
                cmd = "ps -ef";
                break;
            case 7:
                exitProgram();
                return;
            default:
                System.out.println("Invalid input");
                return;
        }

        System.out.println("How many times?: ");
        int x = scnr.nextInt();

        //do command

            Stack<ServerThread> threadStack = new Stack<>();
            for (int i = 1; i <= x; i++){
                ServerThread thread = new ServerThread(cmd, i, svrIP, port);
                threadStack.push(thread);
            }

            ArrayList<Long> times = new ArrayList<>();
            long startTime;
            long time;

            for(ServerThread thread : threadStack){

                System.out.println("Thread " + thread.number);
                startTime = System.currentTimeMillis();
                thread.run();
                time = System.currentTimeMillis() - startTime;

                System.out.println("Completion: " + time + "ms");
                times.add(time);
            }
            printStats(times);

    }

    public static void printStats(ArrayList<Long> times){
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        long sum = 0;
        int count = times.size();
        for(long time : times){
            if(time < min){
                min = time;
            }
            if(time > max){
                max = time;
            }
            sum += time;
        }

        System.out.println("\nFastest: " + min + "ms");
        System.out.println("Slowest: " + max + "ms");
        System.out.println("Total Time Elapsed: " + sum + "ms");
        System.out.println("Avg. Time: " + sum / count + "ms\n");
    }

    public static void exitProgram(){
        System.out.println("Made With <3, Group 10");
        System.exit(0);
    }

    public static void printMenu(){
        System.out.println("Server Operations:");
        System.out.println("1. Date and Time");
        System.out.println("2. Server Uptime");
        System.out.println("3. Memory Use");
        System.out.println("4. Netstat");
        System.out.println("5. Current Users");
        System.out.println("6. Running Processes");
        System.out.println("7. Exit");
        System.out.println("Enter number (1-7): ");
    }
}

class ServerThread implements Runnable {

    private final String svrIP;
    private final int port;

    private final String command;

    public final int number;

    OutputStream out;
    PrintWriter writer;
    InputStream input;
    BufferedReader reader;

    public ServerThread(String command, int number, String ip, int port) throws IOException {
        this.number = number;
        this.command = command;
        this.svrIP = ip;
        this.port = port;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(svrIP, port)) {
            out = socket.getOutputStream();
            writer = new PrintWriter(out, true);
            input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));

            writer.println(this.command);
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            socket.close();
            out.close();
            writer.close();
            input.close();
            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}