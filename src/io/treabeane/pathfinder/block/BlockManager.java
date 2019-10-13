package io.treabeane.pathfinder.block;

import javafx.scene.layout.Pane;

import java.util.*;

public class BlockManager {

  private static final Set<Block> blockSet = new LinkedHashSet<>();

  public static Set<Block> getBlockSet() {
    return blockSet;
  }

  public static void registerBlock(Block block){
    getBlockSet().add(block);
  }

  public static Optional<Block> findBlock(Pane pane){
    return blockSet.stream().filter(block -> block.getPane().equals(pane)).findFirst();
  }

  public static Optional<Block> findBlock(double x, double y){
    return blockSet.stream().filter(block -> block.getX() == x && block.getY() == y).findFirst();
  }

  public static Set<Block> findBlocksByState(BlockState state){
    Set<Block> blockSet = new HashSet<>();
    getBlockSet().stream().filter(block -> block.getState().equals(state)).forEach(blockSet::add);
    return blockSet;
  }

  public static Set<Block> getAdjacentBlocks(Block block){
    return getAdjacentBlocks(block, true);
  }

  public static Set<Block> getAdjacentBlocks(Block block, boolean includeCorners){
    Set<Block> adjacent = new HashSet<>();

    BlockManager.findBlock(block.getX() + 1, block.getY()).ifPresent(adjacent::add);
    BlockManager.findBlock(block.getX() - 1, block.getY()).ifPresent(adjacent::add);
    BlockManager.findBlock(block.getX(), block.getY() + 1).ifPresent(adjacent::add);
    BlockManager.findBlock(block.getX(), block.getY() - 1).ifPresent(adjacent::add);

    if (includeCorners) {
      BlockManager.findBlock(block.getX() + 1, block.getY() + 1).ifPresent(adjacent::add);
      BlockManager.findBlock(block.getX() - 1, block.getY() + 1).ifPresent(adjacent::add);
      BlockManager.findBlock(block.getX() + 1, block.getY() - 1).ifPresent(adjacent::add);
      BlockManager.findBlock(block.getX() - 1, block.getY() - 1).ifPresent(adjacent::add);
    }

    return adjacent;
  }
}
