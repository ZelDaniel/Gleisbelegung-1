package org.gleisbelegung.ui.lib.panel;

/**
 * this is the class you should extend when you create a new Panel.
 * This class provides useful helper methods
 */
public abstract class Panel implements PanelInterface{
    private boolean visibility = true;

    /**
     * @param visibility true to set visible, false to set invisible
     */
    public void setVisible(boolean visibility){
        if(visibility) onVisible();
        else onHide();
    }

    /**
     *
     * @return true if the panel is visible, else false
     */
    public boolean isVisible() {
        return visibility;
    }
}
