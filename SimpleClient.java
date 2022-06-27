import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleClient {

    public static void main(String[] args) {

        // Server Host
        final String username = "msaynevi-21";
        final String serverHost[] = { "tp-1a226-07.enst.fr",
                                      "tp-1a226-08.enst.fr",
                                      "tp-1a226-09.enst.fr" };
        final Integer nSplits = serverHost.length;

        List<Socket> socketsOfClient = new ArrayList<Socket>();
        BufferedOutputStream bos = null;

        try {
            String sourceDir = "splits/";
            String destDir = "/tmp/" + username + "/";

            // Open sockets
            for(String serverAddress : serverHost) {
                socketsOfClient.add(new Socket(serverAddress, 3419));
            }

            for(Integer i = 0; i < nSplits; i++) {
                String filename = "s" + Integer.toString(i) + ".txt";

                // Create output stream at the client (to send data to the server)
                bos = new BufferedOutputStream(socketsOfClient.get(i).getOutputStream());
                DataInputStream dis = new DataInputStream(new FileInputStream(sourceDir + filename));

                // Transfer files and server address list

                try (DataOutputStream dos = new DataOutputStream(bos)) {
                    // Write the filepath and filename on the server
                    dos.writeUTF(destDir);
                    dos.writeUTF(filename);

                    // Write serverHost array
                    dos.writeInt(serverHost.length);
                    for(int j = 0; j < serverHost.length; j++) {
                        dos.writeUTF(serverHost[j]);
                    }
                        
                    // Write the file
                    while(dis.available() > 0)
                        dos.write(dis.readByte());

                    dos.close();
                    dis.close();
                    
                }
                bos.close();
            }

            for(Socket cs : socketsOfClient) {
                bos = new BufferedOutputStream(cs.getOutputStream());
                bos.write("SHUFFLE".getBytes());
                bos.close();
                cs.close();
            }

        } catch (UnknownHostException e) {
            System.err.println("Trying to connect to unknown host: " + e);
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }

}