module org.ies.fenix.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires java.net.http;

    requires com.fasterxml.jackson.databind;
    requires org.ies.fenix.common;

    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens org.ies.fenix.client to javafx.fxml;
    opens org.ies.fenix.client.controller to javafx.fxml;

    exports org.ies.fenix.client;
}