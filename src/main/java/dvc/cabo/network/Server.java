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
    private static int activeThreadIdx;

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

	    while (threads.size() < 3) {
		Socket acceptedSocket = serverSocket.accept();
		System.out.println("   Connected: " + acceptedSocket.getInetAddress());
		ServerThread newThread = new ServerThread(acceptedSocket);
		threads.add(newThread);
		if (threads.indexOf(newThread) == 0) newThread.setHost(true);
		newThread.start();
	    }

	    // Capacity reached, start the game:
	    for (ServerThread st : threads) game.addPlayerByName(Integer.toString(threads.indexOf(st)));
	    game.startGame();

	    // Start games for clients:
	    activeThreadIdx = -1;
	    for (ServerThread st : threads) st.getOS().writeObject(new DataPacket("$GO:" + threads.indexOf(st), game));
	    next(game);
	} catch (IOException e) {
	    System.err.println("Could not listen on port " + portNumber);
	    e.printStackTrace();
	    System.exit(-1);
	}

    }

    public static void next(Game newGame) {
	game = newGame; // FULLY TRUST CLIENT, for no reason.. (ie. needs to change)
	activeThreadIdx = (activeThreadIdx + 1) % threads.size();
	try {
	    for (int i = 0; i < threads.size(); i++) {
		if (i == activeThreadIdx) {
		    threads.get(activeThreadIdx).getOS().writeObject(new DataPacket("$NEXT", game));
		} else {
		    threads.get(i).getOS().writeObject(new DataPacket("$WAIT", game));
		}
	    }
	} catch (IOException e) { e.printStackTrace(); }
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
