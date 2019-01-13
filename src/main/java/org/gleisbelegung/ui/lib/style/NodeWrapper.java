package org.gleisbelegung.ui.lib.style;

import javafx.scene.Node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * easier styling of {@link Node Nodes}. Should be implemented for every used {@link Node}
 * @param <T> object implementing {@link Node}
 */
public class NodeWrapper<T extends Node> {

    private Set<StyleInterface> styles;
    private T node;

    /**
     * constructor
     * @param node {@link Node} Object
     */
    public NodeWrapper(T node) {
        this.node = node;
        styles = new HashSet<>();
    }

    /**
     * add and apply's the given style to the {@link Node} <br>
     * eventually replaces other styles {@link NodeWrapper#removeIfExisting(StyleInterface)}
     * @param style
     */
    public void addStyle(StyleInterface style) {
        removeIfExisting(style);
        styles.add(style);
        apply();
    }

    /**
     * add multiple styles to the given node. See {@link NodeWrapper#addStyle(StyleInterface)}
     * @param styles
     */
    public void addStyles(StyleInterface... styles) {
        for (StyleInterface style : styles) {
            addStyle(style);
        }
    }

    /**
     * removes a style from the given {@link Node}
     * @param style
     */
    public void removeStyle(StyleInterface style) {
        styles.remove(style);
        apply();
    }

    /**
     * apply's all {@link StyleInterface} and set the style of the give {@link Node}
     */
    private void apply() {
        StringBuilder result = new StringBuilder();
        for (StyleInterface style : styles) {
            result.append(style.apply() + "; ");
        }

        //replace the last two chars of the string "; "
        result.replace(result.length() - 2, result.length(), "");

        node.setStyle(result.toString());
    }

    /**
     *
     * @return list of styles
     */
    public Set<StyleInterface> getStyles() {
        return styles;
    }

    /**
     * replace objects of the same class if they exists <br>
     * If a {@link org.gleisbelegung.ui.lib.style.color.BackgroundColor BackgroundColor} exists and a new
     * {@link org.gleisbelegung.ui.lib.style.color.BackgroundColor BackgroundColor} is given, the existing one will be removed
     * @param newStyle
     */
    private void removeIfExisting(StyleInterface newStyle) {
        styles.removeIf(style -> style.getClass() == newStyle.getClass());
    }

    /**
     * returns the given {@link Node}
     * @return {@link Node}
     */
    public T getNode() {
        return node;
    }
}
