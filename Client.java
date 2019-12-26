import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
                    panel.repaint();
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
        panel.setLayout( new FlowLayout( FlowLayout.CENTER ));
        panel.setVisible(true);
        panel.setEnabled(true);
        panel.setBackground(Color.YELLOW);
        setMinimumSize(new Dimension(500,500));
        setPreferredSize(new Dimension(1000, 800));
        panel.setMinimumSize(new Dimension(500,500));

        setVisible(true);
        MyThread  connection = new MyThread();
        x = 50;
        y = 50;

        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
                try {
                    dos.writeInt(x);
                    dos.writeInt(y);
                }
                catch (java.io.IOException ex)
                {
                    ex.printStackTrace();
                }

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
