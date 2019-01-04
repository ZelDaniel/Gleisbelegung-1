package org.gleisbelegung.ui;

import javafx.scene.layout.Pane;
import org.gleisbelegung.ui.lib.style.NodeWrapper;
import org.gleisbelegung.ui.lib.style.color.BackgroundColor;
import org.gleisbelegung.ui.lib.style.color.TextColor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class NodeWrapperTest {

    @Test public void testAddRemove() {
        NodeWrapper<Pane> nodeWrapper = new NodeWrapper<>(new Pane());
        BackgroundColor backgroundColor = new BackgroundColor("#303030");
        nodeWrapper.addStyle(backgroundColor);
        assertEquals("-fx-background-color: #303030",
                nodeWrapper.getNode().getStyle());

        nodeWrapper.addStyle(new TextColor("#505050"));
        assertTrue(nodeWrapper.getNode().getStyle()
                .contains("-fx-text-fill: #505050"));
        assertTrue(nodeWrapper.getNode().getStyle()
                .contains("-fx-background-color: #303030"));

        nodeWrapper.removeStyle(backgroundColor);
        assertEquals("-fx-text-fill: #505050",
                nodeWrapper.getNode().getStyle());
    }

    @Test public void testNoDoubledEntries() {
        NodeWrapper<Pane> nodeWrapper = new NodeWrapper<>(new Pane());
        nodeWrapper.addStyle(new BackgroundColor("#303030"));
        nodeWrapper.addStyle(new BackgroundColor("#505050"));
        assertEquals(1, nodeWrapper.getStyles().size());
        assertEquals("-fx-background-color: #505050",
                nodeWrapper.getNode().getStyle());

        nodeWrapper.addStyle(new TextColor("white"));
        nodeWrapper.addStyle(new TextColor("red"));
        assertEquals(2, nodeWrapper.getStyles().size());
        assertTrue(nodeWrapper.getNode().getStyle()
                .contains("-fx-background-color: #505050"));
        assertTrue(nodeWrapper.getNode().getStyle()
                .contains("-fx-text-fill: red"));
    }

    @Test public void testAddMultiple(){
        NodeWrapper<Pane> nodeWrapper = new NodeWrapper<>(new Pane());
        nodeWrapper.addStyles(new BackgroundColor("#303030"), new TextColor("red"));
        assertEquals(2, nodeWrapper.getStyles().size());
        assertTrue(nodeWrapper.getNode().getStyle()
                .contains("-fx-background-color: #303030"));
        assertTrue(nodeWrapper.getNode().getStyle()
                .contains("-fx-text-fill: red"));
    }
}
