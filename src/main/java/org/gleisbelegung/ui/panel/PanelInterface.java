package org.gleisbelegung.ui.panel;

import javafx.scene.layout.Pane;


/**
 * Interface for Panels in order to bundle the most common actions
 */
public interface PanelInterface {

    /**
     * should create all visible elements is called by the {@link PanelController} after he is initialized
     *
     * @return JavaFX panel with whole content
     */
    Pane init();

    /**
     * set the with and height of the pane and all subelements here. <br>
     * This is an external Method because i.e. pane.getPrefHeight() is giving 0 in the init Method because its not added to the Stage at this moment
     */
    void setSizes();

    /**
     * called on onResize by PanelController
     *
     * @param width  new width
     * @param height new height
     */
    void onResize(double width, double height);

    /**
     * called if the panel is set to hidden (i.e. by the settings)
     */
    void onHide();

    /**
     * called if the panel is set to visible (i.e. by the settings)
     */
    void onVisible();
}
