module osama.partone {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens osama.partone to javafx.fxml;
    exports osama.partone;
}