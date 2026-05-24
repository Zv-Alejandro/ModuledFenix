package org.ies.fenix.client.utils;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

public class ExpandableTextArea extends TextArea {

    private static final double DEFAULT_WIDTH = 300;

    private final int collapsedRows = 1;
    private final int expandedMaxRows = 5;
    private final int collapsedMaxVisibleRows = 2;

    public ExpandableTextArea() {
        super();
        init();
    }

    private void init() {

        setWrapText(true);

        // ===== UNIFIED WIDTH =====
        setPrefWidth(DEFAULT_WIDTH);
        setMinWidth(DEFAULT_WIDTH);
        setMaxWidth(DEFAULT_WIDTH);

        // ===== HEIGHT BEHAVIOR =====
        setPrefRowCount(collapsedRows);
        setMinHeight(Region.USE_PREF_SIZE);
        setMaxHeight(Region.USE_PREF_SIZE);

        // ===== AUTO RESIZE =====
        textProperty().addListener((obs, oldText, newText) -> autoResize());

        focusedProperty().addListener((obs, oldVal, hasFocus) -> {

            if (hasFocus) {
                setPrefRowCount(expandedMaxRows);
                autoResize();
            } else {
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