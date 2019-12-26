import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Deque;
import java.util.Queue;

public class Server {
    int port = 3124;
    InetAddress ip = null;
    Integer x, y;
    ServerSocket ss;
    List<Socket> cs;
    List<DataInputStream> dis;
    List<DataOutputStream> dos;
    List<ReaderThread> readers;
    ArrayList<Threads> threads;
    Socket sc;
    ArrayList<Task> tasks;
    int N, count;

    //DataInputStream dis;
    //DataOutputStream dos;
    //ReaderThread reader;
    public void task1() {
        try
        {wait(1000);}
        catch (InterruptedException ex) {
            ex.printStackTrace();;
        }
    }
    private class Task {
        public Task(int idClient, int taskid, int res) {
            this.idClient = idClient;
            Taskid = taskid;
            this.res = res;
        }

        public int getIdClient() {
            return idClient;
        }

        public int getTaskid() {
            return Taskid;
        }

        int idClient;
        int Taskid;
        int res;
    }
    class Threads{
        public Task t;
        //public int threadId;
        public boolean busy;
    }

    public void task2() {
        try
        {wait(5000);}
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    class ExecuteThread extends Thread {
        int id;

        public ExecuteThread(int id) {
            this.id = id;
        }

        @Override
        public void run () {
            int typeTask = threads.get(id).t.Taskid;

            switch (typeTask)
            {
                case 1: task1(); break;
                case 2: task2(); break;
                default: task1(); break;
            }
            threads.get(id).busy = false;
            count --;
            // terminate thread
        }
    }

    void popTask ()
    {
        if (!tasks.isEmpty() && count < N) {
            Task task = tasks.get(0);
            tasks.remove(0);
            int i = 0;
            while (threads.get(i).busy && i < N)
            {
                i++;
            }
            if (i == N) {
                // error
            }
            threads.get(i).t = task;
            threads.get(i).busy = true;
            count ++;
            ExecuteThread ex = new ExecuteThread(i);
            ex.start();

        }
    }

    class ReaderThread extends  Thread {
        public  int id;
        @Override
        public void run() {
            while (true) {
                try {
                    /*Integer myX, myY;
                    myX = dis.get(id).readInt();
                    myY = dis.get(id).readInt();
                    System.out.println(id);
                    System.out.println(myX);
                    System.out.println(myY);
                    for (int i =0; i < dos.size(); i++) {
                        dos.get(i).writeInt(myX);
                        dos.get(i).writeInt(myY);
                    }*/
                    Integer taskId;
                    taskId = dis.get(id).readInt();
                    tasks.add(new Task(id, taskId, 0 ));
                    String queueTask = new String();
                    for (int j = 0; j <tasks.size(); j++ )
                    {
                        queueTask += ("taskType " + tasks.get(j).Taskid + " Client: " + tasks.get(j).idClient + " | ");
                    }
                    String executingTasks = new String();

                    for (int i = 0; i <threads.size(); i ++)
                    {
                        if (threads.get(i).busy)
                        {
                            executingTasks += ("task type: " + threads.get(i).t.Taskid + "Client: " + threads.get(i).t.idClient + " | ");
                        }
                    }
                    
                    for (int i = 0; i <dos.size(); i++) {

                        executingTasks += "";


                    }

                } catch (java.io.IOException ex) {
                    ex.printStackTrace();
                }

            }
        }
    }
    class TaskManager extends Thread {
        @Override
        public void run() {
            while (true) {
                popTask();
            }
        }
    }
    class MyThread extends Thread {
        @Override
        public void run() {
            try {
                Socket socket = ss.accept();
                synchronized (cs) {
                    int id = cs.size();
                    System.out.println("Connected: " + id + " client!");
                    cs.add(socket);
                    DataInputStream newDis = new DataInputStream(socket.getInputStream());
                    DataOutputStream newDos = new DataOutputStream(socket.getOutputStream());
                    ReaderThread reader = new ReaderThread();
                    reader.id = id;
                    synchronized (dis) {
                        dis.add(newDis);
                        synchronized (dos) {
                            dos.add(new DataOutputStream(newDos));
                            synchronized (readers) {
                                readers.add(reader);
                            }
                        }
                    }
                    reader.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            MyThread nextThread = new MyThread();
            nextThread.start();
        }
    }

    public void startServer() {
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            ss = new ServerSocket(port, 0, ip);
            System.out.println("Server started!");
            cs = new ArrayList<>();
            dis = new ArrayList<>();
            dos = new ArrayList<>();
            readers = new ArrayList<>();
            threads = new ArrayList<>();
            tasks = new ArrayList<>();
            MyThread firstThread = new MyThread();
            TaskManager taskManager = new TaskManager();
            N = 4;
            count = 0;
            firstThread.start();
            taskManager.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}
