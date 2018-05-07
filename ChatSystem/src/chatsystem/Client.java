package chatsystem;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.*;

public class Client implements Runnable {
    public static String username = "Guest"+Math.round(Math.random()*100000);
    static BufferedWriter writer;
    static BufferedReader reader;
    public static JLabel lr;
    public static JTextField tfu;
    public static JTextField tfp;
    public static JFrame f;
    public static Thread t;
    
    public static void ThreadM(){
        Client ss = new Client();
        t = new Thread(ss);
        t.start();
    }
    
    public static void main(String[] args) {
        try {
            f = new JFrame(username);
            JPanel p1 = new JPanel();
            JPanel p2 = new JPanel();
            JPanel p3 = new JPanel();
            JLabel lu = new JLabel("username: ");
            JLabel lp = new JLabel("password: ");
            lr = new JLabel("Otherwise continue as guest.");
            JLabel lr2 = new JLabel();
            tfu = new JTextField();
            tfp = new JTextField();
            JButton bl = new JButton("Login");
            JButton br = new JButton("Register");
            JTextField tf = new JTextField();
            JButton b1 = new JButton("Join");
            
            f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            f.setSize(350,350);
            f.setContentPane(p2);
            tf.setText("Enter group name here and press enter to join");
            p3.setLayout(new GridLayout(4,2,0,50));
            p3.add(lu);
            p3.add(tfu);
            p3.add(lp);
            p3.add(tfp);
            p3.add(lr);
            p3.add(lr2);
            p3.add(bl);
            p3.add(br);
            p1.setLayout(new BorderLayout());
            p1.add(tf, BorderLayout.CENTER);
            p1.add(b1, BorderLayout.EAST);
            p2.setLayout(new BorderLayout());
            p2.add(p3, BorderLayout.CENTER);
            p2.add(p1, BorderLayout.SOUTH);

            f.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    if (JOptionPane.showConfirmDialog(f,"Are you sure to close Chat application?","Confirm",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            });
            bl.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseReleased(MouseEvent e) {
                    try {
                        Socket socketClient = new Socket("localhost",5555);
                        writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
                        reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                        writer.write("laccount");
                        writer.write("\r\n");
                        writer.write(tfu.getText());
                        writer.write("\r\n");
                        writer.write(tfp.getText());
                        writer.write("\r\n");
                        writer.flush();
                        ThreadM();
                    } catch(IOException ex) {}
                }
            });
            br.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseReleased(MouseEvent e) {
                    try {
                        Socket socketClient = new Socket("localhost",5555);
                        writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
                        reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                        writer.write("account");
                        writer.write("\r\n");
                        writer.write(tfu.getText());
                        writer.write("\r\n");
                        writer.write(tfp.getText());
                        writer.write("\r\n");
                        writer.flush();
                        ThreadM();
                    } catch(IOException ex) {}
                }
            });
            tf.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (!tf.getText().equals("")) {
                            Chat c = new Chat(username, tf.getText());
                            Thread t1 = new Thread(c);
                            t1.start();
                            tf.setText("");
                        }
                    }
                }
            });
            tf.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseReleased(MouseEvent e) {
                    if(tf.getText().equals("Enter group name here and press enter to join")) {
                        tf.setText("");
                    }
                }
            });
            b1.addActionListener((ActionEvent ev) -> {
                if (!tf.getText().equals("")) {
                    Chat c = new Chat(username, tf.getText());
                    Thread t1 = new Thread(c);
                    t1.start();
                    tf.setText("");
                }
            });
            f.setVisible(true);
        } catch(HeadlessException e) {}
    }
    
    @Override
    public void run() {
        try {
            String type;
            while ((type = reader.readLine()) != null) {
                if (type.equals("Account created!")) {
                    try {
                        username = tfu.getText();
                        f.setTitle(username);
                    } catch(Exception exx) {}
                }
                if (type.equals("Logged in!")) {
                    try {
                        username = tfu.getText();
                        f.setTitle(username);
                    } catch(Exception exx) {}
                }
                lr.setText(type);
            }
        } catch(IOException ex) {}
    }
}