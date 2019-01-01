package org.gleisbelegung.ui.style.styles;

import javafx.scene.Node;
import org.gleisbelegung.ui.style.StyleInterface;

public class BackgroundColor implements StyleInterface {
  String color;

  public BackgroundColor(String color){
    this.color = color;
  }

  @Override
  public String apply() {
    return "-fx-background-color: " + color;
  }
}
