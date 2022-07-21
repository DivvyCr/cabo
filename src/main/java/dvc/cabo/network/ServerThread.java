package dvc.cabo.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread {

    private Socket socket = null;
    private PrintWriter out;
    private BufferedReader in;
    private int id;

    public ServerThread(Socket socket, int id) {
	super("ServerThread");
	this.socket = socket;
	this.id = id;

	try {
	    out = new PrintWriter(socket.getOutputStream(), true);
	    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	} catch (IOException e1) {
	    try {
		if (this.socket != null) socket.close();
		if (in != null) in.close();
		if (out != null) out.close();
	    } catch (IOException e2) {
		e2.printStackTrace();
	    }
	}
    }

    public void run() {

	try {
	    Server.threads.add(this);

	    out.println("Hello, world!");

	    while (true) {
		Server.broadcast(id, in.readLine());
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public PrintWriter getOS() {
	return out;
    }

    public BufferedReader getIS() {
	return in;
    }

    public Socket getSocket() {
	return socket;
    }

    public int getID() {
	return id;
    }

}
