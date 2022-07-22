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
    private boolean isHost;
    private boolean isListening = false;

    public ServerThread(Socket socket) {
	super("ServerThread");
	this.socket = socket;

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
	    // Server.threads.add(this);
	    // if (Server.threads.indexOf(this) == 0) isHost = true;

	    out.println();
	    out.println("Connected!");
	    out.flush();

	    String s;
	    while ((s = in.readLine()) != null) {
		// if (isListening) {
		if (s.toLowerCase().equals("stop")) {
		    socket.close();
		} else {
		    System.out.println(s.toUpperCase());
		    out.println(s.toUpperCase());
		    out.flush();
		}
		// isListening = false;
		// }
	    }
	} catch (Exception e1) {
	    e1.printStackTrace();
	} finally {
	    Server.removeThread(this);
	    try {
		if (this.socket != null) socket.close();
		if (in != null) in.close();
		if (out != null) out.close();
	    } catch (IOException e2) {
		e2.printStackTrace();
	    }
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

    public boolean isHost() {
	return isHost;
    }

    public void setHost(boolean isHost) {
	this.isHost = isHost;
    }

    public boolean isListening() {
	return isListening;
    }

    public void setListening(boolean isListening) {
	this.isListening = isListening;
    }

}
