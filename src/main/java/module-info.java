module com.triosstudent.csd214_lab2_johncarlo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.triosstudent.csd214_lab2_johncarlo to javafx.fxml;
    exports com.triosstudent.csd214_lab2_johncarlo;
}