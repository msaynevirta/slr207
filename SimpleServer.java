import java.io.*;
import java.nio.file.FileSystems;
//import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleServer {
    public static void main(String args[]) {
        ServerSocket listener = null;
        //DataOutputStream dos = null;
        Socket socketOfServer = null;

        try {
            listener = new ServerSocket(3419);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }

        try {
            System.out.println("Server is waiting to accept user...");

            // Accept client connection request
            // Get new Socket at Server.    
            socketOfServer = listener.accept();
            System.out.println("Accept a client!");

            List<String> serverHost = new ArrayList<String>();
            
            String parentDir = null;
            String filename;

            /* -------- READING SPLITS -------- */
            BufferedInputStream bis = new BufferedInputStream(socketOfServer.getInputStream());
            
            try (DataInputStream dis = new DataInputStream(bis)) {
                // Read the parent directory on the server
                parentDir = dis.readUTF();

                // Create the directories
                File parent = new File(parentDir);
                parent.mkdirs();

                // Read the filename for the server
                filename = dis.readUTF();

                // Read serverHost array
                Integer serverHostLen = dis.readInt();
                for(Integer j = 0; j < serverHostLen; j++)
                    serverHost.add(dis.readUTF());

                File f = new File(parentDir + filename);

                // Read the file
                Files.copy(dis, f.toPath());

                dis.close();
            }

            if(bis.readAllBytes().equals("SHUFFLE".getBytes())) {
                System.out.println("Starting Shuffle");
            }

            bis.close();

            System.out.println("Wrote to " + parentDir + filename);

            

            // Receive sync signal for suffle?

            // Open sockets and output streams

            List<Socket> serverSockets = new ArrayList<Socket>();
            List<BufferedWriter> serverWriters = new ArrayList<BufferedWriter>();

            for(String serverAddress : serverHost) {
                Socket s = new Socket(serverAddress, 3419);
                serverSockets.add(s);
                serverWriters.add(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())));
            }

            /* -------- SHUFFLE -------- */
            Integer nServers = serverHost.size();

            Listener listenerThread = new Listener(nServers);
            listenerThread.start();

            BufferedReader bin = null;
            try {
                
                bin = new BufferedReader(new FileReader(parentDir + filename));
                String read = null;
                while ((read = bin.readLine()) != null) {
                    String[] splited = read.split("\\s+");
    
                    for (String word : splited) {
                        Integer hash = word.hashCode() & 0x7fffffff; // make it positive
                        serverWriters.get(hash % nServers).write(word);
                    }
                }
                for(BufferedWriter s : serverWriters) {
                    s.write(-1);
                }
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            } finally {
                try {
                    bin.close();
                } catch (Exception e) {
                }
            }


        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        
        System.out.println("Server stopped!");
    }
}

class Listener extends Thread {
    Integer serverCount;

    public Listener(Integer nServers) {
        serverCount = nServers;
    }

    public void run(String args[]) {
        ServerSocket listener = null;
        //DataOutputStream dos = null;
        Socket socketOfServer = null;

        try {
            listener = new ServerSocket(3419);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        
        try {
            System.out.println("Server is waiting to accept user...");

            // Accept client connection request
            // Get new Socket at Server.    
            socketOfServer = listener.accept();

            // Put the word counts into a hashmap, needs separate thread (after shuffle)
            Map<String, Integer> hashMap = new HashMap<>();

            BufferedReader bin = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
            try {
                String word = null;
                Integer finished = 0;
                do {
                    word = bin.readLine();
                    if(word.isEmpty()) {
                        finished++;
                    }
                } while(finished.equals(serverCount));
                
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            } finally {
                try {
                    bin.close();
                } catch (Exception e) {
                }
            }
            System.out.println("Server stopped!");
            
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}