package io.treabeane.pathfinder.block;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class Block {

  private final Pane pane;
  private final int x, y;
  private BlockState state = BlockState.EMPTY;

  private int gCost, hCost;

  public Block(Pane pane, int x, int y) {
    this.pane = pane;
    this.x = x;
    this.y = y;
  }

  public Pane getPane() {
    return pane;
  }

  public BlockState getState() {
    return state;
  }

  public void setState(BlockState state) {
    this.state = state;
    getPane().setId(state.getId());
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getGCost() {
    return gCost;
  }

  public void setGCost(int gCost) {
    this.gCost = gCost;
  }

  public int getHCost() {
    return hCost;
  }

  public void setHCost(int hCost) {
    this.hCost = hCost;
  }

  public int getFCost() {
    return getGCost() + getHCost();
  }

  public void setTag(String s) {
    getPane().getChildren().filtered(node -> node instanceof Label)
            .forEach(node -> {
              if (node instanceof Label){
                ((Label) node).setText(s);
              }
            });
  }
}
