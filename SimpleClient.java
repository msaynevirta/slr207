import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleClient {

    public static void main(String[] args) {

        // Server Host
        final String username = "make";
        //final String username = "msaynevi-21";
        final String serverHost[] = { "localhost" };
        // final String serverHost[] = { "tp-1a226-07.enst.fr",
        //                               "tp-1a226-08.enst.fr",
        //                               "tp-1a226-11.enst.fr" };
        final Integer nSplits = serverHost.length;

        try {
            String parentDir = "/tmp/" + username + "/";
            String filePath = "splits/";

            for(int i = 0; i < nSplits; i++) {
                Socket socket = new Socket(serverHost[i], 3419);
                String filename = "s" + Integer.toString(i) + ".txt";

                // Create output stream at the client (to send data to the server)
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(new FileInputStream(filePath + filename));

                // Write the filepath and filename on the server
                dos.writeUTF(parentDir);
                dos.writeUTF(filename);

                System.out.println("wrote filenames to " + serverHost[i]);
                
                // Write serverHost array
                dos.writeInt(serverHost.length);
                for (int j = 0; j < serverHost.length; j++) {
                    dos.writeUTF(serverHost[j]);
                }

                // Write the file
                while(dis.available() > 0)
                    dos.write(dis.readByte());

                System.out.println("wrote file s" + Integer.toString(i) + ".txt to " + serverHost[i]);

                dis.close();
                dos.close();
                socket.close();
            }

            // Tell the servers that 
            for (String address : serverHost) {
                Socket socket = new Socket(address, 3419);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("SHUFFLE");
                dos.close();
                socket.close();
            }

        } catch (UnknownHostException e) {
            System.err.println("Trying to connect to unknown host: " + e);
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }

}