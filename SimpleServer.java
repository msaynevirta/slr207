import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SimpleServer {

    public static void main(String args[]) {
        ServerSocket listener = null;
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

            List<String> serverHost = new ArrayList<String>();
            String parentDir = null;
            String splitfile = null;

            /* --------------- SPLITS & SERVER ADDRESSES --------------- */
            DataInputStream dis = new DataInputStream(bis);
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(parentDir + splitfile));

            parentDir = dis.readUTF();
            splitfile = dis.readUTF();

            Integer serverHostLen = dis.readInt();
            for(int j = 0; j < serverHostLen; j++)
                serverHost.add(dis.readUTF());

            while(dis.available() > 0)
                dos.write(dis.readByte());

            System.out.println("paska");
            //dis.close();
            dos.close();
            dis.close();

            /* --------------- SHUFFLE --------------- */
            System.out.println("Server is waiting to start SHUFFLE...");

            // Accept client connection request
            // Get new Socket at Server.
            socketOfServer = listener.accept();
            System.out.println("Accept a client!");

            /* --------------- SERVER CONNECTIONS --------------- */
            List<Socket> serverSockets = new ArrayList<Socket>();
            try {
                for(String address : serverHost) {
                    serverSockets.add(new Socket(address, 3419));
                }
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }

            // Open buffered input and output streams

            bis.close();
            socketOfServer.close();

            

        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        System.out.println("Server stopped!");
    }
}

class Listener extends Thread {
    public void run() {
        System.out.println("Started listener");
    }
}