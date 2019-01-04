package org.gleisbelegung.ui.lib.style.color;

import org.gleisbelegung.ui.lib.style.StyleInterface;

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
