package io.github.kwisatzx.lastepoch.gui.views;

import javafx.scene.control.Label;
import javafx.scene.paint.Paint;

import java.util.Locale;

public class RootView {
    private final Label leftBottomLabel;
    private final Label rightBottomLabel;

    public RootView(Label leftBottomLabel, Label rightBottomLabel) {
        this.leftBottomLabel = leftBottomLabel;
        this.rightBottomLabel = rightBottomLabel;
    }

    public void setBottomRightText(String text) {
        if (text.toLowerCase(Locale.ROOT).contains("error")) rightBottomLabel.setTextFill(Paint.valueOf("#ff3333"));
        else rightBottomLabel.setTextFill(Paint.valueOf("#000000"));
        rightBottomLabel.setText(text);
    }

    public void setBottomLeftText(String text) {
        leftBottomLabel.setText(text);
    }
}
