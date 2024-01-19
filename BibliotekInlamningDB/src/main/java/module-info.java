module com.example.bibliotekinlamningdb {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.j;


    opens com.example.bibliotekinlamningdb to javafx.fxml;
    exports com.example.bibliotekinlamningdb;
}