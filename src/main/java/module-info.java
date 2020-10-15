module launcher.main {
    requires kotlin.stdlib;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.swing;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.web;
    requires tornadofx;
    requires org.tinylog.api.kotlin;
    requires java.xml.bind;

    exports org.spectral.launcher.gui;
}