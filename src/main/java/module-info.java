module com.antont.parserui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires java.ws.rs;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;

    opens com.antont.parserui to javafx.fxml;
    exports com.antont.parserui;
    exports com.antont.parserui.model;
    opens com.antont.parserui.model to javafx.fxml;
    exports com.antont.parserui.controller;
    opens com.antont.parserui.controller to javafx.fxml;
}