package dvc.cabo.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import dvc.cabo.logic.Game;

public class Server {

    private static ServerSocket serverSocket;
    public static ArrayList<ServerThread> threads;

    public static Game game;

    public static void main(String[] args) throws IOException {

	if (args.length != 1) {
	    System.err.println("Supply a single argument - the PORT number.");
	    System.exit(1);
	}

	int portNumber = Integer.parseInt(args[0]);

	threads = new ArrayList<>();
	game = new Game();

	System.out.println();
	System.out.println("Cabo server started.");

	try {
	    serverSocket = new ServerSocket(portNumber);

	    while (threads.size() < 2) {
		Socket acceptedSocket = serverSocket.accept();
		System.out.println("   Connected: " + acceptedSocket.getInetAddress());
		ServerThread newThread = new ServerThread(acceptedSocket);
		threads.add(newThread);
		if (threads.indexOf(newThread) == 0) newThread.setHost(true);
		newThread.start();
	    }

	    for (ServerThread st : threads) {
		st.getOS().println("$GO!");
	    }
	} catch (IOException e) {
	    System.err.println("Could not listen on port " + portNumber);
	    System.exit(-1);
	}

    }

    public static boolean addPlayer(String name) {
	game.addPlayerByName(name);
	return true;
    }

    public static void removeThread(ServerThread thread) {
	System.out.println("Disconnected: " + thread.getSocket().getInetAddress());
	threads.remove(thread);

	if (threads.size() == 0) {
	    System.out.println("No connections remaining; shutting down...");
	    System.exit(0);
	} else if (!threads.get(0).isHost()) {
	    System.out.println("Host disconnected; new host: " + threads.get(0).getSocket().getInetAddress());
	    threads.get(0).setHost(true);
	}
    }

}
