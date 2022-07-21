package dvc.cabo.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static ArrayList<ServerThread> threads;

    public static void main(String[] args) throws IOException {

	if (args.length != 1) {
	    System.err.println("Supply a single argument - the PORT number.");
	    System.exit(1);
	}

	System.out.println("Server started.");
	int portNumber = Integer.parseInt(args[0]);
	threads = new ArrayList<>();
	int ids = 0;

	try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
	    while (true) {
		Socket acceptedSocket = serverSocket.accept();
		System.out.println("Connected: " + acceptedSocket.getInetAddress());
		new ServerThread(acceptedSocket, ids++).start();
	    }

	    // serverSocket.close();
	} catch (IOException e) {
	    System.err.println("Could not listen on port " + portNumber);
	    System.exit(-1);
	}

    }

    public static void broadcast(int id, String usrMsg) throws IOException {
	for(ServerThread thread : threads) {
	    if (thread.getID() != id) { // Don't broadcast to the same thread.
		String serverMsg = " [BROADCAST] " + usrMsg;
		thread.getOS().println(serverMsg);
		thread.getOS().flush();
	    }
	}

    }

}
