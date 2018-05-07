package server;
import java.io.*;
import java.net.*;
import java.util.*;

class Server implements Runnable {
    Socket connectionSocket;
    public boolean newClientConnected = true;
    public static ArrayList<Info> info = new ArrayList<>();
    public Server(Socket s) {
        try {
            connectionSocket = s;
        } catch(Exception e) {System.out.println("Error 1: "+e);}
    }
    @Override
    public void run() {
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
            newClientConnected = true;
            for(int i=0;i<info.size();i++){
                if(info.get(i).writer == writer){
                    newClientConnected = false;
                    break;
                }
            }
            if(newClientConnected){
                info.add(new Info(writer));
            }
            while (true) {
                String type = reader.readLine().trim();
                if(type.equals("public")){
                    String message = reader.readLine().trim();
                    String group = reader.readLine().trim();
                    String user = reader.readLine().trim();
                    for(int i=0;i<info.size();i++){
                        if(info.get(i).writer == writer){
                            info.get(i).setGroup(group);
                            info.get(i).setUser(user);
                            break;
                        }
                    }
                    System.out.println("("+group+") "+user+": "+message);
                    for (int i=0;i<info.size();i++) {
                        try {
                            if (group.equals(info.get(i).group)) {
                                BufferedWriter bw = (BufferedWriter) info.get(i).writer;
                                bw.write("public");
                                bw.write("\r\n");
                                bw.write(user + ": " + message);
                                bw.write("\r\n");
                                bw.flush();
                            }
                        } catch(IOException e) {
                            System.out.println("Detected offline user ("+info.get(i).user+"), removing from active users...");
                            info.remove(i);
                        }
                    }
                } else if(type.equals("laccount")) {
                    BufferedWriter wr = null;
                    for(int i=0;i<info.size();i++){
                        if(info.get(i).writer == writer){
                            wr = writer;
                            break;
                        }
                    }
                    String uname = reader.readLine().trim();
                    String upass = reader.readLine().trim();
                    File accounts = new File("accounts.txt");
                    boolean exists = accounts.exists();
                    if(exists){
                        try (BufferedReader br = new BufferedReader(new FileReader("accounts.txt"))) {
                            String line;
                            boolean exist = false;
                            while ((line = br.readLine()) != null) {
                                if(line.equals(uname+":"+upass)){
                                    wr.write("Logged in!");
                                    wr.write("\r\n");
                                    wr.flush();
                                    exist = true;
                                    break;
                                }
                            }
                            if(!exist){
                                wr.write("Account does not exist.");
                                wr.write("\r\n");
                                wr.flush();
                            }
                        } catch(Exception ex) {}
                    } else {
                        wr.write("Account does not exist.");
                        wr.write("\r\n");
                        wr.flush();
                    }
                } else if(type.equals("account")) {
                    BufferedWriter wr = null;
                    for(int i=0;i<info.size();i++){
                        if(info.get(i).writer == writer){
                            wr = writer;
                            break;
                        }
                    }
                    String uname = reader.readLine().trim();
                    String upass = reader.readLine().trim();
                    File accounts = new File("accounts.txt");
                    boolean exists = accounts.exists();
                    if(exists){
                        if (uname.equals("") || uname.contains(" ") || upass.equals("") || upass.contains(" ")) {
                            wr.write("You cannot create account with that info.");
                            wr.write("\r\n");
                            wr.flush();
                        } else {
                            try (BufferedReader br = new BufferedReader(new FileReader("accounts.txt"))) {
                                String line;
                                boolean exist = false;
                                while ((line = br.readLine()) != null) {
                                    String uname2 = line.split(":")[0];
                                    if(uname2.equals(uname)){
                                        wr.write("Account already exists.");
                                        wr.write("\r\n");
                                        wr.flush();
                                        exist = true;
                                        break;
                                    }
                                }
                                if(!exist){
                                    try (FileWriter fw = new FileWriter("accounts.txt", true);
                                        BufferedWriter bw = new BufferedWriter(fw);
                                        PrintWriter out = new PrintWriter(bw)) {
                                        out.println(uname+":"+upass);
                                        wr.write("Account created!");
                                        wr.write("\r\n");
                                        wr.flush();
                                    } catch (IOException ex) {}
                                }
                            } catch(Exception ex) {}
                        }
                    } else {
                        try (Writer writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("accounts.txt"), "utf-8"))) {
                            if (uname.equals("") || uname.contains(" ") || upass.equals("") || upass.contains(" ")) {
                                wr.write("You cannot create account with that info.");
                                wr.write("\r\n");
                                wr.flush();
                            } else {
                                writer2.write(uname+":"+upass+"\n");
                                wr.write("Account created!");
                                wr.write("\r\n");
                                wr.flush();
                            }
                        } catch(Exception ex){}
                    }
                } else if(type.equals("private")) {
                    String toUser = reader.readLine().trim();
                    String message = reader.readLine().trim();
                    String fromUser = reader.readLine().trim();
                    for (int i=0;i<info.size();i++) {
                        try {
                            if(toUser.equals(info.get(i).user)){
                                BufferedWriter bw = (BufferedWriter) info.get(i).writer;
                                bw.write("private");
                                bw.write("\r\n");
                                bw.write(fromUser);
                                bw.write("\r\n");
                                bw.write(message);
                                bw.write("\r\n");
                                bw.write(toUser);
                                bw.write("\r\n");
                                bw.flush();
                            }
                        } catch(IOException e) {System.out.println("Error 3: "+e);}
                    }
                }
            }
        } catch(IOException e) {
            System.out.println("A user has disconnected.");
        }
    }
    public static void main(String argv[]) {
        try {
            System.out.println("Server connecting...");
            ServerSocket mysocket = new ServerSocket(5555);
            System.out.println("Server connected.");
            while (true) {
                Socket sock = mysocket.accept();
                Server server = new Server(sock);
                Thread serverThread = new Thread(server);
                serverThread.start();
            }
        } catch(IOException e) {System.out.println("Error 5: "+e);}
    }
}

class Info {
    BufferedWriter writer;
    String user;
    String group;
    Info(BufferedWriter writer, String user, String group) {
        this.writer = writer;
        this.user = user;
        this.group = group;
    }
    
    Info(BufferedWriter writer, String user) {
        this.writer = writer;
        this.user = user;
    }
    
    Info(BufferedWriter writer) {
        this.writer = writer;
    }
    
    public void setGroup(String group){
        this.group = group;
    }
    
    public void setUser(String user){
        this.user = user;
    }
}