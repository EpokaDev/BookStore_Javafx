module application.bookstore {
    exports application.bookstore.controllers;

    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens application.bookstore to java.base;
    exports application.bookstore;
    exports application.bookstore.views;
    exports application.bookstore.models;
}