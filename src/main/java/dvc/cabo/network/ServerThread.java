package dvc.cabo.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread {

    private Socket socket = null;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isHost;

    public ServerThread(Socket socket) {
	super("ServerThread");
	this.socket = socket;

	try {
	    out = new ObjectOutputStream(socket.getOutputStream());
	    in = new ObjectInputStream(socket.getInputStream());
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

	    out.writeObject(new DataPacket("Connected.", null));
	    out.flush();

	    DataPacket s;
	    while (true) { // https://stackoverflow.com/questions/12684072/eofexception-when-reading-files-with-objectinputstream
		s = (DataPacket) in.readObject();
		if (s.info.startsWith("$DONE")) {
		    Server.next(s.game);
		}
	    }
	} catch (EOFException e1) {
	    System.out.println("Exception as control flow..");
	    // e1.printStackTrace();
	} catch (Exception e2) {
	    e2.printStackTrace();
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

    public ObjectOutputStream getOS() {
	return out;
    }

    public ObjectInputStream getIS() {
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

}
