module org.ies.fenix.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires spring.security.crypto;

    opens org.ies.fenix.client to javafx.fxml;
    exports org.ies.fenix.client;
}