package org.gleisbelegung.ui.lib.style;

import javafx.scene.Node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class NodeWrapper<T extends Node> {

    private Set<StyleInterface> styles;
    private T node;

    public NodeWrapper(T node) {
        this.node = node;
        styles = new HashSet<>();
    }

    public void addStyle(StyleInterface style) {
        removeIfExisting(style);
        styles.add(style);
        apply();
    }

    public void addStyles(StyleInterface... styles) {
        for (StyleInterface style : styles) {
            addStyle(style);
        }
    }

    public void removeStyle(StyleInterface style) {
        styles.remove(style);
        apply();
    }

    private String apply() {
        StringBuilder result = new StringBuilder();
        for (StyleInterface style : styles) {
            result.append(style.apply() + "; ");
        }

        //replace the last two chars of the string "; "
        result.replace(result.length() - 2, result.length(), "");

        node.setStyle(result.toString());
        return result.toString();
    }

    public Set<StyleInterface> getStyles() {
        return styles;
    }

    private void removeIfExisting(StyleInterface newStyle) {
        styles.removeIf(style -> style.getClass() == newStyle.getClass());
    }

    public T getNode() {
        return node;
    }
}
