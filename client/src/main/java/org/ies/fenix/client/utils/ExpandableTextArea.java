package org.ies.fenix.client.utils;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

public class ExpandableTextArea extends TextArea {

    private final int collapsedRows = 1;   // apariencia de TextField
    private final int expandedMaxRows = 100;
    private final int collapsedMaxVisibleRows = 3;

    public ExpandableTextArea() {
        super();
        init();
    }

    private void init() {
        setWrapText(true);

        // Apariencia inicial tipo TextField
        setPrefRowCount(collapsedRows);
        setMinHeight(Region.USE_PREF_SIZE);
        setMaxHeight(Region.USE_PREF_SIZE);

        // Auto-resize según contenido
        textProperty().addListener((obs, oldText, newText) -> autoResize());

        // Expandir al enfocar
        focusedProperty().addListener((obs, oldVal, hasFocus) -> {
            if (hasFocus) {
                setPrefRowCount(expandedMaxRows);
                autoResize();
            } else {
                // Si el texto es corto → 1 línea (TextField)
                // Si es largo → máximo 3 líneas visibles
                int lines = Math.min(countLines(getText()), collapsedMaxVisibleRows);
                setPrefRowCount(lines == 0 ? 1 : lines);
                autoResize();
            }
        });
    }

    private void autoResize() {
        setMinHeight(Region.USE_PREF_SIZE);
        setPrefHeight(Region.USE_COMPUTED_SIZE);
        setMaxHeight(Region.USE_PREF_SIZE);
    }

    private int countLines(String text) {
        if (text == null || text.isEmpty()) return 1;
        return text.split("\n").length;
    }
}
