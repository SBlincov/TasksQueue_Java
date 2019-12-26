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
import java.util.concurrent.TimeUnit;

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
        {   System.out.println("Task 1 started");
            TimeUnit.SECONDS.sleep(10);
            System.out.println("Task 1 ended!");}
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
        {
            System.out.println("Task 2 started");
            TimeUnit.SECONDS.sleep(5);;
            System.out.println("Task 2 ended!");}
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
            System.out.println();
            switch (typeTask)
            {
                case 1: task1(); break;
                case 2: task2(); break;
                default: task1(); break;
            }
            threads.get(id).busy = false;

            String queueTask = new String();
            try {
                synchronized (this) {
                    for (int j = 0; j < tasks.size(); j++) {
                        queueTask += ("taskType " + tasks.get(j).Taskid + " Client: " + tasks.get(j).idClient + " | ");
                    }
                }
                String executingTasks = new String();
                synchronized (this) {
                    for (int i = 0; i < threads.size(); i++) {
                        if (threads.get(i).busy) {
                            executingTasks += ("task type: " + threads.get(i).t.Taskid + " Client: " + threads.get(i).t.idClient + " | ");
                        }
                    }
                }

                for (int i = 0; i < dos.size(); i++) {
                    dos.get(i).writeUTF(queueTask);
                    dos.get(i).writeUTF(executingTasks);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // terminate thread
        }
    }

    void popTask ()
    {
        /*for (int i = 0; i<tasks.size(); i++)
        {
            System.out.println(tasks.get(i).Taskid);
        }
        System.out.println("****************");
        for(int i =0; i <threads.size(); i++) {
            System.out.println(threads.get(i).busy);
        }
        System.out.println("777777777777");*/
        synchronized (this) {
            int i =0;
            if (!tasks.isEmpty()) {
                while (i < N ) {
                    if (!threads.get(i).busy) break;
                    i++;
                }
                if (i == N) {
                    //System.out.println("error all are busy");
                    return;
                }
                Task task = new Task(0,3,0);
                try {
                    task = tasks.get(0);

                    System.out.println("got a task " + task.Taskid);
                }
                catch (java.lang.NullPointerException ex) {
                    System.out.println("Exceptionf ff " + tasks.size());
                    return;
                }


                tasks.remove(0);
                for (int j = 0; j < tasks.size(); j++) {
                    System.out.println(tasks.get(j).Taskid);
                }

                threads.get(i).t = task;
                threads.get(i).busy = true;
                count++;
                ExecuteThread ex = new ExecuteThread(i);
                ex.start();

            }
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

                    synchronized (this) {
                        for (int j = 0; j <tasks.size(); j++ )
                        {
                            queueTask += ("taskType " + tasks.get(j).Taskid + " Client: " + tasks.get(j).idClient + " | ");
                        }
                    }
                    String executingTasks = new String();
                    synchronized (this ) {
                        for (int i = 0; i < threads.size(); i++) {
                            if (threads.get(i).busy) {
                                executingTasks += ("task type: " + threads.get(i).t.Taskid + " Client: " + threads.get(i).t.idClient + " | ");
                            }
                        }
                    }
                    
                    for (int i = 0; i <dos.size(); i++) {
                        dos.get(i).writeUTF(queueTask);
                        dos.get(i).writeUTF(executingTasks);
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
                //System.out.println("popping");
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
            N = 4;
            threads = new ArrayList<>();
            for (int i = 0;i <N;i++) {
                threads.add(new Threads());
                threads.get(i).busy = false;
            }
            tasks = new ArrayList<>();
            MyThread firstThread = new MyThread();
            TaskManager taskManager = new TaskManager();
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
