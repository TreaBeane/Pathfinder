package io.treabeane.pathfinder.block;

import javafx.scene.shape.Rectangle;

public class Block {

  private final Rectangle pane;
  private final int x, y;
  private BlockState state = BlockState.EMPTY;

  private Block parentBlock;

  public Block(Rectangle pane, int x, int y) {
    this.pane = pane;
    this.x = x;
    this.y = y;
  }

  public Rectangle getPane() {
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

  public Block getParentBlock() {
    return parentBlock;
  }

  public void setParentBlock(Block parentBlock) {
    this.parentBlock = parentBlock;
  }
}
