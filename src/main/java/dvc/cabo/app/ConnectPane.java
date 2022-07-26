package dvc.cabo.app;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ConnectPane extends VBox {

    private final TextField ipField;
    private final TextField portField;
    private final TextField nameField;
    private final Button connectButton;

    public ConnectPane() {
	setAlignment(Pos.CENTER);
	setSpacing(20);

	HBox ipBox = new HBox();
	Label ipLabel = new Label("IP: ");
	ipField = new TextField("localhost");
	ipBox.getChildren().setAll(ipLabel, ipField);
	ipBox.setAlignment(Pos.CENTER);

	HBox portBox = new HBox();
	Label portLabel = new Label("PORT: ");
	portField = new TextField("9966");
	portBox.getChildren().setAll(portLabel, portField);
	portBox.setAlignment(Pos.CENTER);

	HBox nameBox = new HBox();
	Label nameLabel = new Label("NAME: ");
	nameField = new TextField();
	nameBox.getChildren().setAll(nameLabel, nameField);
	nameBox.setAlignment(Pos.CENTER);

	connectButton = new Button("Connect!");

	getChildren().setAll(ipBox, portBox, nameBox, connectButton);
    }

    public TextField getNameField() {
	return nameField;
    }

    public TextField getIpField() {
	return ipField;
    }

    public TextField getPortField() {
	return portField;
    }

    public Button getConnectButton() {
	return connectButton;
    }

}
