import java.io.*;
import java.net.*;

public class SimpleClient {

    public static void main(String[] args) {

        // Server Host'
        final String username = "msaynevi-21";
        final String serverHost[] = { "tp-1a226-07.enst.fr",
                                      "tp-1a226-08.enst.fr",
                                      "tp-1a226-09.enst.fr" };
        final Integer nSplits = serverHost.length;

        Socket socketOfClient = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;

        try {
            String parentDir = "/tmp/" + username + "/";
            String filePath = "splits/";

            for(int i = 0; i < nSplits; i++) {
                String filename = "s" + Integer.toString(i) + ".txt";
                // Send a request to connect to the server is listening
                // on machine 'localhost' port 9999.
                socketOfClient = new Socket(serverHost[i], 3419);

                // Create output stream at the client (to send data to the server)
                bos = new BufferedOutputStream(socketOfClient.getOutputStream());
                DataInputStream dis = new DataInputStream(new FileInputStream(filePath + filename));

                // File file = new File(filePath + filename);

                try (DataOutputStream dos = new DataOutputStream(bos)) {
                    // Write the filepath on the server
                    dos.writeUTF(parentDir + filePath);
                    dos.writeUTF(filename);

                    // Write the file
                    while(dis.available() > 0)
                        dos.write(dis.readByte());

                    dos.close();
                    dis.close();
                }

                bos.close();
                //bis.close();

            }
            

            bos.close();
            bis.close();
            socketOfClient.close();

        } catch (UnknownHostException e) {
            System.err.println("Trying to connect to unknown host: " + e);
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }

}