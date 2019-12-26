import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends JFrame {
    static int port = 3124;
    static InetAddress ip = null;
    Socket cs;
    DataInputStream dis;
    DataOutputStream dos;
    Integer x, y;
    JPanel panel;
    JButton startTask1, startTask2;
    JLabel exQueue, waitQueue;
    String executingTasks, tasksQueue;
    //RoundRectangle2D circle;

    class  ReadThread extends  Thread {
        @Override
        public void run () {
            try {
                while (true) {
                    /*Integer myx = dis.readInt();
                    Integer myy = dis.readInt();
                    x = myx;
                    y = myy;
                    System.out.println(x);
                    System.out.println(y);*/
                    tasksQueue = dis.readUTF();
                    executingTasks = dis.readUTF();

                    exQueue.setText("Executing tasks " + executingTasks);
                    waitQueue.setText("Queue of tasks " + tasksQueue);
                    //panel.repaint();
                }
            }
            catch (java.io.IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
    class  MyThread extends Thread {
        @Override
        public void run () {
            try {
                ip = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                cs = new Socket(ip, port);
                System.out.println("Client start!");
                dis = new DataInputStream(cs.getInputStream());
                dos = new DataOutputStream(cs.getOutputStream());
                ReadThread rt = new ReadThread();
                rt.start();
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
            }
        }
    }



    public  Client () {

        panel = new JPanel() {
            @Override
            public  void paint (Graphics g) {
                super.paint(g);
                //setBackground(Color.YELLOW);
                //g.setColor( Color.yellow);
                g.fillRoundRect(x, y, 5, 5, 5, 5);
            }
        };
        startTask1 = new JButton("Start task 1");
        startTask1.setLocation(10,10);
        startTask1.setVisible(true);
        startTask1.setEnabled(true);
        startTask1.setMinimumSize(new Dimension(40,20));

        startTask2 = new JButton("Start task 2");
        startTask2.setLocation(10,40);
        startTask2.setVisible(true);
        startTask2.setEnabled(true);
        startTask2.setMinimumSize(new Dimension(40,20));

        exQueue = new JLabel();
        exQueue.setBackground(Color.CYAN);
        waitQueue = new JLabel();
        exQueue.setVisible(true);

        panel.setLayout( new FlowLayout( FlowLayout.LEFT ));
        panel.setVisible(true);
        panel.setEnabled(true);


        setMinimumSize(new Dimension(500,500));
        setPreferredSize(new Dimension(1000, 800));
        panel.setMinimumSize(new Dimension(500,500));
        panel.add(startTask1);
        panel.add(startTask2);

        panel.add(exQueue);
        panel.add(waitQueue);
        panel.setBackground(Color.YELLOW);
        setVisible(true);
        MyThread  connection = new MyThread();
        x = 50;
        y = 50;
        startTask1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.writeInt(1);
                    repaint();
                }
                catch (IOException ex) {
                    ex.printStackTrace();;
                }
            }
        });
        startTask2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.writeInt(2);
                }
                catch (IOException ex) {
                    ex.printStackTrace();;
                }
            }
        });

        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
                /*try {
                    dos.writeInt(x);
                    dos.writeInt(y);
                }
                catch (java.io.IOException ex)
                {
                    ex.printStackTrace();
                }*/

                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        add(panel);
        connection.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->new Client());
    }
}
