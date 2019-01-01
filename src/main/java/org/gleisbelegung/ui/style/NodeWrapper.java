package org.gleisbelegung.ui.style;

import javafx.scene.Node;

import java.util.Set;

public class NodeWrapper {
  Set<StyleInterface> styles;
  Node node;

  public NodeWrapper(Node node){
    this.node = node;
  }

  public void addStyle(StyleInterface style){
    styles.add(style);
  }

  public void removeStyle(StyleInterface style){
    styles.remove(style);
  }

  public String apply(){
    StringBuilder result = new StringBuilder();
    for (StyleInterface style : styles){
      result.append(style.apply());
    }

    node.setStyle(result.toString());
    return result.toString();
  }
}
