module osama.parttwoservertcp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens osama.parttwoservertcp to javafx.fxml;
    exports osama.parttwoservertcp;
}