package dvc.cabo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import dvc.cabo.app.ConnectPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TestClient extends Application {
    // This is used as a testing ground for `Main.java`
    Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(Stage stage) {
	ConnectPane cp = new ConnectPane();
	cp.getConnectButton().setOnMouseClicked(event -> {
		connect("localhost", 9966); // connect(ipField.getText(), Integer.parseInt(portField.getText()));

		try {
		    String res;
		    while ((res = in.readLine()) != null) {
			if (res.startsWith("$GO")) {
			    stage.setScene(new Scene(new StackPane(), 2560, 1440));
			    break;
			}
		    }
		} catch (IOException e) { e.printStackTrace(); }
	    });

	stage.setTitle("Test Client");
	stage.setScene(new Scene(cp, 500, 500));
	stage.show();
    }

    private void send(String str) {
	out.println(str);
	out.flush();
    }

    private void connect(String ipAddress, int portNumber) {
	try {
	    socket = new Socket(ipAddress, portNumber);
	    out = new PrintWriter(socket.getOutputStream(), true);
	    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	    // Thread connectionThread = new Thread() {
	    //	    public void run() {
	    //		String fromServer;

	    //		try {
	    //		    while ((fromServer = in.readLine()) != null) {
	    //			System.out.println(fromServer);
	    //		    }
	    //		} catch (Exception e1) {
	    //		    e1.printStackTrace();
	    //		    try {
	    //			socket.close();
	    //		    } catch (IOException e2) { e2.printStackTrace(); }
	    //		}
	    //	    }
	    //	};
	    // connectionThread.setDaemon(true); // Ensures that it exits with the parent thread (?)
	    // connectionThread.start();
	} catch (UnknownHostException e) {
	    System.err.println("Unknown host: " + ipAddress);
	    System.exit(1);
	} catch (IOException e) {
	    System.err.println("Unable to connect to " + ipAddress);
	    System.exit(1);
	}
    }

    public static void main(String[] args) throws IOException {
	if (args.length != 2) {
	    System.err.println("Usage: java EchoClient <host name> <port number>");
	    System.exit(1);
	}

	launch(args);
    }
}
