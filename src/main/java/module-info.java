module dvc.cabo {
    requires javafx.controls;
    requires javafx.fxml;

    opens dvc.cabo to javafx.fxml;
    exports dvc.cabo;
}