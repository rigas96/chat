package chatsystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.text.*;

public class Chat implements Runnable {
    public JTextField tx;
    public JEditorPane ta;
    public String username;
    BufferedWriter writer;
    BufferedReader reader;
    
    public Chat(String l, String GroupName) {
        username = l;
        JFrame f = new JFrame(GroupName);
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        ta = new JEditorPane("text/html", "");
        tx = new JTextField();
        tx.setText("Send a message to enter chat room.");
        JScrollPane scrollpane = new JScrollPane(ta);
        JButton b1 = new JButton("Send");
        
        f.setSize(350,350);
        //ta.setLineWrap(true);
        ta.setEditable(false);
        p1.setLayout(new BorderLayout());
        p1.add(tx, BorderLayout.CENTER);
        p1.add(b1, BorderLayout.EAST);
        p2.setLayout(new BorderLayout());
        p2.add(p1, BorderLayout.SOUTH);
        p2.add(scrollpane);
        scrollpane.setVisible(true);
        f.setContentPane(p2);
        
        try {
            Socket socketClient = new Socket("localhost",5555);
            writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
        } catch(IOException e) {}
        
        tx.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseReleased(MouseEvent e) {
                if(tx.getText().equals("Send a message to enter chat room.")) {
                    tx.setText("");
                }
            }
        });
        tx.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!tx.getText().equals("") && !tx.getText().contains("/")){
                        try {
                            writer.write("public");
                            writer.write("\r\n");
                            writer.write(tx.getText());
                            writer.write("\r\n");
                            writer.write(GroupName);
                            writer.write("\r\n");
                            writer.write(username);
                            writer.write("\r\n");
                            writer.flush();
                        } catch(IOException ex) {}
                        tx.setText("");
                    }
                    if(tx.getText().contains("/")){
                        String name = tx.getText().split(" ",2)[0].substring(1, tx.getText().split(" ",2)[0].length());
                        String message = tx.getText().split(" ",2)[1];
                        try {
                            writer.write("private");
                            writer.write("\r\n");
                            writer.write(name);
                            writer.write("\r\n");
                            writer.write(message);
                            writer.write("\r\n");
                            writer.write(username);
                            writer.write("\r\n");
                            writer.flush();
                        } catch(IOException ex) {}
                        tx.setText("");
                    }
                }
            }
        });
        b1.addActionListener((ActionEvent ev) -> {
            if (!tx.getText().equals("") && !tx.getText().contains("/")){
                try {
                    writer.write("public");
                    writer.write("\r\n");
                    writer.write(tx.getText());
                    writer.write("\r\n");
                    writer.write(GroupName);
                    writer.write("\r\n");
                    writer.write(username);
                    writer.write("\r\n");
                    writer.flush();
                } catch(IOException ex) {}
                tx.setText("");
            }
            if(tx.getText().contains("/")){
                String name = tx.getText().split(" ",2)[0].substring(1, tx.getText().split(" ",2)[0].length());
                String message = tx.getText().split(" ",2)[1];
                try {
                    writer.write("private");
                    writer.write("\r\n");
                    writer.write(name);
                    writer.write("\r\n");
                    writer.write(message);
                    writer.write("\r\n");
                    writer.write(username);
                    writer.write("\r\n");
                    writer.flush();
                } catch(IOException ex) {}
                tx.setText("");
            }
        });
        f.setVisible(true);
    }
    @Override
    public void run() {
        try {
            String type;
            while ((type = reader.readLine()) != null) {
                if (type.equals("public")) {
                    String message = reader.readLine();
                    try {
                        Document doc = ta.getDocument();
                        doc.insertString(doc.getLength(), message+"\n", null);
                    } catch(BadLocationException e) {}
                    int len = ta.getDocument().getLength();
                    ta.setCaretPosition(len);
                }
                if (type.equals("private")) {
                    String fromUser = reader.readLine();
                    String message = reader.readLine();
                    String toUser = reader.readLine();
                    if(username.equals(toUser)){
                        try {
                            SimpleAttributeSet attributes = new SimpleAttributeSet();
                            attributes.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
                            attributes.addAttribute(StyleConstants.CharacterConstants.Background, new Color(220,220,220));
                            Document doc = ta.getDocument();
                            doc.insertString(doc.getLength(), "(PM) "+fromUser+": "+message+"\n", attributes);
                        } catch(BadLocationException e) {}
                        int len = ta.getDocument().getLength();
                        ta.setCaretPosition(len);
                    }
                }
            }
        } catch(IOException e) {}
    }
}