import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServerProgram {

    public static void main(String args[]) {

        final String username = "msaynevi-21";
        
        ServerSocket listener = null;
        BufferedInputStream bis = null;
        DataOutputStream dos = null;
        Socket socketOfServer = null;

        // Try to open a server socket on port 9999
        // Note that we can't choose a port less than 1023 if we are not
        // privileged users (root)

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
            String parentDir = null;
            String filename = "s.txt";

            // Open buffered input and output streams
            bis = new BufferedInputStream(socketOfServer.getInputStream());
            
            try (DataInputStream dis = new DataInputStream(bis)) {
                // Read the parent directory on the server
                parentDir = dis.readUTF();

                // Create the directories
                File parent = new File(parentDir);
                parent.mkdirs();

                // Read the filename for the server
                filename = dis.readUTF();

                dos = new DataOutputStream(new FileOutputStream(parentDir + filename));

                // Write the file
                while(dis.available() > 0)
                    dos.write(dis.readByte());

                dos.close();
                dis.close();
            }

            bis.close();

            System.out.println("Wrote to " + parentDir + filename);

        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        System.out.println("Server stopped!");
    }
}